package tech.kayys.syirkah.support.application.notification;

import java.util.concurrent.CompletionStage;

public interface NotificationPort {

    CompletionStage<NotificationResult> send(NotificationRequest request);
}
