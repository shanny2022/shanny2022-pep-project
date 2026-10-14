import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ServiceValidationTest {
    private final SocialMediaDAO dao = mock(SocialMediaDAO.class);
    private final SocialMediaService service = new SocialMediaService(dao);

    @Test public void nullRegistrationDoesNotReachDatabase() {
        assertNull(service.register(null));
        verifyNoInteractions(dao);
    }
    @Test public void missingRegistrationFields() {
        assertNull(service.register(new Account()));
        verifyNoInteractions(dao);
    }
    @Test public void shortPassword() {
        assertNull(service.register(new Account("user", "abc")));
        verifyNoInteractions(dao);
    }
    @Test public void minimumPasswordAccepted() {
        Account account = new Account("user", "abcd");
        when(dao.insertAccount(account)).thenReturn(account);
        assertSame(account, service.register(account));
    }
    @Test public void missingLoginFields() {
        assertNull(service.login(new Account()));
        assertNull(service.login(null));
        verifyNoInteractions(dao);
    }
    @Test public void missingMessageText() {
        assertNull(service.createMessage(new Message()));
        assertNull(service.createMessage(null));
        verifyNoInteractions(dao);
    }
    @Test public void tooLongMessage() {
        assertNull(service.createMessage(new Message(1, "x".repeat(255), 1L)));
        verifyNoInteractions(dao);
    }
    @Test public void maximumMessageAccepted() {
        Message message = new Message(1, "x".repeat(254), 1L);
        when(dao.insertMessage(message)).thenReturn(message);
        assertSame(message, service.createMessage(message));
    }
}
