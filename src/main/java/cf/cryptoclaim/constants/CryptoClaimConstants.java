package cf.cryptoclaim.constants;

public class CryptoClaimConstants {

	private CryptoClaimConstants() {
		// nothing to initialize
	}
	
	public static final String C9M_DATABASE_NAME = "cryptoClaim";
	public static final String C9M_MONGODB_PASSWORD_KEY = "C9M_MONGODB_PASSWORD"; 
	public static final String C9M_MONGODB_USERNAME_KEY = "C9M_MONGODB_USERNAME"; 
	public static final String C9M_MASTER_KEY = "C9M_MASTER_KEY";
	public static final String UTF8_ENCODING = "UTF-8";
	public static final String RSA_ENCRYPTION_ALGORITHM = "RSA";
	public static final String HASHING_ALGORITHM = "SHA-256";
	public static final int JWT_CLAIM_IAT_CLOCK_SKEW_SECONDS = 300;
}
