-- Add accounting reference to transactions
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS accounting_reference VARCHAR(255);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS accounting_status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS accounting_posted_at TIMESTAMP;

-- Add source information to journal entries
ALTER TABLE journal_entries ADD COLUMN IF NOT EXISTS source_type VARCHAR(50);
ALTER TABLE journal_entries ADD COLUMN IF NOT EXISTS source_id VARCHAR(255);

-- Create reconciliation tracking table
CREATE TABLE IF NOT EXISTS reconciliation_entries (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    journal_entry_id VARCHAR(255),
    reconciliation_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Create accounting event log
CREATE TABLE IF NOT EXISTS accounting_event_log (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    transaction_id VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Indexes
CREATE INDEX idx_transactions_accounting_ref ON transactions(accounting_reference);
CREATE INDEX idx_transactions_accounting_status ON transactions(accounting_status);
CREATE INDEX idx_journal_entries_source ON journal_entries(source_type, source_id);
CREATE INDEX idx_reconciliation_transaction ON reconciliation_entries(transaction_id);
CREATE INDEX idx_reconciliation_date ON reconciliation_entries(reconciliation_date);
CREATE INDEX idx_accounting_event_transaction ON accounting_event_log(transaction_id);
CREATE INDEX idx_accounting_event_processed ON accounting_event_log(processed);