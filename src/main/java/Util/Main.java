package Util;

import Controller.SocialMediaController;
import Service.MessageService;
import io.javalin.Javalin;

/**
 * This class is provided with a main method to allow you to manually run and
 * test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
public class Main {
    public static void main(String[] args) {
        MessageService messageService = new MessageService();
        SocialMediaController controller = new SocialMediaController(messageService);
        Javalin app = controller.startAPI();
        app.start(8080);
    }
}
