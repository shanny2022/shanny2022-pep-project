package Service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.MessageDao;
import DAO.DaoException;
import Model.Account;
import Model.Message;
import io.javalin.http.NotFoundResponse;

public class MessageService {
    private MessageDao messageDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private static final String DB_ACCESS_ERROR_MSG = "Error accessing the database";

    public MessageService() {
        messageDao = new MessageDao();
    }

    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public List<Message> getAllMessages() {
        LOGGER.info("Fetching all messages");
        try {
            List<Message> messages = messageDao.getAll();
            LOGGER.info("Fetched {} messages", messages.size());
            return messages;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        LOGGER.info("Fetching messages posted by ID account: {}", accountId);
        try {
            List<Message> messages = messageDao.getMessagesByAccountId(accountId);
            LOGGER.info("Fetched {} messages", messages.size());
            return messages;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public Message createMessage(Message message, Optional<Account> account) {
        LOGGER.info("Creating message: {}", message);

        if (!account.isPresent()) {
            throw new ServiceException("Account must exist when posting a new message");
        }

        validateMessage(message);
        checkAccountPermission(account.get(), message.getPosted_by());
        try {
            Message createdMessage = messageDao.insert(message);
            LOGGER.info("Created message: {}", createdMessage);
            return createdMessage;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public Message updateMessage(Message message) {
        LOGGER.info("Updating message: {}", message.getMessage_id());

        Optional<Message> retrievedMessage = Optional.ofNullable(this.getMessageById(message.getMessage_id()));

        if (!retrievedMessage.isPresent()) {
            throw new ServiceException("Message not found");
        }

        retrievedMessage.get().setMessage_text(message.getMessage_text());
        validateMessage(retrievedMessage.get());

        try {
            messageDao.update(retrievedMessage.get());
            LOGGER.info("Updated message: {}", message);
            return retrievedMessage.get();
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    public void deleteMessage(int id) {
        LOGGER.info("Deleting message: {}", id);
        try {
            Message message = getMessageById(id);

            if (message == null) {
                throw new NotFoundResponse("Message to delete not found");
            }

            boolean hasDeletedMessage = messageDao.delete(message);

            if (hasDeletedMessage) {
                LOGGER.info("Deleted message {}", id);
            } else {
                throw new NotFoundResponse("Message to delete not found");
            }
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    private void validateMessage(Message message) {
        LOGGER.info("Validating message: {}", message);
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            throw new ServiceException("Message text cannot be null or empty");
        }
        if (message.getMessage_text().length() > 254) {
            throw new ServiceException("Message text cannot exceed 254 characters");
        }
    }

    private void checkAccountPermission(Account account, int postedBy) {
        LOGGER.info("Checking account permissions for messages");
        if (account.getAccount_id() != postedBy) {
            throw new ServiceException("Account not authorized to modify this message");
        }
    }

    public Message getMessageById(int id) {
        try {
            Message message = messageDao.getMessage(id);
            if (message == null) {
                throw new ServiceException("No message found with ID: " + id);
            }
            return message;
        } catch (DaoException e) {
            LOGGER.error("Error while retrieving message from the database", e);
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Implement these methods or remove them if they are not needed
    public List<Message> getAllMessagesForUser(int id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Message updateMessage(int id, Message updatedMessage) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Message getMessage(int id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
