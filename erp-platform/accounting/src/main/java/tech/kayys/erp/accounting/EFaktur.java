
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * e-Faktur aggregate root.
 * Implements Indonesian electronic tax invoice (e-Faktur) generation.
 * Required for B2B transactions (PKP to PKP).
 */
public final class EFaktur extends AggregateRoot<EFakturId> {
    
    private static final long serialVersionUID = 1L;
    
    // e-Faktur format: [Kode Transaksi][Tahun][Bulan][Tanggal][NomorUrut]
    // Example: 010-01-2024-12-31-000001
    private String fakturNumber;
    private String transactionId;
    private String invoiceId;
    private String customerId;
    private String customerName;
    private String customerNPWP; // Tax ID
    private String sellerNPWP;
    private String sellerName;
    private String sellerAddress;
    private Instant fakturDate;
    private PPNConfig ppnConfig;
    private Money baseAmount;
    private Money ppnAmount;
    private Money totalAmount;
    private String currencyCode;
    private String transactionType; // 01 = B2B, 02 = B2C, etc.
    private String status; // DRAFT, GENERATED, SENT, APPROVED, REJECTED
    private String xmlData;
    private String qrCodeData;
    private String approvalCode;
    private Instant approvalDate;
    private String rejectionReason;
    private String generatedBy;
    private boolean active;

    private EFaktur(EFakturId id) {
        super(id);
        this.status = "DRAFT";
        this.active = true;
    }

    private EFaktur() {
        super();
    }

    /**
     * Factory method to create a new e-Faktur.
     */
    public static EFaktur create(
            EFakturId id,
            String transactionId,
            String invoiceId,
            String customerId,
            String customerName,
            String customerNPWP,
            String sellerNPWP,
            String sellerName,
            Money baseAmount,
            PPNConfig ppnConfig,
            String currencyCode) {
        EFaktur faktur = new EFaktur(id);
        faktur.transactionId = transactionId;
        faktur.invoiceId = invoiceId;
        faktur.customerId = customerId;
        faktur.customerName = customerName;
        faktur.customerNPWP = customerNPWP;
        faktur.sellerNPWP = sellerNPWP;
        faktur.sellerName = sellerName;
        faktur.baseAmount = baseAmount;
        faktur.ppnConfig = ppnConfig;
        faktur.currencyCode = currencyCode;
        faktur.fakturDate = Instant.now();
        faktur.ppnAmount = baseAmount.multiply(ppnConfig.getRate());
        faktur.totalAmount = baseAmount.add(faktur.ppnAmount);
        faktur.transactionType = customerNPWP != null && !customerNPWP.isEmpty() ? "01" : "02";
        return faktur;
    }

    /**
     * Generates the e-Faktur XML and QR Code.
     */
    public void generate() {
        if (status.equals("GENERATED")) {
            return;
        }
        
        // Generate faktur number
        this.fakturNumber = generateFakturNumber();
        
        // Generate XML data
        this.xmlData = generateXML();
        
        // Generate QR Code data (for verification)
        this.qrCodeData = generateQRCodeData();
        
        this.status = "GENERATED";
        addEvent("e-Faktur Generated", "Faktur: " + fakturNumber);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private String generateFakturNumber() {
        // Format: [Kode Transaksi][Tahun][Bulan][Tanggal][NomorUrut]
        // Example: 010-2024-12-31-000001
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateFormatter.format(fakturDate);
        
        // Generate sequential number (in production, use database sequence)
        String sequentialNumber = String.format("%06d", getId().hashCode() % 1000000);
        
        return transactionType + "-" + 
               date.substring(0, 4) + "-" + 
               date.substring(5, 7) + "-" + 
               date.substring(8, 10) + "-" + 
               sequentialNumber;
    }

    private String generateXML() {
        // In production, this would generate the full e-Faktur XML
        // according to the DJP (Direktorat Jenderal Pajak) specification
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<Faktur>\n");
        xml.append("  <KodeTransaksi>").append(transactionType).append("</KodeTransaksi>\n");
        xml.append("  <NomorFaktur>").append(fakturNumber).append("</NomorFaktur>\n");
        xml.append("  <TanggalFaktur>").append(fakturDate).append("</TanggalFaktur>\n");
        xml.append("  <PKP_Pembeli>\n");
        xml.append("    <NPWP>").append(customerNPWP).append("</NPWP>\n");
        xml.append("    <Nama>").append(customerName).append("</Nama>\n");
        xml.append("  </PKP_Pembeli>\n");
        xml.append("  <DasarPengenaanPajak>")
           .append(baseAmount.getAmount())
           .append("</DasarPengenaanPajak>\n");
        xml.append("  <PPN>").append(ppnAmount.getAmount()).append("</PPN>\n");
        xml.append("  <TotalTagihan>")
           .append(totalAmount.getAmount())
           .append("</TotalTagihan>\n");
        xml.append("</Faktur>");
        
        return xml.toString();
    }

    private String generateQRCodeData() {
        // QR Code data for e-Faktur verification
        // Format: [NPWP Seller][NPWP Buyer][Faktur Number][Total Amount]
        return sellerNPWP + "|" + 
               customerNPWP + "|" + 
               fakturNumber + "|" + 
               totalAmount.getAmount().toString();
    }

    /**
     * Sends the e-Faktur to the buyer and DJP.
     */
    public void send() {
        if (!status.equals("GENERATED")) {
            throw new IllegalStateException("e-Faktur must be generated before sending");
        }
        this.status = "SENT";
        addEvent("e-Faktur Sent", "Sent to buyer and DJP");
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the e-Faktur (from DJP response).
     */
    public void approve(String approvalCode) {
        this.approvalCode = approvalCode;
        this.approvalDate = Instant.now();
        this.status = "APPROVED";
        addEvent("e-Faktur Approved", "Approval code: " + approvalCode);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the e-Faktur (from DJP response).
     */
    public void reject(String reason) {
        this.rejectionReason = reason;
        this.status = "REJECTED";
        addEvent("e-Faktur Rejected", "Reason: " + reason);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void addEvent(String action, String details) {
        // Event tracking for audit
    }

    // Getters
    public String getFakturNumber() { return fakturNumber; }
    public String getTransactionId() { return transactionId; }
    public String getInvoiceId() { return invoiceId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerNPWP() { return customerNPWP; }
    public String getSellerNPWP() { return sellerNPWP; }
    public String getSellerName() { return sellerName; }
    public String getSellerAddress() { return sellerAddress; }
    public Instant getFakturDate() { return fakturDate; }
    public PPNConfig getPpnConfig() { return ppnConfig; }
    public Money getBaseAmount() { return baseAmount; }
    public Money getPpnAmount() { return ppnAmount; }
    public Money getTotalAmount() { return totalAmount; }
    public String getCurrencyCode() { return currencyCode; }
    public String getTransactionType() { return transactionType; }
    public String getStatus() { return status; }
    public String getXmlData() { return xmlData; }
    public String getQrCodeData() { return qrCodeData; }
    public String getApprovalCode() { return approvalCode; }
    public Instant getApprovalDate() { return approvalDate; }
    public String getRejectionReason() { return rejectionReason; }
    public String getGeneratedBy() { return generatedBy; }
    public boolean isActive() { return active; }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "EFaktur{" +
                "id=" + getId() +
                ", fakturNumber='" + fakturNumber + '\'' +
                ", customerNPWP='" + customerNPWP + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}