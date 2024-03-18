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
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(Javalin app) {
        this.accountService = new AccountService();
        this.messageService = new MessageService();

        app.get("/accounts", this::getAllAccounts);
        app.get("/accounts/{id}", this::getAccount);
        app.post("/accounts", this::createAccount);
        app.put("/accounts/{id}", this::updateAccount);
        app.delete("/accounts/{id}", this::deleteAccount);
        app.get("/messages/{id}", this::getMessageById);
        app.get("/messages", this::getAllMessages);
        app.post("/messages", this::createMessage);
        app.put("/messages/{id}", this::updateMessage);
        app.delete("/messages/{id}", this::deleteMessage);
        app.get("/accounts/{id}/messages", this::getAllMessagesForUser);
        app.post("/register", this::registerAccount);
        app.patch("/messages/{id}", this::updateMessage);
    }

    public SocialMediaController(MessageService messageService) {
        this.messageService = messageService;
    }

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    private void getAllAccounts(Context ctx) {
        List<Account> accounts = accountService.getAllAccounts();
        ctx.json(accounts);
    }

    private void registerAccount(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        if (account.getPassword().length() < 4) {
            ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Password length should be at least 4 characters");
        } else {
            accountService.createAccount(account);
            ctx.status(HttpStatus.CREATED.getStatusCode());
        }
    }

    private void getAccount(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Optional<Account> account = accountService.getAccount(id);
            ctx.json(account.get());
        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
        }
    }

    private void createAccount(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        accountService.createAccount(account);
        ctx.status(HttpStatus.CREATED.getStatusCode());
    }

    private void updateAccount(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Account account = ctx.bodyAsClass(Account.class);
            accountService.updateAccount(id, account);
            ctx.status(HttpStatus.NO_CONTENT.getStatusCode());
        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
        }
    }

    private void deleteAccount(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            accountService.deleteAccount(id);
            ctx.status(HttpStatus.NO_CONTENT.getStatusCode());
        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
        }
    }

    private void getAllMessages(Context ctx) {
        try {
            List<Message> messages = messageService.getAllMessages();
            ctx.status(HttpStatus.OK.getStatusCode()).json(messages);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getStatusCode()).result("An error occurred while retrieving messages: " + e.getMessage());
        }
    }

    private void getAllMessagesForUser(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            List<Message> messages = messageService.getAllMessagesForUser(id);
            ctx.status(HttpStatus.OK.getStatusCode()).json(messages);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Invalid ID format: " + ctx.pathParam("id"));
        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
        }
    }

    private void getMessageById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Message message = messageService.getMessageById(id);
            if (message != null) {
                ctx.status(HttpStatus.OK.getStatusCode()).json(message);
            } else {
                ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Invalid ID format: " + ctx.pathParam("id"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getStatusCode()).result("An error occurred while retrieving the message: " + e.getMessage());
        }
    }

    private void createMessage(Context ctx) {
        Message message = ctx.bodyAsClass(Message.class);
        if (message.getText() == null || message.getText().length() > 255) {
            ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Message text should not be null and should be less than 256 characters");
        } else {
            Optional<Account> account = accountService.getAccount(message.getUserId());
            if (!account.isPresent()) {
                ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("User ID does not exist");
            } else {
                messageService.createMessage(message, account);
                ctx.status(HttpStatus.CREATED.getStatusCode());
            }
        }
    }

    public List<Message> getMessages() {
        return messageService.getAllMessages();
    }

    private void updateMessage(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Message updatedMessage = ctx.bodyAsClass(Message.class);
            if (updatedMessage.getText() == null || updatedMessage.getText().isEmpty()) {
                ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Message text cannot be empty");
                return;
            }
            if (updatedMessage.getText().length() > 200) {
                ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Message text is too long");
                return;
            }
            Message message = messageService.updateMessage(id, updatedMessage);
            if (message != null) {
                ctx.status(HttpStatus.OK.getStatusCode()).json(message);
            } else {
                ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Invalid ID format: " + ctx.pathParam("id"));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getStatusCode()).result("An error occurred while updating the message: " + e.getMessage());
        }
    }

    private void deleteMessage(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            messageService.deleteMessage(id);
            ctx.status(HttpStatus.NO_CONTENT.getStatusCode());
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST.getStatusCode()).result("Invalid ID format: " + ctx.pathParam("id"));
        } catch (NoSuchElementException e) {
            ctx.status(HttpStatus.NOT_FOUND.getStatusCode());
        }
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        new SocialMediaController(app);
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        new SocialMediaController(app);
        return app;
    }
}
