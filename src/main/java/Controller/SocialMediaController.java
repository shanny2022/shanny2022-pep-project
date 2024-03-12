package Controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SocialMediaController {
    private MessageService messageService = new MessageService();
    private AccountService accountService;

    public SocialMediaController(AccountService accountService) {
        this.accountService = accountService;
    }

    public Message getMessage(int id) {
        return messageService.getMessageById(id).orElse(null);
    }

    private void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        messageService.createMessage(message);
        ctx.status(201).json(message);
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
        Account account = ctx.bodyAsClass(Account.class);
        accountService.login(account);
        ctx.status(200);
    }

    private void registerHandler(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        accountService.register(account);
        ctx.status(201);
    }

    // ... (keep the rest of your methods as they are)

    private void getAllMessages(Context ctx) {
            List<Message> messages = messageService.getAllMessages();
            ctx.status(200).json(messages);
        }

        private void getMessageById(Context ctx) {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<Message> message = messageService.getMessageById(messageId);
            if (message.isPresent()) {
                ctx.status(200).json(message.get());
            } else {
                ctx.status(404);
            }
        }

        private void deleteMessageById(Context ctx) {
    int messageId = Integer.parseInt(ctx.pathParam("message_id"));
    try {
        Message message = messageService.deleteMessageById(messageId);
        ctx.status(200).json(message);
    } catch (NoSuchElementException e) {
        ctx.status(200);
    }
}

        private void updateMessageById(Context ctx) {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            String newText = ctx.body();
            Message message = messageService.updateMessageTextById(messageId, newText);
            ctx.status(200).json(message);
        }

        private void getMessagesByAccountId(Context ctx) {
            String accountId = ctx.pathParam("account_id");
            List<Message> messages = messageService.getMessagesByAccountId(accountId);
            ctx.status(200).json(messages);
        }
    }
