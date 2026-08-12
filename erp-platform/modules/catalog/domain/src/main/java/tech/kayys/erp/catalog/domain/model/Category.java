package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.catalog.domain.identifier.CategoryId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Category aggregate root.
 * Represents a product category with hierarchical structure.
 */
public final class Category extends AggregateRoot<CategoryId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String slug;
    private String description;
    private CategoryId parentCategoryId;
    private List<CategoryId> childCategoryIds;
    private List<CategoryAttribute> attributes;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private int sortOrder;
    private boolean active;
    private boolean visibleInMenu;
    private String imageUrl;
    private String iconClass;
    private String color;

    private Category(CategoryId id) {
        super(id);
        this.childCategoryIds = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.active = true;
        this.visibleInMenu = true;
        this.sortOrder = 0;
    }

    private Category() {
        super();
    }

    /**
     * Factory method to create a new category.
     */
    public static Category create(
            CategoryId id,
            String name,
            String slug,
            String description) {
        Category category = new Category(id);
        category.name = name;
        category.slug = slug;
        category.description = description;
        return category;
    }

    /**
     * Adds a child category.
     */
    public void addChildCategory(CategoryId childCategoryId) {
        if (!childCategoryIds.contains(childCategoryId)) {
            childCategoryIds.add(childCategoryId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a child category.
     */
    public void removeChildCategory(CategoryId childCategoryId) {
        childCategoryIds.remove(childCategoryId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the parent category.
     */
    public void setParentCategory(CategoryId parentCategoryId) {
        if (this.id.equals(parentCategoryId)) {
            throw new IllegalArgumentException("Cannot set self as parent");
        }
        this.parentCategoryId = parentCategoryId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an attribute to the category.
     */
    public void addAttribute(CategoryAttribute attribute) {
        if (!attributes.contains(attribute)) {
            attributes.add(attribute);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes an attribute from the category.
     */
    public void removeAttribute(String attributeName) {
        attributes.removeIf(attr -> attr.getName().equals(attributeName));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the category.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the category.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the full category path.
     */
    public String getFullPath() {
        return slug.replace("-", "/");
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public CategoryId getParentCategoryId() { return parentCategoryId; }
    public List<CategoryId> getChildCategoryIds() { return Collections.unmodifiableList(childCategoryIds); }
    public List<CategoryAttribute> getAttributes() { return Collections.unmodifiableList(attributes); }
    public String getMetaTitle() { return metaTitle; }
    public String getMetaDescription() { return metaDescription; }
    public String getMetaKeywords() { return metaKeywords; }
    public int getSortOrder() { return sortOrder; }
    public boolean isActive() { return active; }
    public boolean isVisibleInMenu() { return visibleInMenu; }
    public String getImageUrl() { return imageUrl; }
    public String getIconClass() { return iconClass; }
    public String getColor() { return color; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setVisibleInMenu(boolean visibleInMenu) {
        this.visibleInMenu = visibleInMenu;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setColor(String color) {
        this.color = color;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", active=" + active +
                '}';
    }

    /**
     * Category attribute value object.
     */
    public static final class CategoryAttribute implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String name;
        private final String type; // TEXT, NUMBER, DATE, BOOLEAN, SELECT
        private final List<String> options;
        private final boolean required;
        private final boolean filterable;
        private final boolean visible;

        public CategoryAttribute(
                String name,
                String type,
                List<String> options,
                boolean required,
                boolean filterable,
                boolean visible) {
            this.name = name;
            this.type = type;
            this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
            this.required = required;
            this.filterable = filterable;
            this.visible = visible;
            validate();
        }

        @Override
        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Attribute name cannot be empty");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Attribute type cannot be empty");
            }
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public List<String> getOptions() { return Collections.unmodifiableList(options); }
        public boolean isRequired() { return required; }
        public boolean isFilterable() { return filterable; }
        public boolean isVisible() { return visible; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CategoryAttribute that = (CategoryAttribute) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "CategoryAttribute{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}