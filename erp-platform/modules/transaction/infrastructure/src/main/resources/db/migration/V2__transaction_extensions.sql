-- Fraud Detection Tables
CREATE TABLE IF NOT EXISTS fraud_checks (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_fingerprint VARCHAR(255),
    risk_score DOUBLE PRECISION DEFAULT 0,
    fraud_level VARCHAR(20) DEFAULT 'MINIMAL',
    status VARCHAR(20) DEFAULT 'PENDING',
    check_results_json TEXT,
    rule_set_id VARCHAR(255),
    recommended_action VARCHAR(20),
    reviewed_by VARCHAR(255),
    reviewed_at TIMESTAMP,
    review_notes TEXT,
    flagged BOOLEAN DEFAULT FALSE,
    flag_reason TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Gateway Configuration
CREATE TABLE IF NOT EXISTS gateway_configs (
    id VARCHAR(50) PRIMARY KEY,
    provider VARCHAR(50) NOT NULL,
    merchant_id VARCHAR(255),
    api_key VARCHAR(255) NOT NULL,
    api_secret VARCHAR(255),
    public_key VARCHAR(255),
    webhook_secret VARCHAR(255),
    is_live_mode BOOLEAN DEFAULT FALSE,
    additional_config_json TEXT,
    endpoint_url VARCHAR(500),
    timeout_seconds INTEGER DEFAULT 30,
    retry_attempts INTEGER DEFAULT 3,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- 3D Secure Authentication
CREATE TABLE IF NOT EXISTS three_d_secure_auths (
    authentication_id VARCHAR(50) PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    enrollment_status VARCHAR(5),
    authentication_status VARCHAR(5),
    eci_indicator VARCHAR(5),
    cavv VARCHAR(50),
    xid VARCHAR(50),
    ds_transaction_id VARCHAR(50),
    three_ds_version VARCHAR(20),
    challenge_status VARCHAR(20),
    authenticated BOOLEAN DEFAULT FALSE,
    authentication_time TIMESTAMP,
    authentication_method VARCHAR(50),
    browser_info TEXT,
    failure_reason TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Dispute Management
CREATE TABLE IF NOT EXISTS disputes (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    order_id VARCHAR(255),
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'OPEN',
    reason_code VARCHAR(50),
    reason_description TEXT,
    dispute_date TIMESTAMP NOT NULL,
    evidence_id VARCHAR(255),
    response_due_date VARCHAR(50),
    response TEXT,
    responded_at TIMESTAMP,
    resolved_by VARCHAR(255),
    resolved_at TIMESTAMP,
    resolution_notes TEXT,
    customer_notified BOOLEAN DEFAULT FALSE,
    funds_withheld BOOLEAN DEFAULT TRUE,
    internal_notes TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Dispute Evidence
CREATE TABLE IF NOT EXISTS dispute_evidence (
    id UUID PRIMARY KEY,
    dispute_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255),
    file_url VARCHAR(500),
    description TEXT,
    uploaded_at TIMESTAMP NOT NULL,
    uploaded_by VARCHAR(255),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (dispute_id) REFERENCES disputes(id)
);

-- Webhook Events
CREATE TABLE IF NOT EXISTS webhook_events (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    endpoint_url VARCHAR(500) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    response_status VARCHAR(20),
    response_body TEXT,
    error_message TEXT,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    delivered BOOLEAN DEFAULT FALSE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Exchange Rates
CREATE TABLE IF NOT EXISTS exchange_rates (
    id UUID PRIMARY KEY,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19,6) NOT NULL,
    inverse_rate DECIMAL(19,6) NOT NULL,
    rate_date TIMESTAMP NOT NULL,
    source VARCHAR(100),
    markup_percentage DECIMAL(5,2) DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Indexes
CREATE INDEX idx_fraud_transaction ON fraud_checks(transaction_id);
CREATE INDEX idx_fraud_customer ON fraud_checks(customer_id);
CREATE INDEX idx_fraud_status ON fraud_checks(status);
CREATE INDEX idx_fraud_score ON fraud_checks(risk_score);

CREATE INDEX idx_gateway_provider ON gateway_configs(provider);
CREATE INDEX idx_gateway_mode ON gateway_configs(is_live_mode);

CREATE INDEX idx_3ds_transaction ON three_d_secure_auths(transaction_id);
CREATE INDEX idx_3ds_status ON three_d_secure_auths(authentication_status);

CREATE INDEX idx_disputes_transaction ON disputes(transaction_id);
CREATE INDEX idx_disputes_customer ON disputes(customer_id);
CREATE INDEX idx_disputes_status ON disputes(status);
CREATE INDEX idx_disputes_date ON disputes(dispute_date);

CREATE INDEX idx_webhook_transaction ON webhook_events(transaction_id);
CREATE INDEX idx_webhook_event_type ON webhook_events(event_type);
CREATE INDEX idx_webhook_status ON webhook_events(status);
CREATE INDEX idx_webhook_created ON webhook_events(created_at);

CREATE INDEX idx_exchange_rates_currencies ON exchange_rates(from_currency, to_currency);
CREATE INDEX idx_exchange_rates_date ON exchange_rates(rate_date);