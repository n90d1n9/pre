package tech.kayys.erp.groceries.application.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GroceryReceipt(
        UUID receiptId,
        String transactionNumber,
        Instant timestamp,
        List<GroceryReceiptLine> items,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        String paymentMethod,
        String cashierId
) {}

record GroceryReceiptLine(
        String productId,
        String productName,
        Double quantity,
        String unit,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        boolean isWeightBased
) {}
