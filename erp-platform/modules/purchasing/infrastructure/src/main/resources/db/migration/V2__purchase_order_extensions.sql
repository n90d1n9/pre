-- Purchase Order Approvals
CREATE TABLE IF NOT EXISTS po_approvals (
    id UUID PRIMARY KEY,
    purchase_order_id VARCHAR(255) NOT NULL,
    workflow_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    current_step_index INTEGER DEFAULT 0,
    rejection_count INTEGER DEFAULT 0,
    rejected_by VARCHAR(255),
    rejection_reason TEXT,
    rejected_at TIMESTAMP,
    completed_at TIMESTAMP,
    completed_by VARCHAR(255),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Approval Records
CREATE TABLE IF NOT EXISTS approval_records (
    id UUID PRIMARY KEY,
    approval_id UUID NOT NULL,
    approver_id VARCHAR(255) NOT NULL,
    approver_name VARCHAR(255) NOT NULL,
    step_index INTEGER NOT NULL,
    decision VARCHAR(20) NOT NULL,
    notes TEXT,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (approval_id) REFERENCES po_approvals(id)
);

-- Approval History
CREATE TABLE IF NOT EXISTS approval_history (
    id UUID PRIMARY KEY,
    approval_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (approval_id) REFERENCES po_approvals(id)
);

-- Receiving Records
CREATE TABLE IF NOT EXISTS receiving_records (
    id UUID PRIMARY KEY,
    purchase_order_id VARCHAR(255) NOT NULL,
    vendor_id VARCHAR(255) NOT NULL,
    receiving_number VARCHAR(100) NOT NULL UNIQUE,
    receiving_date TIMESTAMP NOT NULL,
    inspection_status VARCHAR(20) DEFAULT 'PENDING',
    received_by VARCHAR(255) NOT NULL,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Received Items
CREATE TABLE IF NOT EXISTS received_items (
    id UUID PRIMARY KEY,
    receiving_record_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(50),
    ordered_quantity INTEGER NOT NULL,
    received_quantity INTEGER NOT NULL,
    rejected_quantity INTEGER DEFAULT 0,
    uom VARCHAR(20),
    condition VARCHAR(20),
    notes TEXT,
    FOREIGN KEY (receiving_record_id) REFERENCES receiving_records(id)
);

-- Inspection Records
CREATE TABLE IF NOT EXISTS inspection_records (
    id UUID PRIMARY KEY,
    receiving_record_id UUID NOT NULL,
    inspector_id VARCHAR(255) NOT NULL,
    inspector_name VARCHAR(255) NOT NULL,
    item_index INTEGER NOT NULL,
    passed BOOLEAN NOT NULL,
    defect_type VARCHAR(100),
    defect_description TEXT,
    inspection_date TIMESTAMP NOT NULL,
    notes TEXT,
    FOREIGN KEY (receiving_record_id) REFERENCES receiving_records(id)
);

-- Invoice Matching (3-Way Match)
CREATE TABLE IF NOT EXISTS invoice_matches (
    id UUID PRIMARY KEY,
    purchase_order_id VARCHAR(255) NOT NULL,
    receiving_record_id VARCHAR(255) NOT NULL,
    vendor_invoice_id VARCHAR(255) NOT NULL,
    vendor_invoice_number VARCHAR(100) NOT NULL,
    invoice_amount DECIMAL(19,2) NOT NULL,
    po_amount DECIMAL(19,2),
    received_amount DECIMAL(19,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    matched_by VARCHAR(255),
    matched_at TIMESTAMP,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Match Discrepancies
CREATE TABLE IF NOT EXISTS match_discrepancies (
    id UUID PRIMARY KEY,
    match_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    resolution TEXT,
    FOREIGN KEY (match_id) REFERENCES invoice_matches(id)
);

-- Indexes
CREATE INDEX idx_po_approvals_status ON po_approvals(status);
CREATE INDEX idx_po_approvals_po ON po_approvals(purchase_order_id);

CREATE INDEX idx_receiving_records_po ON receiving_records(purchase_order_id);
CREATE INDEX idx_receiving_records_date ON receiving_records(receiving_date);
CREATE INDEX idx_receiving_records_status ON receiving_records(inspection_status);

CREATE INDEX idx_invoice_matches_po ON invoice_matches(purchase_order_id);
CREATE INDEX idx_invoice_matches_invoice ON invoice_matches(vendor_invoice_id);
CREATE INDEX idx_invoice_matches_status ON invoice_matches(status);