-- Core Tables
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    stock_level INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    category_id UUID,
    brand VARCHAR(100),
    manufacturer VARCHAR(100),
    upc VARCHAR(20),
    ean VARCHAR(20),
    mpn VARCHAR(50),
    weight DOUBLE PRECISION,
    weight_unit VARCHAR(5),
    taxable BOOLEAN DEFAULT TRUE,
    tax_code VARCHAR(20),
    shippable BOOLEAN DEFAULT TRUE,
    min_order_quantity INTEGER DEFAULT 1,
    max_order_quantity INTEGER DEFAULT 100,
    seo_title VARCHAR(200),
    seo_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    parent_category_id UUID,
    meta_title VARCHAR(200),
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    sort_order INTEGER DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    visible_in_menu BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    icon_class VARCHAR(100),
    color VARCHAR(20),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (parent_category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    subtotal DECIMAL(19,2) NOT NULL,
    tax_total DECIMAL(19,2) NOT NULL,
    shipping_cost DECIMAL(19,2) NOT NULL,
    discount_total DECIMAL(19,2) NOT NULL,
    grand_total DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    shipping_address_street VARCHAR(255),
    shipping_address_city VARCHAR(100),
    shipping_address_state VARCHAR(100),
    shipping_address_postal_code VARCHAR(20),
    shipping_address_country VARCHAR(100),
    billing_address_street VARCHAR(255),
    billing_address_city VARCHAR(100),
    billing_address_state VARCHAR(100),
    billing_address_postal_code VARCHAR(20),
    billing_address_country VARCHAR(100),
    customer_notes TEXT,
    internal_notes TEXT,
    submitted_at TIMESTAMP,
    confirmed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    shipping_method VARCHAR(50),
    tracking_number VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(50),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    total_price DECIMAL(19,2) NOT NULL,
    tax_amount DECIMAL(19,2) NOT NULL,
    discount_amount DECIMAL(19,2) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS billing_schedules (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255),
    frequency VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP,
    next_billing_date TIMESTAMP,
    last_billing_date TIMESTAMP,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_method_token VARCHAR(255),
    current_cycle INTEGER DEFAULT 0,
    total_cycles INTEGER DEFAULT 0,
    failed_payment_count INTEGER DEFAULT 0,
    max_failed_payments INTEGER DEFAULT 3,
    send_email_notifications BOOLEAN DEFAULT TRUE,
    send_sms_notifications BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Indexes
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
CREATE INDEX idx_products_status ON products(status);

CREATE INDEX idx_categories_parent ON categories(parent_category_id);
CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_active ON categories(active);

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_submitted_at ON orders(submitted_at);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE INDEX idx_billing_schedules_customer ON billing_schedules(customer_id);
CREATE INDEX idx_billing_schedules_subscription ON billing_schedules(subscription_id);
CREATE INDEX idx_billing_schedules_status ON billing_schedules(status);
CREATE INDEX idx_billing_schedules_next_billing ON billing_schedules(next_billing_date);