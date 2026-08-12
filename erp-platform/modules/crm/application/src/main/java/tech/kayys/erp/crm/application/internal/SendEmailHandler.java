package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.SendEmailCommand;
import tech.kayys.erp.crm.application.port.EmailSenderPort;
import tech.kayys.erp.crm.domain.identifier.EmailMessageId;
import tech.kayys.erp.crm.domain.model.EmailMessage;
import tech.kayys.erp.crm.domain.repository.EmailMessageRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Handler for sending emails.
 */
@UseCase("Send an email")
public class SendEmailHandler implements CommandHandler<SendEmailCommand, EmailMessageId> {

    private final EmailMessageRepository emailMessageRepository;
    private final EmailSenderPort emailSenderPort;

    @Inject
    public SendEmailHandler(EmailMessageRepository emailMessageRepository, EmailSenderPort emailSenderPort) {
        this.emailMessageRepository = emailMessageRepository;
        this.emailSenderPort = emailSenderPort;
    }

    @Override
    public CompletionStage<EmailMessageId> handle(SendEmailCommand command) {
        // Create email message record
        EmailMessage message = EmailMessage.create(
            command.messageId(),
            command.fromEmail(),
            command.to(),
            command.subject(),
            command.body(),
            command.trackOpens(),
            command.trackClicks()
        );

        if (command.fromName() != null) {
            message.setFromName(command.fromName());
        }
        if (command.cc() != null) {
            message.setCc(command.cc());
        }
        if (command.bcc() != null) {
            message.setBcc(command.bcc());
        }
        if (command.htmlBody() != null) {
            message.setHtmlBody(command.htmlBody());
        }
        if (command.replyTo() != null) {
            message.setReplyTo(command.replyTo());
        }
        if (command.attachments() != null) {
            for (String attachment : command.attachments()) {
                message.addAttachment(attachment);
            }
        }

        // Save message
        return emailMessageRepository.save(message)
            .thenCompose(savedMessage -> {
                // Send email via configured provider
                return emailSenderPort.sendEmail(savedMessage, command.variables())
                    .thenApply(result -> {
                        savedMessage.markSent();
                        return savedMessage;
                    })
                    .thenCompose(emailMessageRepository::save)
                    .thenApply(EmailMessage::getId);
            });
    }
}