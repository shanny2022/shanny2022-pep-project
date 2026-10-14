package Service;

import java.util.List;
import DAO.SocialMediaDAO;
import Model.*;

/**
 * Service that connects controller endpoints to data access objects
 * method naming terminology uses CRUD verbs for what action is being taken
 * It also checks requirements like password length and message length
 * TODO After project is complete, implement checking the login for the request sender
 * Could also implement a role base relational table for a list of users that can: view, edit, and delete based on role.
 */
public class SocialMediaService {
    private static final int MAX_MESSAGE_LENGTH = 254;
    private static final int MIN_PASSWORD_LENGTH = 4;

    //class field for the data access object
    SocialMediaDAO socialMediaDAO;

    public SocialMediaService(){
        this.socialMediaDAO = new SocialMediaDAO();
    }

    public SocialMediaService(SocialMediaDAO dao) {
        this.socialMediaDAO = java.util.Objects.requireNonNull(dao);
    }

    /**
     * Used to add account record to database
     *
     * @param account account object using the constructor without the id as a parameter
     * @return returns account with id value or null if there was an error
     */
    public Account register(Account account) {

        //Try catch block for catching unexpected errors and returning null
        try {

            //Checks message requirements and if it fails returns null
            if(account == null || account.getUsername() == null || account.getPassword() == null
                    || account.getUsername().isEmpty() || account.getPassword().length() < MIN_PASSWORD_LENGTH) {
                return null;
            }
            //Otherwise return registered account
            else {
                return this.socialMediaDAO.insertAccount(account);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;


    }
    public SocialMediaService(SocialMediaDAO dao) {
        this.socialMediaDAO = java.util.Objects.requireNonNull(dao);
    }

    /**
     * Used to add account record to database
     * @param account account object using the constructor without the id as a parameter
     * @return returns account with id value or null if there was an error or account doesn't exist
     */
    public Account login(Account account) {
        if (account == null || account.getUsername() == null || account.getPassword() == null) {
            return null;
        }
        return this.socialMediaDAO.selectAccount(account);
    }
    /**
     * Method for adding a message record
     *
     * @param message message object to be posted
     * @return null or message if it was inserted
     */
    public Message createMessage(Message message) {

        //Try catch block for catching unexpected errors and returning null
        try {
            //Checks message requirements and if it fails returns null
            if(message == null || message.getMessage_text() == null
                    || message.getMessage_text().length() > MAX_MESSAGE_LENGTH || message.getMessage_text().isEmpty()) {
                return null;
            }
            else {
                return this.socialMediaDAO.insertMessage(message);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    /**
     * Returns message from a given message id
     * @param account_id integer - id value of an account
     * @return Message object null on fail or none
     */
    public Message readMessage(int message_id){

        //Try catch block for catching unexpected errors and returning null
        try {
            return this.socialMediaDAO.selectMessage(message_id);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    /**
     * Returns all Messages
     * @return List<Message> - List of Messages object for all messages, null on fail or none
     */
    public List<Message> readAllMessages(){
        try {
            return this.socialMediaDAO.selectAllMessages();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    /**
     * Returns all Messages from a given account id
     * @param account_id integer - id value of an account
     * @return List<Message> - List of Message object for that account, null on fail or none
     */
    public List<Message> readAllMessagesById(int account_id){
        try {
            return this.socialMediaDAO.selectAllMessagesFromAccountId(account_id);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    /**
     * Updates message given a message. Id value was added to the method in the controller
     * @param message message object to be updated
     * @return updated message or null
     */
    public Message updateMessage(Message message){
        try {
            //Checks message requirements and if it fails returns null
            if(message == null || message.getMessage_text() == null
                    || message.getMessage_text().length() > MAX_MESSAGE_LENGTH || message.getMessage_text().isEmpty()) {
                return null;
            }
            else {
                return this.socialMediaDAO.updateMessage(message.getMessage_id(), message.getMessage_text());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    /**
     * Service Method to delete a message from a message id
     * @param message_id integer: id value of the message
     * @return returns the deleted message object on successful deletion, null on failure or error
     */
    public Message deleteMessage(int message_id){
        try {
            return this.socialMediaDAO.deleteMessage(message_id);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}