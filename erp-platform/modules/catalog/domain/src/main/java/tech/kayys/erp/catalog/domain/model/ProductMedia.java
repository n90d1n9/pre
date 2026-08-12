package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Product media value object.
 * Represents images, videos, or other media attached to a product.
 */
public final class ProductMedia implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String mediaId;
    private final String url;
    private final String thumbnailUrl;
    private final String altText;
    private final String title;
    private final String mediaType; // IMAGE, VIDEO, DOCUMENT
    private final int sortOrder;
    private final boolean primary;
    private final boolean active;
    private final Map<String, String> metadata;

    public ProductMedia(
            String mediaId,
            String url,
            String thumbnailUrl,
            String altText,
            String title,
            String mediaType,
            int sortOrder,
            boolean primary,
            boolean active,
            Map<String, String> metadata) {
        this.mediaId = mediaId;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.altText = altText;
        this.title = title;
        this.mediaType = mediaType;
        this.sortOrder = sortOrder;
        this.primary = primary;
        this.active = active;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        validate();
    }

    @Override
    public void validate() {
        if (mediaId == null || mediaId.trim().isEmpty()) {
            throw new IllegalArgumentException("Media ID cannot be empty");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        if (mediaType == null || mediaType.trim().isEmpty()) {
            throw new IllegalArgumentException("Media type cannot be empty");
        }
    }

    // Getters
    public String getMediaId() { return mediaId; }
    public String getUrl() { return url; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getAltText() { return altText; }
    public String getTitle() { return title; }
    public String getMediaType() { return mediaType; }
    public int getSortOrder() { return sortOrder; }
    public boolean isPrimary() { return primary; }
    public boolean isActive() { return active; }
    public Map<String, String> getMetadata() { return Collections.unmodifiableMap(metadata); }

    public boolean isImage() {
        return "IMAGE".equals(mediaType);
    }

    public boolean isVideo() {
        return "VIDEO".equals(mediaType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductMedia that = (ProductMedia) o;
        return Objects.equals(mediaId, that.mediaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaId);
    }

    @Override
    public String toString() {
        return "ProductMedia{" +
                "mediaId='" + mediaId + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", primary=" + primary +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String mediaId;
        private String url;
        private String thumbnailUrl;
        private String altText;
        private String title;
        private String mediaType = "IMAGE";
        private int sortOrder = 0;
        private boolean primary = false;
        private boolean active = true;
        private Map<String, String> metadata = new HashMap<>();

        public Builder mediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder altText(String altText) {
            this.altText = altText;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder sortOrder(int sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Builder primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
            return this;
        }

        public Builder addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }

        public ProductMedia build() {
            if (mediaId == null) {
                mediaId = UUID.randomUUID().toString();
            }
            return new ProductMedia(
                mediaId, url, thumbnailUrl, altText, title,
                mediaType, sortOrder, primary, active, metadata
            );
        }
    }
}