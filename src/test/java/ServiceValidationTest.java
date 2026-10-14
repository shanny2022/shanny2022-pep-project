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
}
