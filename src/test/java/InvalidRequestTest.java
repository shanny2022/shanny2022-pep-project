import Controller.SocialMediaController;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class InvalidRequestTest {
    private Javalin app;
    private String baseUrl;
    private final HttpClient client = HttpClient.newHttpClient();

    @Before public void start() {
        ConnectionUtil.resetTestDatabase();
        app = new SocialMediaController().startAPI().start(0);
        baseUrl = "http://localhost:" + app.port();
    }

    @After public void stop() {
        if (app != null) app.stop();
    }

    private int status(String method, String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
            .header("Content-Type", "application/json")
            .method(method, HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
    }

    @Test public void malformedJsonReturnsBadRequest() throws Exception {
        assertEquals(400, status("POST", "/register", "{"));
        assertEquals(400, status("POST", "/login", "{"));
        assertEquals(400, status("POST", "/messages", "{"));
        assertEquals(400, status("PATCH", "/messages/1", "{"));
    }

    @Test public void invalidPathIdsReturnBadRequest() throws Exception {
        assertEquals(400, status("GET", "/messages/abc", ""));
        assertEquals(400, status("DELETE", "/messages/abc", ""));
        assertEquals(400, status("PATCH", "/messages/abc", "{}"));
        assertEquals(400, status("GET", "/accounts/99999999999999/messages", ""));
    }

    @Test public void nullPatchReturnsBadRequest() throws Exception {
        assertEquals(400, status("PATCH", "/messages/1", "null"));
    }
}
