-- Idempotency Keys
CREATE TABLE IF NOT EXISTS idempotency_keys (
    id UUID PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    operation_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    result_json TEXT,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
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
    last_attempt_at TIMESTAMP,
    response_status_code INTEGER,
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

-- Audit Logs
CREATE TABLE IF NOT EXISTS billing_audit_logs (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(255),
    details TEXT,
    timestamp TIMESTAMP NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Batch Processing Logs
CREATE TABLE IF NOT EXISTS batch_processing_logs (
    id UUID PRIMARY KEY,
    batch_id VARCHAR(100) NOT NULL,
    processed_count INTEGER DEFAULT 0,
    successful_count INTEGER DEFAULT 0,
    failed_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PROCESSING',
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    error_message TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Rate Limit Logs
CREATE TABLE IF NOT EXISTS rate_limit_logs (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    allowed BOOLEAN NOT NULL,
    current_count INTEGER NOT NULL,
    max_allowed INTEGER NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_idempotency_key ON idempotency_keys(key);
CREATE INDEX idx_idempotency_resource ON idempotency_keys(resource_id);
CREATE INDEX idx_idempotency_expires ON idempotency_keys(expires_at);

CREATE INDEX idx_webhook_transaction ON webhook_events(transaction_id);
CREATE INDEX idx_webhook_status ON webhook_events(status);
CREATE INDEX idx_webhook_created ON webhook_events(created_at);

CREATE INDEX idx_audit_entity ON billing_audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_timestamp ON billing_audit_logs(timestamp);

CREATE INDEX idx_batch_status ON batch_processing_logs(status);
CREATE INDEX idx_batch_started ON batch_processing_logs(started_at);

CREATE INDEX idx_ratelimit_customer ON rate_limit_logs(customer_id);
CREATE INDEX idx_ratelimit_operation ON rate_limit_logs(operation_type);