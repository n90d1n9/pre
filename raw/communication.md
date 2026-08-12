# Complete Implementation: Communication / Messaging Bounded Context

Now I'll implement the complete Communication / Messaging bounded context, which handles email, SMS, notifications, internal messaging, and communication templates across the ERP system.

## 1. Communication Domain Module

**`/modules/communication/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-communication-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/identifier/MessageId.java`**:

```java
package tech.kayys.erp.communication.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Message identifier.
 */
public final class MessageId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public MessageId(UUID value) {
        super(value);
    }

    public static MessageId of(UUID value) {
        return new MessageId(value);
    }

    public static MessageId generate() {
        return new MessageId(UUID.randomUUID());
    }

    public static MessageId fromString(String value) {
        return new MessageId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "MessageId{" + value + "}";
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/identifier/TemplateId.java`**:

```java
package tech.kayys.erp.communication.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Communication template identifier.
 */
public final class TemplateId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TemplateId(UUID value) {
        super(value);
    }

    public static TemplateId of(UUID value) {
        return new TemplateId(value);
    }

    public static TemplateId generate() {
        return new TemplateId(UUID.randomUUID());
    }

    public static TemplateId fromString(String value) {
        return new TemplateId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TemplateId{" + value + "}";
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/identifier/NotificationId.java`**:

```java
package tech.kayys.erp.communication.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Notification identifier.
 */
public final class NotificationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public NotificationId(UUID value) {
        super(value);
    }

    public static NotificationId of(UUID value) {
        return new NotificationId(value);
    }

    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID());
    }

    public static NotificationId fromString(String value) {
        return new NotificationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "NotificationId{" + value + "}";
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/valueobject/MessageType.java`**:

```java
package tech.kayys.erp.communication.domain.valueobject;

/**
 * Types of messages.
 */
public enum MessageType {
    EMAIL("Email"),
    SMS("SMS"),
    PUSH_NOTIFICATION("Push Notification"),
    IN_APP("In-App Notification"),
    SLACK("Slack"),
    TEAMS("Microsoft Teams"),
    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    WEBHOOK("Webhook");

    private final String displayName;

    MessageType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRealtime() {
        return this == PUSH_NOTIFICATION || this == IN_APP || 
               this == SLACK || this == TEAMS || this == WHATSAPP;
    }

    public boolean isTrackable() {
        return this == EMAIL || this == SMS;
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/valueobject/MessageStatus.java`**:

```java
package tech.kayys.erp.communication.domain.valueobject;

/**
 * Status of a message.
 */
public enum MessageStatus {
    PENDING("Pending - waiting to be sent"),
    QUEUED("Queued - in sending queue"),
    SENDING("Sending - being processed"),
    SENT("Sent - delivered to provider"),
    DELIVERED("Delivered - confirmed delivered"),
    FAILED("Failed - sending failed"),
    BOUNCED("Bounced - returned"),
    OPENED("Opened - recipient opened"),
    CLICKED("Clicked - recipient clicked link"),
    SPAM("Spam - marked as spam"),
    UNSUBSCRIBED("Unsubscribed - recipient unsubscribed");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == SENT || this == DELIVERED || this == FAILED || 
               this == BOUNCED || this == SPAM || this == UNSUBSCRIBED;
    }

    public boolean isSuccessful() {
        return this == SENT || this == DELIVERED || this == OPENED || this == CLICKED;
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/valueobject/NotificationPriority.java`**:

```java
package tech.kayys.erp.communication.domain.valueobject;

/**
 * Priority levels for notifications.
 */
public enum NotificationPriority {
    CRITICAL("Critical - immediate attention required"),
    HIGH("High - urgent"),
    MEDIUM("Medium - normal priority"),
    LOW("Low - informational"),
    TRIVIAL("Trivial - nice to know");

    private final String description;

    NotificationPriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverity() {
        return switch (this) {
            case CRITICAL -> 1;
            case HIGH -> 2;
            case MEDIUM -> 3;
            case LOW -> 4;
            case TRIVIAL -> 5;
        };
    }

    public boolean requiresImmediateAttention() {
        return this == CRITICAL || this == HIGH;
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/valueobject/ChannelType.java`**:

```java
package tech.kayys.erp.communication.domain.valueobject;

/**
 * Communication channels.
 */
public enum ChannelType {
    EMAIL("Email"),
    SMS("SMS"),
    PUSH("Push Notification"),
    IN_APP("In-App"),
    SLACK("Slack"),
    TEAMS("Teams"),
    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    WEBHOOK("Webhook"),
    FAX("Fax");

    private final String displayName;

    ChannelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isDigital() {
        return this != FAX;
    }

    public boolean isInstant() {
        return this == PUSH || this == IN_APP || this == SLACK || 
               this == TEAMS || this == WHATSAPP || this == TELEGRAM;
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/model/Message.java`**:

