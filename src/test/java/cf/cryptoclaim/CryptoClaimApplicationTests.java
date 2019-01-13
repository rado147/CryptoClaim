package cf.cryptoclaim;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import cf.cryptoclaim.auth.JWTService;
import cf.cryptoclaim.controller.HttpRequestLengthInterceptor;
import cf.cryptoclaim.crypto.ClaimEncryptionService;
import cf.cryptoclaim.repositories.ConsumedJWTRepository;
import cf.cryptoclaim.repositories.CryptoMessagesRepository;
import cf.cryptoclaim.repositories.UsersRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptoClaimApplicationTests {

	@MockBean
	private UsersRepository usersRepository;
	@MockBean
	private CryptoMessagesRepository ryptoMessagesRepository;
	@MockBean
	private ConsumedJWTRepository consumedJWTRepository;
	@MockBean
	private JWTService jwtService;
	@MockBean
	private ClaimEncryptionService claimEncryptionService;
	/*@MockBean
	private HttpRequestLengthInterceptor httpRequestLengthInterceptor;
	*/
	@Test
	public void contextLoads() {
	}

}
