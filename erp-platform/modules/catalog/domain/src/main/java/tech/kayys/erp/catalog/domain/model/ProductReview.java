package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.catalog.domain.identifier.ReviewId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Product review aggregate root.
 * Represents customer reviews and ratings for a product.
 */
public final class ProductReview extends AggregateRoot<ReviewId> {
    
    private static final long serialVersionUID = 1L;
    
    private UUID productId;
    private UUID customerId;
    private String customerName;
    private int rating; // 1-5
    private String title;
    private String content;
    private ReviewStatus status;
    private int helpfulVotes;
    private int unhelpfulVotes;
    private List<String> images;
    private String verifiedPurchaseOrderId;
    private Instant purchaseDate;
    private String response; // Seller response
    private String respondedBy;
    private Instant respondedAt;
    private boolean recommended;

    private ProductReview(ReviewId id) {
        super(id);
        this.status = ReviewStatus.PENDING_MODERATION;
        this.images = new ArrayList<>();
        this.helpfulVotes = 0;
        this.unhelpfulVotes = 0;
        this.recommended = true;
    }

    private ProductReview() {
        super();
    }

    /**
     * Factory method to create a new product review.
     */
    public static ProductReview create(
            ReviewId id,
            UUID productId,
            UUID customerId,
            String customerName,
            int rating,
            String title,
            String content) {
        ProductReview review = new ProductReview(id);
        review.productId = productId;
        review.customerId = customerId;
        review.customerName = customerName;
        review.rating = rating;
        review.title = title;
        review.content = content;
        review.recommended = rating >= 4;
        return review;
    }

    /**
     * Approves the review.
     */
    public void approve(String approvedBy) {
        if (status != ReviewStatus.PENDING_MODERATION) {
            throw new IllegalStateException("Review is not pending moderation");
        }
        this.status = ReviewStatus.APPROVED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the review.
     */
    public void reject(String reason) {
        if (status != ReviewStatus.PENDING_MODERATION) {
            throw new IllegalStateException("Review is not pending moderation");
        }
        this.status = ReviewStatus.REJECTED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Hides the review.
     */
    public void hide() {
        this.status = ReviewStatus.HIDDEN;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a helpful vote.
     */
    public void markHelpful() {
        this.helpfulVotes++;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an unhelpful vote.
     */
    public void markUnhelpful() {
        this.unhelpfulVotes++;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Responds to the review.
     */
    public void respond(String response, String respondedBy) {
        this.response = response;
        this.respondedBy = respondedBy;
        this.respondedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the helpfulness ratio.
     */
    public double getHelpfulnessRatio() {
        int total = helpfulVotes + unhelpfulVotes;
        if (total == 0) {
            return 0.0;
        }
        return (double) helpfulVotes / total * 100.0;
    }

    // Getters
    public UUID getProductId() { return productId; }
    public UUID getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public int getRating() { return rating; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public ReviewStatus getStatus() { return status; }
    public int getHelpfulVotes() { return helpfulVotes; }
    public int getUnhelpfulVotes() { return unhelpfulVotes; }
    public List<String> getImages() { return Collections.unmodifiableList(images); }
    public String getVerifiedPurchaseOrderId() { return verifiedPurchaseOrderId; }
    public Instant getPurchaseDate() { return purchaseDate; }
    public String getResponse() { return response; }
    public String getRespondedBy() { return respondedBy; }
    public Instant getRespondedAt() { return respondedAt; }
    public boolean isRecommended() { return recommended; }

    public void setImages(List<String> images) {
        this.images = new ArrayList<>(images);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setVerifiedPurchaseOrderId(String verifiedPurchaseOrderId) {
        this.verifiedPurchaseOrderId = verifiedPurchaseOrderId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPurchaseDate(Instant purchaseDate) {
        this.purchaseDate = purchaseDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + getId() +
                ", productId=" + productId +
                ", customerName='" + customerName + '\'' +
                ", rating=" + rating +
                ", status=" + status +
                '}';
    }

    /**
     * Review status enum.
     */
    public enum ReviewStatus {
        PENDING_MODERATION("Pending Moderation"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        HIDDEN("Hidden");

        private final String description;

        ReviewStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}