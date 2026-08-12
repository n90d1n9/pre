-- Price Books
CREATE TABLE IF NOT EXISTS price_books (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    currency_code VARCHAR(3) NOT NULL,
    customer_segment VARCHAR(50),
    region VARCHAR(100),
    channel VARCHAR(50),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
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

-- Price Book Entries
CREATE TABLE IF NOT EXISTS price_book_entries (
    id UUID PRIMARY KEY,
    price_book_id UUID NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    product_sku VARCHAR(50),
    product_name VARCHAR(255),
    price DECIMAL(19,2) NOT NULL,
    compare_at_price DECIMAL(19,2),
    cost DECIMAL(19,2),
    price_type VARCHAR(20) DEFAULT 'FIXED',
    unit VARCHAR(20),
    min_quantity DECIMAL(19,2) DEFAULT 1,
    max_quantity DECIMAL(19,2) DEFAULT 0,
    notes TEXT,
    FOREIGN KEY (price_book_id) REFERENCES price_books(id)
);

-- Dynamic Price Rules
CREATE TABLE IF NOT EXISTS dynamic_price_rules (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    product_id VARCHAR(255) NOT NULL,
    product_category VARCHAR(100),
    rule_type VARCHAR(50) NOT NULL,
    base_price_modifier DECIMAL(10,2) DEFAULT 0,
    min_price DECIMAL(19,2),
    max_price DECIMAL(19,2),
    currency_code VARCHAR(3) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    priority INTEGER DEFAULT 0,
    notes TEXT,
    created_by VARCHAR(255),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(255)
);

-- Price Adjustments
CREATE TABLE IF NOT EXISTS price_adjustments (
    id UUID PRIMARY KEY,
    rule_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    value DECIMAL(10,2) NOT NULL,
    description TEXT,
    FOREIGN KEY (rule_id) REFERENCES dynamic_price_rules(id)
);

-- Price Triggers
CREATE TABLE IF NOT EXISTS price_triggers (
    id UUID PRIMARY KEY,
    rule_id UUID NOT NULL,
    condition VARCHAR(100) NOT NULL,
    operator VARCHAR(10) NOT NULL,
    threshold DECIMAL(19,2) NOT NULL,
    adjustment_id UUID NOT NULL,
    description TEXT,
    FOREIGN KEY (rule_id) REFERENCES dynamic_price_rules(id),
    FOREIGN KEY (adjustment_id) REFERENCES price_adjustments(id)
);

-- Tiered Prices
CREATE TABLE IF NOT EXISTS tiered_prices (
    id UUID PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(50),
    currency_code VARCHAR(3) NOT NULL,
    customer_segment VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    notes TEXT,
    created_by VARCHAR(255),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(255)
);

-- Price Tiers
CREATE TABLE IF NOT EXISTS price_tiers (
    id UUID PRIMARY KEY,
    tiered_price_id UUID NOT NULL,
    min_quantity DECIMAL(19,2) NOT NULL,
    max_quantity DECIMAL(19,2) DEFAULT 0,
    unit_price DECIMAL(19,2) NOT NULL,
    discount_percentage DECIMAL(10,2) DEFAULT 0,
    description TEXT,
    FOREIGN KEY (tiered_price_id) REFERENCES tiered_prices(id)
);

-- Price Change History
CREATE TABLE IF NOT EXISTS price_change_history (
    id UUID PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    old_price DECIMAL(19,2) NOT NULL,
    new_price DECIMAL(19,2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    change_reason TEXT,
    changed_by VARCHAR(255),
    changed_at TIMESTAMP NOT NULL,
    price_book_id UUID,
    FOREIGN KEY (price_book_id) REFERENCES price_books(id)
);

-- Indexes
CREATE INDEX idx_price_books_code ON price_books(code);
CREATE INDEX idx_price_books_status ON price_books(status);
CREATE INDEX idx_price_books_type ON price_books(type);

CREATE INDEX idx_price_entries_book ON price_book_entries(price_book_id);
CREATE INDEX idx_price_entries_product ON price_book_entries(product_id);

CREATE INDEX idx_dynamic_rules_product ON dynamic_price_rules(product_id);
CREATE INDEX idx_dynamic_rules_type ON dynamic_price_rules(rule_type);
CREATE INDEX idx_dynamic_rules_active ON dynamic_price_rules(active);

CREATE INDEX idx_tiered_product ON tiered_prices(product_id);
CREATE INDEX idx_tiered_active ON tiered_prices(active);

CREATE INDEX idx_price_history_product ON price_change_history(product_id);
CREATE INDEX idx_price_history_changed ON price_change_history(changed_at);