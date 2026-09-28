package tech.kayys.syirkah.accounting.domain.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void create_document_with_initial_version() {
        var id = DocumentId.generate();
        var meta = DocumentMetadata.of("contract.pdf", "application/pdf", 1024);
        var doc = new Document(id, DocumentType.CONTRACT, DocumentClassification.CONFIDENTIAL, meta, "hash123", "s3://bucket/key");

        assertEquals(DocumentStatus.ACTIVE, doc.status());
        assertEquals(1, doc.versions().size());
        assertEquals(1, doc.latestVersion().versionNumber());
        assertEquals("hash123", doc.latestVersion().sha256Hash());
    }

    @Test
    void add_version_increments_version_number() {
        var id = DocumentId.generate();
        var meta = DocumentMetadata.of("invoice.pdf", "application/pdf", 500);
        var doc = new Document(id, DocumentType.INVOICE, DocumentClassification.INTERNAL, meta, "hash1", "s3://key1");

        var v2 = doc.addVersion("hash2", "s3://key2", DocumentMetadata.of("invoice_v2.pdf", "application/pdf", 600));

        assertEquals(2, doc.versions().size());
        assertEquals(2, v2.versionNumber());
        assertEquals("hash2", doc.latestVersion().sha256Hash());
    }

    @Test
    void archive_prevents_new_versions() {
        var id = DocumentId.generate();
        var meta = DocumentMetadata.of("doc.txt", "text/plain", 10);
        var doc = new Document(id, DocumentType.GENERAL, DocumentClassification.PUBLIC, meta, "h1", "s3://1");

        doc.archive();
        assertEquals(DocumentStatus.ARCHIVED, doc.status());
        assertThrows(IllegalStateException.class, () -> doc.addVersion("h2", "s3://2", meta));
    }
}
