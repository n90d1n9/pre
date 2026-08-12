package tech.kayys.erp.billing.core.webhook;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Webhook retry engine for billing events.
 * Implements exponential backoff for failed webhooks.
 */
@ApplicationScoped
public class WebhookRetryEngine {

    private static final Logger log = LoggerFactory.getLogger(WebhookRetryEngine.class);

    @Inject
    WebhookRepository webhookRepository;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final int[] RETRY_DELAYS = {30, 60, 120, 300, 600, 1800, 3600, 7200, 14400, 28800};

    /**
     * Scheduled retry of failed webhooks.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public Uni<Void> retryFailedWebhooks() {
        log.info("Processing webhook retries");

        return webhookRepository.findFailedWebhooks()
            .onItem()
            .transformToUni(webhooks -> {
                if (webhooks.isEmpty()) {
                    return Uni.createFrom().voidItem();
                }

                List<Uni<Void>> retryOperations = webhooks.stream()
                    .filter(w -> w.getRetryCount() < w.getMaxRetries())
                    .map(webhook -> retryWebhook(webhook))
                    .collect(java.util.stream.Collectors.toList());

                return Uni.combine()
                    .all()
                    .unis(retryOperations)
                    .combinedWith(results -> null);
            });
    }

    private Uni<Void> retryWebhook(WebhookEvent webhook) {
        int retryCount = webhook.getRetryCount();
        int delaySeconds = RETRY_DELAYS[Math.min(retryCount, RETRY_DELAYS.length - 1)];

        // Check if enough time has passed since last attempt
        if (webhook.getLastAttemptAt() != null) {
            Instant nextRetryAt = webhook.getLastAttemptAt().plusSeconds(delaySeconds);
            if (Instant.now().isBefore(nextRetryAt)) {
                return Uni.createFrom().voidItem();
            }
        }

        log.info("Retrying webhook: {} (attempt {}/{})", 
            webhook.getId(), retryCount + 1, webhook.getMaxRetries());

        return sendWebhook(webhook)
            .onItem()
            .transformToUni(response -> {
                if (response.success) {
                    webhook.markDelivered(response.statusCode, response.body);
                    return webhookRepository.save(webhook)
                        .onItem()
                        .transform(v -> null);
                } else {
                    webhook.markFailed(response.statusCode, response.body);
                    return webhookRepository.save(webhook)
                        .onItem()
                        .transform(v -> null);
                }
            });
    }

    private Uni<WebhookResponse> sendWebhook(WebhookEvent webhook) {
        return Uni.createFrom()
            .completionStage(() -> {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(webhook.getEndpointUrl()))
                    .header("Content-Type", "application/json")
                    .header("X-Webhook-Id", webhook.getId())
                    .header("X-Webhook-Event", webhook.getEventType())
                    .header("X-Webhook-Retry", String.valueOf(webhook.getRetryCount()))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(webhook.getPayload()))
                    .build();

                return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        int statusCode = response.statusCode();
                        String body = response.body();
                        boolean success = statusCode >= 200 && statusCode < 300;
                        return new WebhookResponse(success, statusCode, body);
                    })
                    .exceptionally(throwable -> {
                        log.error("Webhook send failed", throwable);
                        return new WebhookResponse(false, 500, throwable.getMessage());
                    });
            });
    }

    /**
     * Webhook response record.
     */
    public record WebhookResponse(
        boolean success,
        int statusCode,
        String body
    ) {}
}