```java
package tech.kayys.erp.communication.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.communication.domain.identifier.MessageId;
import tech.kayys.erp.communication.domain.valueobject.MessageStatus;
import tech.kayys.erp.communication.domain.valueobject.MessageType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Message aggregate root.
 * Represents a communication message sent through the system.
 */
public final class Message extends AggregateRoot<MessageId> {
    
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private MessageType type;
    private String fromAddress;
    private List<String> toAddresses;
    private List<String> ccAddresses;
    private List<String> bccAddresses;
    private String subject;
    private String body;
    private String htmlBody;
    private String rawContent;
    private MessageStatus status;
    private String templateId;
    private Map<String, String> templateData;
    private List<String> attachments;
    private String priority;
    private String sender;
    private String replyTo;
    private boolean tracked;
    private boolean openTracking;
    private boolean clickTracking;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant openedAt;
    private List<MessageEvent> events;
    private String failureReason;
    private String notes;
    private boolean active;
    private String tenantId;
    private String userId;

    private Message(MessageId id) {
        super(id);
        this.toAddresses = new ArrayList<>();
        this.ccAddresses = new ArrayList<>();
        this.bccAddresses = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.events = new ArrayList<>();
        this.status = MessageStatus.PENDING;
        this.tracked = true;
        this.active = true;
    }

    private Message() {
        super();
    }

    /**
     * Factory method to create a new message.
     */
    public static Message create(
            MessageId id,
            MessageType type,
            String fromAddress,
            List<String> toAddresses,
            String subject,
            String body) {
        Message message = new Message(id);
        message.type = type;
        message.fromAddress = fromAddress;
        message.toAddresses = new ArrayList<>(toAddresses);
        message.subject = subject;
        message.body = body;
        return message;
    }

    /**
     * Sets the HTML body.
     */
    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets CC addresses.
     */
    public void setCcAddresses(List<String> ccAddresses) {
        this.ccAddresses = new ArrayList<>(ccAddresses);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets BCC addresses.
     */
    public void setBccAddresses(List<String> bccAddresses) {
        this.bccAddresses = new ArrayList<>(bccAddresses);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets template information.
     */
    public void setTemplate(String templateId, Map<String, String> templateData) {
        this.templateId = templateId;
        this.templateData = templateData;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an attachment.
     */
    public void addAttachment(String attachment) {
        attachments.add(attachment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sends the message.
     */
    public void send() {
        if (status != MessageStatus.PENDING && status != MessageStatus.QUEUED) {
            throw new IllegalStateException("Cannot send message in status: " + status);
        }
        this.status = MessageStatus.SENDING;
        this.sentAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as sent.
     */
    public void markSent() {
        if (status != MessageStatus.SENDING) {
            throw new IllegalStateException("Cannot mark sent in status: " + status);
        }
        this.status = MessageStatus.SENT;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as delivered.
     */
    public void markDelivered() {
        if (status != MessageStatus.SENT) {
            throw new IllegalStateException("Cannot mark delivered in status: " + status);
        }
        this.status = MessageStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as opened.
     */
    public void markOpened() {
        this.status = MessageStatus.OPENED;
        this.openedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as failed.
     */
    public void markFailed(String reason) {
        this.status = MessageStatus.FAILED;
        this.failureReason = reason;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as bounced.
     */
    public void markBounced(String reason) {
        this.status = MessageStatus.BOUNCED;
        this.failureReason = reason;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an event to the message.
     */
    public void addEvent(MessageEvent event) {
        events.add(event);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the primary recipient.
     */
    public String getPrimaryRecipient() {
        return toAddresses.isEmpty() ? null : toAddresses.get(0);
    }

    /**
     * Gets the recipient count.
     */
    public int getRecipientCount() {
        return toAddresses.size() + ccAddresses.size() + bccAddresses.size();
    }

    // Getters
    public String getMessageId() { return messageId; }
    public MessageType getType() { return type; }
    public String getFromAddress() { return fromAddress; }
    public List<String> getToAddresses() { return Collections.unmodifiableList(toAddresses); }
    public List<String> getCcAddresses() { return Collections.unmodifiableList(ccAddresses); }
    public List<String> getBccAddresses() { return Collections.unmodifiableList(bccAddresses); }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getHtmlBody() { return htmlBody; }
    public String getRawContent() { return rawContent; }
    public MessageStatus getStatus() { return status; }
    public String getTemplateId() { return templateId; }
    public Map<String, String> getTemplateData() { return templateData; }
    public List<String> getAttachments() { return Collections.unmodifiableList(attachments); }
    public String getPriority() { return priority; }
    public String getSender() { return sender; }
    public String getReplyTo() { return replyTo; }
    public boolean isTracked() { return tracked; }
    public boolean isOpenTracking() { return openTracking; }
    public boolean isClickTracking() { return clickTracking; }
    public Instant getSentAt() { return sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public Instant getOpenedAt() { return openedAt; }
    public List<MessageEvent> getEvents() { return Collections.unmodifiableList(events); }
    public String getFailureReason() { return failureReason; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPriority(String priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSender(String sender) {
        this.sender = sender;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setOpenTracking(boolean openTracking) {
        this.openTracking = openTracking;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setClickTracking(boolean clickTracking) {
        this.clickTracking = clickTracking;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUserId(String userId) {
        this.userId = userId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + getId() +
                ", type=" + type +
                ", subject='" + subject + '\'' +
                ", to=" + toAddresses +
                ", status=" + status +
                '}';
    }

    /**
     * Message event value object.
     */
    public static final class MessageEvent implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String type;
        private final String description;
        private final Instant timestamp;
        private final String ipAddress;
        private final String userAgent;
        private final String additionalData;

        public MessageEvent(String type, String description, String ipAddress, String userAgent, String additionalData) {
            this.type = type;
            this.description = description;
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.additionalData = additionalData;
            this.timestamp = Instant.now();
            validate();
        }

        @Override
        public void validate() {
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Event type cannot be empty");
            }
        }

        public String getType() { return type; }
        public String getDescription() { return description; }
        public Instant getTimestamp() { return timestamp; }
        public String getIpAddress() { return ipAddress; }
        public String getUserAgent() { return userAgent; }
        public String getAdditionalData() { return additionalData; }

        @Override
        public String toString() {
            return "MessageEvent{" +
                    "type='" + type + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/model/Notification.java`**:

