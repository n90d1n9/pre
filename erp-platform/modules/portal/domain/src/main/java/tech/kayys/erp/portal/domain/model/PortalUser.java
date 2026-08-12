package tech.kayys.erp.portal.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.portal.domain.identifier.PortalUserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Portal user aggregate root.
 * Manages portal access and preferences.
 */
public final class PortalUser extends AggregateRoot<PortalUserId> {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String portalType; // CUSTOMER, EMPLOYEE, VENDOR, PARTNER
    private List<String> preferences;
    private String theme;
    private String language;
    private String timezone;
    private String defaultDashboard;
    private List<String> bookmarks;
    private String notificationPreferences;
    private boolean active;
    private boolean onboarded;
    private Instant lastLoginAt;
    private String notes;

    private PortalUser(PortalUserId id) {
        super(id);
        this.preferences = new ArrayList<>();
        this.bookmarks = new ArrayList<>();
        this.active = true;
        this.onboarded = false;
        this.theme = "light";
        this.language = "en";
    }

    private PortalUser() {
        super();
    }

    /**
     * Factory method to create a new portal user.
     */
    public static PortalUser create(
            PortalUserId id,
            String userId,
            String portalType) {
        PortalUser user = new PortalUser(id);
        user.userId = userId;
        user.portalType = portalType;
        return user;
    }

    /**
     * Onboards the user.
     */
    public void onboard() {
        this.onboarded = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records login.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a preference.
     */
    public void addPreference(String preference) {
        if (!preferences.contains(preference)) {
            preferences.add(preference);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a preference.
     */
    public void removePreference(String preference) {
        preferences.remove(preference);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates theme.
     */
    public void updateTheme(String theme) {
        this.theme = theme;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates language.
     */
    public void updateLanguage(String language) {
        this.language = language;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a bookmark.
     */
    public void addBookmark(String bookmark) {
        if (!bookmarks.contains(bookmark)) {
            bookmarks.add(bookmark);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a bookmark.
     */
    public void removeBookmark(String bookmark) {
        bookmarks.remove(bookmark);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the portal user.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getUserId() { return userId; }
    public String getPortalType() { return portalType; }
    public List<String> getPreferences() { return Collections.unmodifiableList(preferences); }
    public String getTheme() { return theme; }
    public String getLanguage() { return language; }
    public String getTimezone() { return timezone; }
    public String getDefaultDashboard() { return defaultDashboard; }
    public List<String> getBookmarks() { return Collections.unmodifiableList(bookmarks); }
    public String getNotificationPreferences() { return notificationPreferences; }
    public boolean isActive() { return active; }
    public boolean isOnboarded() { return onboarded; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public String getNotes() { return notes; }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDefaultDashboard(String defaultDashboard) {
        this.defaultDashboard = defaultDashboard;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotificationPreferences(String notificationPreferences) {
        this.notificationPreferences = notificationPreferences;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "PortalUser{" +
                "id=" + getId() +
                ", userId='" + userId + '\'' +
                ", portalType='" + portalType + '\'' +
                ", active=" + active +
                ", onboarded=" + onboarded +
                '}';
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>

    <module>modules/workforce/domain</module>
    <module>modules/workforce/application</module>
    <module>modules/workforce/infrastructure</module>
    <module>modules/workforce/interfaces</module>

    <module>modules/risk/domain</module>
    <module>modules/risk/application</module>
    <module>modules/risk/infrastructure</module>
    <module>modules/risk/interfaces</module>

    <module>modules/workflow/domain</module>
    <module>modules/workflow/application</module>
    <module>modules/workflow/infrastructure</module>
    <module>modules/workflow/interfaces</module>

    <module>modules/integration/domain</module>
    <module>modules/integration/application</module>
    <module>modules/integration/infrastructure</module>
    <module>modules/integration/interfaces</module>

    <module>modules/project/domain</module>
    <module>modules/project/application</module>
    <module>modules/project/infrastructure</module>
    <module>modules/project/interfaces</module>

    <module>modules/analytics/domain</module>
    <module>modules/analytics/application</module>
    <module>modules/analytics/infrastructure</module>
    <module>modules/analytics/interfaces</module>

    <!-- Cross-Cutting Modules -->
    <module>modules/security/domain</module>
    <module>modules/security/application</module>
    <module>modules/security/infrastructure</module>
    <module>modules/security/interfaces</module>

    <module>modules/audit/domain</module>
    <module>modules/audit/application</module>
    <module>modules/audit/infrastructure</module>
    <module>modules/audit/interfaces</module>

    <module>modules/i18n/domain</module>
    <module>modules/i18n/application</module>
    <module>modules/i18n/infrastructure</module>
    <module>modules/i18n/interfaces</module>

    <module>modules/privacy/domain</module>
    <module>modules/privacy/application</module>
    <module>modules/privacy/infrastructure</module>
    <module>modules/privacy/interfaces</module>

    <module>modules/portal/domain</module>
    <module>modules/portal/application</module>
    <module>modules/portal/infrastructure</module>
    <module>modules/portal/interfaces</module>
</modules>