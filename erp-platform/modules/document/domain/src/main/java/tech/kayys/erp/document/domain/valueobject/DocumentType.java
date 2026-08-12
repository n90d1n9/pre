package tech.kayys.erp.document.domain.valueobject;

/**
 * Types of documents.
 */
public enum DocumentType {
    INVOICE("Invoice"),
    PURCHASE_ORDER("Purchase Order"),
    SALES_ORDER("Sales Order"),
    CONTRACT("Contract"),
    AGREEMENT("Agreement"),
    POLICY("Policy"),
    PROCEDURE("Procedure"),
    REPORT("Report"),
    PRESENTATION("Presentation"),
    SPREADSHEET("Spreadsheet"),
    IMAGE("Image"),
    VIDEO("Video"),
    AUDIO("Audio"),
    ARCHIVE("Archive"),
    EMAIL("Email"),
    LETTER("Letter"),
    MEMO("Memo"),
    FORM("Form"),
    TEMPLATE("Template"),
    OTHER("Other");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}