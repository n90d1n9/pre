-- Usage Tracking Tables
CREATE TABLE IF NOT EXISTS usage_records (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    subscription_id VARCHAR(255),
    meter_id VARCHAR(100) NOT NULL,
    usage_date TIMESTAMP NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(50) NOT NULL,
    metadata_json TEXT,
    source VARCHAR(100),
    invoiced BOOLEAN DEFAULT FALSE,
    invoice_id VARCHAR(255),
    aggregated_period VARCHAR(50),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Payment Retry Logs
CREATE TABLE IF NOT EXISTS payment_retry_logs (
    id UUID PRIMARY KEY,
    billing_schedule_id UUID NOT NULL,
    attempt_number INTEGER NOT NULL,
    retry_date TIMESTAMP NOT NULL,
    retry_delay_days DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_code VARCHAR(50),
    error_message TEXT,
    next_retry_date TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (billing_schedule_id) REFERENCES billing_schedules(id)
);

-- Invoice Templates
CREATE TABLE IF NOT EXISTS invoice_templates (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    language VARCHAR(10) DEFAULT 'en',
    currency_code VARCHAR(3) DEFAULT 'USD',
    header_html TEXT,
    footer_html TEXT,
    styles_css TEXT,
    placeholders_json TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Billing Reconciliation Logs
CREATE TABLE IF NOT EXISTS billing_reconciliation_logs (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    reconciliation_date TIMESTAMP NOT NULL,
    total_billed DECIMAL(19,2) NOT NULL,
    total_accounted DECIMAL(19,2) NOT NULL,
    discrepancy DECIMAL(19,2) NOT NULL,
    reconciled BOOLEAN DEFAULT FALSE,
    notes TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Billing Dashboard Cache
CREATE TABLE IF NOT EXISTS billing_dashboard_cache (
    id UUID PRIMARY KEY,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    data_json TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_usage_records_customer ON usage_records(customer_id);
CREATE INDEX idx_usage_records_subscription ON usage_records(subscription_id);
CREATE INDEX idx_usage_records_meter ON usage_records(meter_id);
CREATE INDEX idx_usage_records_date ON usage_records(usage_date);
CREATE INDEX idx_usage_records_invoiced ON usage_records(invoiced);

CREATE INDEX idx_payment_retry_logs_schedule ON payment_retry_logs(billing_schedule_id);
CREATE INDEX idx_payment_retry_logs_status ON payment_retry_logs(status);
CREATE INDEX idx_payment_retry_logs_next_retry ON payment_retry_logs(next_retry_date);

CREATE INDEX idx_invoice_templates_default ON invoice_templates(is_default);
CREATE INDEX idx_invoice_templates_active ON invoice_templates(active);

CREATE INDEX idx_reconciliation_customer ON billing_reconciliation_logs(customer_id);
CREATE INDEX idx_reconciliation_date ON billing_reconciliation_logs(reconciliation_date);