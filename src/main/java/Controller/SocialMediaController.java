package Controller;

import java.util.List;
import java.util.Optional;

import Model.Message;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SocialMediaController {
    public Message getMessage(int id) {
        // Implementation goes here
        return null; // Replace null with the actual implementation
    }

    private void createMessage(Context ctx) {
        // Implementation goes here
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create().start(8080);

        app.put("/register", this::registerHandler);
        app.post("/login", ctx -> login(ctx)); // Fix: Define the login method in the SocialMediaController class
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/:message_id", this::getMessageById);
        app.delete("/messages/:message_id", this::deleteMessageById);
        app.patch("/messages/:message_id", this::updateMessageById);
        app.get("/accounts/:account_id/messages", this::getMessagesByAccountId);

        return app;
    }

    private void login(Context ctx) {
        // Implementation goes here
    }

    private void registerHandler(Context ctx) {
        // Implementation goes here
    }

    // ... (keep the rest of your methods as they are)

    private void getAllMessages(Context ctx) {
        MessageService messageService = new MessageService();
        List<Message> messages = messageService.getAllMessages();
        ctx.status(200).json(messages);
    }

    private void getMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        MessageService messageService = new MessageService();
        if (messageService.getMessageById(messageId) != null) {
            ctx.status(200).json(messageService.getMessageById(messageId));
        } else {
            ctx.status(404);
        }
    }

    private void deleteMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        MessageService messageService = new MessageService();
        Optional<Message> message = messageService.getMessageById(messageId);
        if (message != null) {
            messageService.deleteMessageById(Integer.toString(messageId));
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    }

    private void updateMessageById(Context ctx) {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        String newText = ctx.bodyAsClass(Message.class).getText();
        MessageService messageService = new MessageService();
        messageService.updateMessageTextById(Integer.toString(messageId), newText);
        messageService.updateMessage(messageService);
        ctx.status(200).json(messageService);
    }

    private void getMessagesByAccountId(Context ctx) {
        String accountId = ctx.pathParam("account_id");
        MessageService messageService = new MessageService();
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        ctx.status(200).json(messages);
    }
}