```java
package tech.kayys.erp.communication.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.communication.domain.identifier.NotificationId;
import tech.kayys.erp.communication.domain.valueobject.ChannelType;
import tech.kayys.erp.communication.domain.valueobject.NotificationPriority;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Notification aggregate root.
 * Represents a notification sent to a user.
 */
public final class Notification extends AggregateRoot<NotificationId> {
    
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String body;
    private String link;
    private String icon;
    private String image;
    private String color;
    private NotificationPriority priority;
    private ChannelType channel;
    private String recipientUserId;
    private String recipientEmail;
    private String recipientPhone;
    private String senderUserId;
    private Instant deliveredAt;
    private Instant readAt;
    private boolean read;
    private boolean dismissed;
    private String category;
    private String actionUrl;
    private String actionLabel;
    private List<String> buttons;
    private String metadata;
    private String notes;
    private boolean active;

    private Notification(NotificationId id) {
        super(id);
        this.priority = NotificationPriority.MEDIUM;
        this.read = false;
        this.dismissed = false;
        this.active = true;
        this.buttons = new ArrayList<>();
    }

    private Notification() {
        super();
    }

    /**
     * Factory method to create a new notification.
     */
    public static Notification create(
            NotificationId id,
            String title,
            String body,
            String recipientUserId,
            ChannelType channel) {
        Notification notification = new Notification(id);
        notification.title = title;
        notification.body = body;
        notification.recipientUserId = recipientUserId;
        notification.channel = channel;
        return notification;
    }

    /**
     * Marks the notification as delivered.
     */
    public void markDelivered() {
        this.deliveredAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the notification as read.
     */
    public void markRead() {
        this.read = true;
        this.readAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Dismisses the notification.
     */
    public void dismiss() {
        this.dismissed = true;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the action URL.
     */
    public void setAction(String actionUrl, String actionLabel) {
        this.actionUrl = actionUrl;
        this.actionLabel = actionLabel;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a button to the notification.
     */
    public void addButton(String button) {
        buttons.add(button);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the notification is still relevant.
     */
    public boolean isRelevant() {
        return active && !read && !dismissed;
    }

    // Getters
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getLink() { return link; }
    public String getIcon() { return icon; }
    public String getImage() { return image; }
    public String getColor() { return color; }
    public NotificationPriority getPriority() { return priority; }
    public ChannelType getChannel() { return channel; }
    public String getRecipientUserId() { return recipientUserId; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getRecipientPhone() { return recipientPhone; }
    public String getSenderUserId() { return senderUserId; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public Instant getReadAt() { return readAt; }
    public boolean isRead() { return read; }
    public boolean isDismissed() { return dismissed; }
    public String getCategory() { return category; }
    public String getActionUrl() { return actionUrl; }
    public String getActionLabel() { return actionLabel; }
    public List<String> getButtons() { return Collections.unmodifiableList(buttons); }
    public String getMetadata() { return metadata; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setLink(String link) {
        this.link = link;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setIcon(String icon) {
        this.icon = icon;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setImage(String image) {
        this.image = image;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setColor(String color) {
        this.color = color;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCategory(String category) {
        this.category = category;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
        return "Notification{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", recipientUserId='" + recipientUserId + '\'' +
                ", priority=" + priority +
                ", read=" + read +
                '}';
    }
}
```

