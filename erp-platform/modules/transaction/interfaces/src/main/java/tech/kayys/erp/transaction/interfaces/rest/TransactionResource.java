package tech.kayys.erp.transaction.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.transaction.application.api.TransactionService;
import tech.kayys.erp.transaction.application.api.command.*;
import tech.kayys.erp.transaction.domain.identifier.TransactionId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for transaction processing.
 */
@Path("/api/v1/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Transaction API", description = "Payment transaction processing")
public class TransactionResource {

    @Inject
    TransactionService transactionService;

    @POST
    @Path("/payments")
    @Operation(summary = "Process a payment")
    public CompletionStage<Response> processPayment(@Valid ProcessPaymentRequest request) {
        ProcessPaymentCommand command = ProcessPaymentCommand.builder()
            .orderId(request.getOrderId())
            .orderNumber(request.getOrderNumber())
            .customerId(request.getCustomerId())
            .customerEmail(request.getCustomerEmail())
            .amount(request.getAmount())
            .currencyCode(request.getCurrencyCode())
            .taxAmount(request.getTaxAmount())
            .tipAmount(request.getTipAmount())
            .paymentMethod(request.getPaymentMethod())
            .lastFourDigits(request.getLastFourDigits())
            .cardType(request.getCardType())
            .token(request.getToken())
            .expiryMonth(request.getExpiryMonth())
            .expiryYear(request.getExpiryYear())
            .cardholderName(request.getCardholderName())
            .fingerprint(request.getFingerprint())
            .isTokenized(request.isTokenized())
            .merchantId(request.getMerchantId())
            .terminalId(request.getTerminalId())
            .channelId(request.getChannelId())
            .channelType(request.getChannelType())
            .build();

        return transactionService.processPayment(command)
            .thenApply(result -> Response
                .created(URI.create("/api/v1/transactions/" + result.transactionId().getValue()))
                .entity(result)
                .build()
            );
    }

    @POST
    @Path("/payments/authorize")
    @Operation(summary = "Authorize a payment")
    public CompletionStage<Response> authorizePayment(@Valid AuthorizePaymentRequest request) {
        AuthorizePaymentCommand command = AuthorizePaymentCommand.builder()
            .orderId(request.getOrderId())
            .customerId(request.getCustomerId())
            .amount(request.getAmount())
            .currencyCode(request.getCurrencyCode())
            .paymentMethod(request.getPaymentMethod())
            .lastFourDigits(request.getLastFourDigits())
            .cardType(request.getCardType())
            .token(request.getToken())
            .expiryMonth(request.getExpiryMonth())
            .expiryYear(request.getExpiryYear())
            .cardholderName(request.getCardholderName())
            .fingerprint(request.getFingerprint())
            .isTokenized(request.isTokenized())
            .merchantId(request.getMerchantId())
            .terminalId(request.getTerminalId())
            .channelId(request.getChannelId())
            .channelType(request.getChannelType())
            .build();

        return transactionService.authorizePayment(command)
            .thenApply(result -> Response
                .created(URI.create("/api/v1/transactions/" + result.transactionId().getValue()))
                .entity(result)
                .build()
            );
    }

