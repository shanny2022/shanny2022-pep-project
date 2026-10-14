package Controller;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 * Controller uses Rest related verbs to describe method behavior.
 */

 
public class SocialMediaController {

    private final ObjectMapper mapper = new ObjectMapper();

    //Class field for service object for handler functions to reference to
    private SocialMediaService socialMediaService = new SocialMediaService();

    /**
     * Creates an instance of a Javalin Jettyserver.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.exception(JsonProcessingException.class, (exception, context) -> context.status(400));
        app.exception(NumberFormatException.class, (exception, context) -> context.status(400));
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::patchMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByIdHandler);
        return app;
    }

    /**
     * Uses Object mapper to turn json request body into an account object
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    private void registerHandler(Context context) throws JsonMappingException, JsonProcessingException {

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Converts body json to account object
        Account account = mapper.readValue(context.body(), Account.class);

        //Sends body json to service to register the account
        Account addedAccount = socialMediaService.register(account);

        //Checks if null for when an error occurred
        if (addedAccount == null) {
            context.status(400); //General Client Error
        } else {
            context.json(mapper.writeValueAsString(addedAccount));
        }
    }
    /**
     *
     * @param context
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    private void loginHandler(Context context) throws JsonMappingException, JsonProcessingException {

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Converts body json to account object
        Account account = mapper.readValue(context.body(), Account.class);
        //Sends body json to service to validate the login
        Account addedAccount = socialMediaService.login(account);

        //Checks if null for when an error occurred
        if (addedAccount == null) {
            context.status(401); //Unauthorized: Request not valid
        } else {
            context.json(mapper.writeValueAsString(addedAccount));
        }
    }
    /**
     *
     * @param context
     * @throws JsonProcessingException
     */
    private void getMessageHandler(Context context) throws JsonProcessingException{

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Gets id value from endpoint url
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        //Gets message object from service read message method
        Message message = socialMediaService.readMessage(message_id);

        //If message null still return 200 but without an object in response body.
        if (message == null) {
            context.status(200); //Default successful response
        } else {
            context.json(mapper.writeValueAsString(message));
        }
    }
    /**
     *
     * @param context
     * @throws JsonProcessingException
     */
    private void getAllMessagesHandler(Context context) throws JsonProcessingException{

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Returns List of Messages or null on error from service
        List<Message> messages = socialMediaService.readAllMessages();

        //If null return 200 without a response body
        if (messages == null) {
            context.status(200); //Default successful response
        } else {
            //return all messages
            context.json(mapper.writeValueAsString(messages));
        }
    }
    /**
     *
     * @param context
     * @throws JsonProcessingException
     */
    private void getMessagesByIdHandler(Context context) throws JsonProcessingException{

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Gets account id from url path
        int account_id = Integer.parseInt(context.pathParam("account_id"));

        //Creates a list of messages from service's read all messages or null on errors
        List<Message> messages = socialMediaService.readAllMessagesById(account_id);

        //If null then an error occurred otherwise return object
        if (messages == null) {
            context.status(200); //Deafult successful response
        } else {
            context.json(mapper.writeValueAsString(messages));
        }
    }
    /**
     *
     * @param context
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    private void postMessageHandler(Context context) throws JsonMappingException, JsonProcessingException{

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Converts body json to message object
        Message message = mapper.readValue(context.body(), Message.class);

        //Set message to response from service which is the new value or null on error
        message = socialMediaService.createMessage(message);

        //If null then error occurred, otherwise return object
        if (message == null) {
            context.status(400); //General Client Error
        } else {
            context.status(200).json(mapper.writeValueAsString(message));
        }
    }
    /**
     *
     * @param context
     * @throws JsonProcessingException
     */
    private void patchMessageHandler(Context context) throws JsonProcessingException{

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();

        //Gets id value from endpoint url
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        //Converts body json to message object and sets the object to have the id from path
        Message message = mapper.readValue(context.body(), Message.class);
        message.setMessage_id(message_id);

        //Returns message or null to new message object
        message = socialMediaService.updateMessage(message);

        //Using a new object
        if (message == null) {
            context.status(400); //General Client Error
        } else {
            context.json(mapper.writeValueAsString(message));
        }
    }

    /**
     *
     * @param context
     * @throws JsonProcessingException
     */
    private void deleteMessageHandler(Context context) throws JsonProcessingException{

        //Uses Jackson object mapper to serialize objects
        ObjectMapper mapper = new ObjectMapper();


        //Get message id from url path
        int message_id = Integer.parseInt(context.pathParam("message_id"));

        //sets message object to deleted message or null on failure
        Message message = socialMediaService.deleteMessage(message_id);

        //If null return empty body with default response
        if (message == null) {
            context.status(200); //Deafult successful response
        } else {
            context.json(mapper.writeValueAsString(message));
        }
    }
}