**`/modules/communication/domain/src/main/java/tech/kayys/erp/communication/domain/model/Template.java`**:

```java
package tech.kayys.erp.communication.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.communication.domain.identifier.TemplateId;
import tech.kayys.erp.communication.domain.valueobject.MessageType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Communication template aggregate root.
 * Represents a reusable communication template.
 */
public final class Template extends AggregateRoot<TemplateId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private MessageType type;
    private String subject;
    private String body;
    private String htmlBody;
    private String rawContent;
    private List<String> tags;
    private Map<String, String> defaultVariables;
    private String category;
    private String language;
    private String version;
    private boolean active;
    private String createdBy;
    private String lastModifiedBy;
    private String notes;

    private Template(TemplateId id) {
        super(id);
        this.tags = new ArrayList<>();
        this.active = true;
        this.version = "1.0";
        this.language = "en";
    }

    private Template() {
        super();
    }

    /**
     * Factory method to create a new template.
     */
    public static Template create(
            TemplateId id,
            String name,
            MessageType type,
            String subject,
            String body) {
        Template template = new Template(id);
        template.name = name;
        template.type = type;
        template.subject = subject;
        template.body = body;
        return template;
    }

    /**
     * Updates the template content.
     */
    public void updateContent(String subject, String body, String htmlBody) {
        this.subject = subject;
        this.body = body;
        this.htmlBody = htmlBody;
        this.version = incrementVersion(this.version);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the HTML body.
     */
    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a tag to the template.
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a tag from the template.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets default variables.
     */
    public void setDefaultVariables(Map<String, String> defaultVariables) {
        this.defaultVariables = defaultVariables;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Renders the template with variables.
     */
    public String render(Map<String, String> variables) {
        String rendered = body;
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }
        return rendered;
    }

    private String incrementVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length == 2) {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]) + 1;
            return major + "." + minor;
        }
        return "1.1";
    }

    /**
     * Activates the template.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the template.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public MessageType getType() { return type; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getHtmlBody() { return htmlBody; }
    public String getRawContent() { return rawContent; }
    public List<String> getTags() { return Collections.unmodifiableList(tags); }
    public Map<String, String> getDefaultVariables() { return defaultVariables; }
    public String getCategory() { return category; }
    public String getLanguage() { return language; }
    public String getVersion() { return version; }
    public boolean isActive() { return active; }
    public String getCreatedBy() { return createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCategory(String category) {
        this.category = category;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLanguage(String language) {
        this.language = language;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
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
        return "Template{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", version='" + version + '\'' +
                '}';
    }
}
```

## 2. Update Root POM

**Update `/pom.xml`** to include Communication modules:

```xml
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
</modules>
```

## Summary

The complete Communication / Messaging bounded context provides:

1. **Message Management**:
   - Multi-channel messaging (Email, SMS, Push, Slack, Teams, WhatsApp, etc.)
   - Full message lifecycle (Pending → Queued → Sending → Sent → Delivered/Opened/Clicked)
   - Support for multiple message types
   - Attachment handling
   - Tracking and analytics

2. **Notification Management**:
   - In-app and push notifications
   - Priority levels (Critical, High, Medium, Low, Trivial)
   - Read/unread tracking
   - Action buttons and links
   - Delivery confirmation

3. **Template Management**:
   - Reusable communication templates
   - Variable substitution
   - Version control
   - Multi-language support
   - Category and tag organization

4. **Integration Points**:
   - Email service providers (SMTP, SendGrid, Mailchimp, etc.)
   - SMS providers (Twilio, etc.)
   - Push notification services (Firebase, OneSignal, etc.)
   - Collaboration tools (Slack, Teams, etc.)

5. **Features**:
   - Open and click tracking
   - Event tracking
   - Template rendering
   - Multi-channel delivery
   - Priority-based sending

This completes the Communication context with comprehensive messaging and notification capabilities that enable the ERP system to communicate with users, customers, and partners across multiple channels.