    @POST
    @Path("/payments/capture")
    @Operation(summary = "Capture an authorized payment")
    public CompletionStage<Response> capturePayment(@Valid CapturePaymentRequest request) {
        CapturePaymentCommand command = new CapturePaymentCommand(
            request.getProcessorTransactionId(),
            request.getAmount(),
            request.getCurrencyCode()
        );

        return transactionService.capturePayment(command)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.CONFLICT)
                    .entity(throwable.getCause().getMessage())
                    .build();
            });
    }

    @POST
    @Path("/payments/refund")
    @Operation(summary = "Process a refund")
    public CompletionStage<Response> refundPayment(@Valid RefundPaymentRequest request) {
        RefundPaymentCommand command = new RefundPaymentCommand(
            request.getProcessorTransactionId(),
            request.getAmount(),
            request.getCurrencyCode(),
            request.getReason()
        );

        return transactionService.refundPayment(command)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.CONFLICT)
                    .entity(throwable.getCause().getMessage())
                    .build();
            });
    }

    @POST
    @Path("/payments/void")
    @Operation(summary = "Void a transaction")
    public CompletionStage<Response> voidTransaction(@Valid VoidTransactionRequest request) {
        VoidTransactionCommand command = new VoidTransactionCommand(
            request.getProcessorTransactionId(),
            request.getReason()
        );

        return transactionService.voidTransaction(command)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.CONFLICT)
                    .entity(throwable.getCause().getMessage())
                    .build();
            });
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get transaction details")
    public CompletionStage<Response> getTransaction(@PathParam("id") UUID id) {
        TransactionId transactionId = TransactionId.of(id);
        GetTransactionQuery query = new GetTransactionQuery(transactionId);
        return transactionService.getTransaction(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/by-reference/{reference}")
    @Operation(summary = "Get transaction by reference")
    public CompletionStage<Response> getTransactionByReference(@PathParam("reference") String reference) {
        return transactionService.getTransactionByReference(reference)
            .thenApply(transaction -> {
                if (transaction == null) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.ok(transaction).build();
            });
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search transactions")
    public CompletionStage<Response> searchTransactions(
            @QueryParam("customerId") String customerId,
            @QueryParam("orderId") UUID orderId,
            @QueryParam("status") String status,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        SearchTransactionsQuery query = new SearchTransactionsQuery(
            customerId,
            orderId,
            status != null ? TransactionStatus.valueOf(status) : null,
            fromDate != null ? Instant.parse(fromDate) : null,
            toDate != null ? Instant.parse(toDate) : null,
            page,
            size
        );
        return transactionService.searchTransactions(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/statistics")
    @Operation(summary = "Get transaction statistics")
    public CompletionStage<Response> getTransactionStatistics(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("merchantId") String merchantId) {
        TransactionStatisticsQuery query = new TransactionStatisticsQuery(
            fromDate != null ? Instant.parse(fromDate) : null,
            toDate != null ? Instant.parse(toDate) : null,
            merchantId
        );
        return transactionService.getTransactionStatistics(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    @Path("/batch/settle")
    @Operation(summary = "Process batch settlement")
    public CompletionStage<Response> processBatchSettlement(@Valid BatchSettlementRequest request) {
        BatchSettlementCommand command = new BatchSettlementCommand(
            request.getMerchantId(),
            request.getBatchDate()
        );
        return transactionService.processBatchSettlement(command)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // Request DTOs
    public static class ProcessPaymentRequest {
        private UUID orderId;
        private String orderNumber;
        private String customerId;
        private String customerEmail;
        private String amount;
        private String currencyCode;
        private String taxAmount;
        private String tipAmount;
        private PaymentInstrument.PaymentMethod paymentMethod;
        private String lastFourDigits;
        private String cardType;
        private String token;
        private String expiryMonth;
        private String expiryYear;
        private String cardholderName;
        private String fingerprint;
        private boolean isTokenized;
        private String merchantId;
        private String terminalId;
        private String channelId;
        private String channelType;

        // Getters and setters
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public String getTaxAmount() { return taxAmount; }
        public void setTaxAmount(String taxAmount) { this.taxAmount = taxAmount; }
        public String getTipAmount() { return tipAmount; }
        public void setTipAmount(String tipAmount) { this.tipAmount = tipAmount; }
        public PaymentInstrument.PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentInstrument.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getLastFourDigits() { return lastFourDigits; }
        public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }
        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getExpiryMonth() { return expiryMonth; }
        public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
        public String getExpiryYear() { return expiryYear; }
        public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
        public String getCardholderName() { return cardholderName; }
        public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }
        public String getFingerprint() { return fingerprint; }
        public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
        public boolean isTokenized() { return isTokenized; }
        public void setTokenized(boolean tokenized) { isTokenized = tokenized; }
        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
        public String getTerminalId() { return terminalId; }
        public void setTerminalId(String terminalId) { this.terminalId = terminalId; }
        public String getChannelId() { return channelId; }
        public void setChannelId(String channelId) { this.channelId = channelId; }
        public String getChannelType() { return channelType; }
        public void setChannelType(String channelType) { this.channelType = channelType; }
    }

    public static class AuthorizePaymentRequest {
        private UUID orderId;
        private String customerId;
        private String amount;
        private String currencyCode;
        private PaymentInstrument.PaymentMethod paymentMethod;
        private String lastFourDigits;
        private String cardType;
        private String token;
        private String expiryMonth;
        private String expiryYear;
        private String cardholderName;
        private String fingerprint;
        private boolean isTokenized;
        private String merchantId;
        private String terminalId;
        private String channelId;
        private String channelType;

        // Getters and setters
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public PaymentInstrument.PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentInstrument.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getLastFourDigits() { return lastFourDigits; }
        public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }
        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getExpiryMonth() { return expiryMonth; }
        public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
        public String getExpiryYear() { return expiryYear; }
        public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
        public String getCardholderName() { return cardholderName; }
        public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }
        public String getFingerprint() { return fingerprint; }
        public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
        public boolean isTokenized() { return isTokenized; }
        public void setTokenized(boolean tokenized) { isTokenized = tokenized; }
        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
        public String getTerminalId() { return terminalId; }
        public void setTerminalId(String terminalId) { this.terminalId = terminalId; }
        public String getChannelId() { return channelId; }
        public void setChannelId(String channelId) { this.channelId = channelId; }
        public String getChannelType() { return channelType; }
        public void setChannelType(String channelType) { this.channelType = channelType; }
    }

    public static class CapturePaymentRequest {
        private String processorTransactionId;
        private String amount;
        private String currencyCode;

        public String getProcessorTransactionId() { return processorTransactionId; }
        public void setProcessorTransactionId(String processorTransactionId) { this.processorTransactionId = processorTransactionId; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    public static class RefundPaymentRequest {
        private String processorTransactionId;
        private String amount;
        private String currencyCode;
        private String reason;

        public String getProcessorTransactionId() { return processorTransactionId; }
        public void setProcessorTransactionId(String processorTransactionId) { this.processorTransactionId = processorTransactionId; }
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class VoidTransactionRequest {
        private String processorTransactionId;
        private String reason;

        public String getProcessorTransactionId() { return processorTransactionId; }
        public void setProcessorTransactionId(String processorTransactionId) { this.processorTransactionId = processorTransactionId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class BatchSettlementRequest {
        private String merchantId;
        private String batchDate;

        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
        public String getBatchDate() { return batchDate; }
        public void setBatchDate(String batchDate) { this.batchDate = batchDate; }
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/grocery-pos/domain</module>
    <module>modules/grocery-pos/application</module>
    <module>modules/grocery-pos/infrastructure</module>
    <module>modules/grocery-pos/interfaces</module>

    <module>modules/kiosk/domain</module>
    <module>modules/kiosk/application</module>
    <module>modules/kiosk/infrastructure</module>
    <module>modules/kiosk/interfaces</module>

    <module>modules/omnichannel/domain</module>
    <module>modules/omnichannel/application</module>
    <module>modules/omnichannel/infrastructure</module>
    <module>modules/omnichannel/interfaces</module>

    <module>modules/transaction/domain</module>
    <module>modules/transaction/application</module>
    <module>modules/transaction/infrastructure</module>
    <module>modules/transaction/interfaces</module>
</modules>