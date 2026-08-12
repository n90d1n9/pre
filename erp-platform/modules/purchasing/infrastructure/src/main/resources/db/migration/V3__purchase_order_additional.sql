-- Purchase Requisitions
CREATE TABLE IF NOT EXISTS purchase_requisitions (
    id UUID PRIMARY KEY,
    requisition_number VARCHAR(50) NOT NULL UNIQUE,
    department_id VARCHAR(255) NOT NULL,
    department_name VARCHAR(255),
    requested_by VARCHAR(255) NOT NULL,
    requested_by_name VARCHAR(255),
    cost_center VARCHAR(50),
    project_code VARCHAR(50),
    budget_code VARCHAR(50),
    total_amount DECIMAL(19,2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    justification TEXT,
    delivery_location TEXT,
    required_date TIMESTAMP,
    created_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    rejected_by VARCHAR(255),
    rejection_reason TEXT,
    rejected_at TIMESTAMP,
    purchase_order_id VARCHAR(255),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Requisition Items
CREATE TABLE IF NOT EXISTS requisition_items (
    id UUID PRIMARY KEY,
    requisition_id UUID NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(50),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    total_amount DECIMAL(19,2) NOT NULL,
    uom VARCHAR(20),
    required_date VARCHAR(50),
    notes TEXT,
    FOREIGN KEY (requisition_id) REFERENCES purchase_requisitions(id)
);

-- Vendor Onboarding
CREATE TABLE IF NOT EXISTS vendor_onboarding (
    id UUID PRIMARY KEY,
    vendor_id VARCHAR(255) NOT NULL,
    vendor_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(50),
    status VARCHAR(20) DEFAULT 'INITIATED',
    assigned_to VARCHAR(255),
    completed_by VARCHAR(255),
    completed_at TIMESTAMP,
    rejection_reason TEXT,
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Onboarding Documents
CREATE TABLE IF NOT EXISTS onboarding_documents (
    id UUID PRIMARY KEY,
    onboarding_id UUID NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500),
    uploaded_at TIMESTAMP NOT NULL,
    uploaded_by VARCHAR(255),
    verified BOOLEAN DEFAULT FALSE,
    verified_by VARCHAR(255),
    verified_at TIMESTAMP,
    FOREIGN KEY (onboarding_id) REFERENCES vendor_onboarding(id)
);

-- Budget Checks
CREATE TABLE IF NOT EXISTS budget_checks (
    id UUID PRIMARY KEY,
    budget_code VARCHAR(50) NOT NULL,
    cost_center VARCHAR(50) NOT NULL,
    project_code VARCHAR(50),
    requested_amount DECIMAL(19,2) NOT NULL,
    available_budget DECIMAL(19,2),
    encumbered_amount DECIMAL(19,2),
    remaining_budget DECIMAL(19,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    purchase_order_id VARCHAR(255),
    requisition_id VARCHAR(255),
    checked_by VARCHAR(255),
    checked_at TIMESTAMP,
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

-- Budget Check Details
CREATE TABLE IF NOT EXISTS budget_check_details (
    id UUID PRIMARY KEY,
    budget_check_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (budget_check_id) REFERENCES budget_checks(id)
);

-- Indexes
CREATE INDEX idx_requisitions_number ON purchase_requisitions(requisition_number);
CREATE INDEX idx_requisitions_department ON purchase_requisitions(department_id);
CREATE INDEX idx_requisitions_status ON purchase_requisitions(status);
CREATE INDEX idx_requisitions_created ON purchase_requisitions(created_date);

CREATE INDEX idx_onboarding_vendor ON vendor_onboarding(vendor_id);
CREATE INDEX idx_onboarding_status ON vendor_onboarding(status);

CREATE INDEX idx_budget_checks_code ON budget_checks(budget_code);
CREATE INDEX idx_budget_checks_po ON budget_checks(purchase_order_id);
CREATE INDEX idx_budget_checks_status ON budget_checks(status);