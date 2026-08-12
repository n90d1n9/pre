package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.port.NotificationPort;
import tech.kayys.erp.crm.application.port.EmailSenderPort;
import tech.kayys.erp.crm.domain.model.AutomationRule;
import tech.kayys.erp.crm.domain.repository.AutomationRuleRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Service for executing automation rules.
 */
@Singleton
@UseCase("Execute automation rules")
public class ExecuteAutomationHandler {

    private final AutomationRuleRepository ruleRepository;
    private final NotificationPort notificationPort;
    private final EmailSenderPort emailSenderPort;

    @Inject
    public ExecuteAutomationHandler(
            AutomationRuleRepository ruleRepository,
            NotificationPort notificationPort,
            EmailSenderPort emailSenderPort) {
        this.ruleRepository = ruleRepository;
        this.notificationPort = notificationPort;
        this.emailSenderPort = emailSenderPort;
    }

    /**
     * Executes all matching automation rules for a trigger event.
     */
    public CompletionStage<Void> executeRules(String triggerEvent, Map<String, Object> context) {
        return ruleRepository.findByTriggerEvent(triggerEvent)
            .thenAccept(rules -> {
                for (AutomationRule rule : rules) {
                    if (rule.matches(context)) {
                        executeActions(rule, context);
                    }
                }
            });
    }

    /**
     * Executes the actions of a single rule.
     */
    private void executeActions(AutomationRule rule, Map<String, Object> context) {
        for (AutomationRule.Action action : rule.getActions()) {
            executeAction(action, context);
        }
        ruleRepository.save(rule); // Update execution count
    }

    /**
     * Executes a single action.
     */
    private void executeAction(AutomationRule.Action action, Map<String, Object> context) {
        String type = action.getType();
        Map<String, String> params = action.getParameters();
        
        switch (type) {
            case "ASSIGN":
                // Assign to user based on params
                String assignTo = params.get("assignTo");
                if (assignTo != null) {
                    // Implementation would update the entity
                }
                break;
                
            case "SEND_EMAIL":
                // Send email notification
                String emailTo = params.get("emailTo");
                String subject = params.get("subject");
                String body = params.get("body");
                if (emailTo != null && subject != null && body != null) {
                    // Implementation would send email
                }
                break;
                
            case "UPDATE_FIELD":
                // Update a field on the entity
                String field = params.get("field");
                String value = params.get("value");
                if (field != null && value != null) {
                    // Implementation would update the field
                }
                break;
                
            case "NOTIFY":
                // Send notification
                String notifyTo = params.get("notifyTo");
                String message = params.get("message");
                if (notifyTo != null && message != null) {
                    notificationPort.sendNotification(notifyTo, message);
                }
                break;
                
            case "ESCALATE":
                // Escalate to higher level
                String escalateTo = params.get("escalateTo");
                String reason = params.get("reason");
                if (escalateTo != null) {
                    // Implementation would escalate
                }
                break;
        }
    }
}