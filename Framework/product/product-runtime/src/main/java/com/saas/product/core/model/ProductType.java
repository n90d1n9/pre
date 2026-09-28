package com.saas.product.core.model;

/**
 * Universal product type classification.
 * Determines which lifecycle rules and extension contexts are applicable.
 */
public enum ProductType {

    /**
     * Tangible item with physical inventory (e.g. clothing, electronics, groceries).
     */
    PHYSICAL,

    /**
     * Digital goods: software, ebooks, media files. No physical stock.
     */
    DIGITAL,

    /**
     * Time-based recurring access (SaaS, streaming, membership).
     */
    SUBSCRIPTION,

    /**
     * One-time or recurring service delivery (cleaning, repair, consulting).
     */
    SERVICE,

    /**
     * Redeemable voucher or gift card with monetary value.
     */
    VOUCHER,

    /**
     * Bundle of other products sold together (combo meal, starter kit).
     * Contains a list of component ProductIds.
     */
    BUNDLE,

    /**
     * Master product with multiple variants (size/color SKUs).
     * The aggregate itself is not orderable; variants are.
     */
    VARIANT_PARENT,

    /**
     * Concrete variant of a VARIANT_PARENT.
     */
    VARIANT
}
