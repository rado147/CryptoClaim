package cf.cryptoclaim.crypto;

import static cf.cryptoclaim.constants.CryptoClaimConstants.C9M_MASTER_KEY;
import static cf.cryptoclaim.constants.CryptoClaimConstants.RSA_ENCRYPTION_ALGORITHM;
import static cf.cryptoclaim.constants.CryptoClaimConstants.UTF8_ENCODING;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.exception.MongoInconsistencyException;
import cf.cryptoclaim.model.CryptoClaimUser;
import cf.cryptoclaim.model.CryptoMessage;
import cf.cryptoclaim.repositories.CryptoMessagesRepository;
import cf.cryptoclaim.repositories.UsersRepository;

@Component
public class ClaimEncryptionService {

	private static final String AES_ENCRYPTION_ALGORITHM = "AES";
	private static final String AES_WITH_MODE = AES_ENCRYPTION_ALGORITHM + "/ECB/PKCS5Padding";

	private KeyPairManager keyPairManager;
	
	private Cipher symmetricCipher;
	private Cipher asymetricCipher;
	
	@Value("${" + C9M_MASTER_KEY + ":}")
	private String masterKey;
	
	private SecretKeySpec secretKeySpec;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private CryptoMessagesRepository cryptoMessagesRepository;
	
	@PostConstruct
	public void init() throws UnsupportedEncodingException {
		secretKeySpec = new SecretKeySpec(masterKey.getBytes(UTF8_ENCODING), AES_ENCRYPTION_ALGORITHM);
	}
	
	public ClaimEncryptionService() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
		keyPairManager = KeyPairManager.getInstance();
		
		symmetricCipher = Cipher.getInstance(AES_WITH_MODE);
		asymetricCipher = Cipher.getInstance(RSA_ENCRYPTION_ALGORITHM);
	}
	
	public CryptoClaimUser registerTenant(String username, String password) throws CryptoClaimException {
		KeyPair keyPair = keyPairManager.generateKeyPair();
		
		CryptoClaimUser user = new CryptoClaimUser(username, password, keyPair.getPublic().getEncoded(), performSymmetricEncryption(keyPair.getPrivate().getEncoded()));
		
		return usersRepository.save(user);
	}
	
	public String getPublicKey(String username) throws CryptoClaimException {
		List<CryptoClaimUser> result = usersRepository.findByName(username);
		
		validateGetResult(result);
		CryptoClaimUser user = result.get(0);
		return new String(user.getPublicKey());
	}
	
	
	public void encryptMessage(String username, byte[] message) throws CryptoClaimException {
		List<CryptoClaimUser> result = usersRepository.findByName(username);
		validateGetResult(result);
		CryptoClaimUser tenant = result.get(0);

		byte[] publicKeyBytes = tenant.getPublicKey();
		
		CryptoMessage cryptoMessage = new CryptoMessage();
		cryptoMessage.setEncryptedData(performAsymmetricEncryption(message, keyPairManager.derivePublicKey(publicKeyBytes)));
		cryptoMessage.setSendAt(new Date());
		cryptoMessage.setReceivingTenant(username);
		// ??
		cryptoMessage.setSendingTenant(null);
		
		cryptoMessagesRepository.save(cryptoMessage);
	}
	
	public byte[] decryptMessage(byte[] message, String tenantName) throws CryptoClaimException {
		List<CryptoClaimUser> tenantsResult = usersRepository.findByName(tenantName);
		
		validateGetResult(tenantsResult);
		CryptoClaimUser tenant = tenantsResult.get(0);
		byte[] encrryptedPrivateKeyBytes = tenant.getPrivateKey();
		
		return performAsymmetricDecryption(message, keyPairManager.derivePrivateKey(performSymmetricDecryption(encrryptedPrivateKeyBytes)));
	}
	
	// symmetric operations
	private byte[] performSymmetricDecryption(byte[] data) throws CryptoClaimException {
		try {
			switchCipherToDecryptionMode(symmetricCipher);
			
			return symmetricCipher.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			throw new CryptoClaimException("Error while decrypting", e);
		}
	}

	private byte[] performSymmetricEncryption(byte[] data) throws CryptoClaimException {
		try {
			switchCipherToEncryptionMode(symmetricCipher);
			
			return symmetricCipher.doFinal(data);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoClaimException("Error while encrypting", e);
		}
	}
	
	// asymmetric operations
	private byte[] performAsymmetricDecryption(byte[] message, PrivateKey privateKey) throws CryptoClaimException {
		try {
			asymetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			return asymetricCipher.doFinal(message);
		} catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
			throw new CryptoClaimException("Error while decrypting", e);
		}
	}
	
	private byte[] performAsymmetricEncryption(byte[] message, PublicKey publicKey) throws CryptoClaimException {
		byte[] encryptedData = null;
		try {
			asymetricCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			encryptedData = asymetricCipher.doFinal(message);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoClaimException("Error while encrypting", e);
		}
		
		return encryptedData;
	}
	
	private void switchCipherToEncryptionMode(Cipher cipher) throws InvalidKeyException {
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	}
	
	private void switchCipherToDecryptionMode(Cipher cipher) throws InvalidKeyException {
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
	}
	
	private void validateGetResult(List<CryptoClaimUser> result) {
		if (result == null || result.isEmpty()) {
			throw new NoSuchElementException("No results found");
		} else if (result.size() > 1) {
			throw new MongoInconsistencyException("Expected only one result");
		}
	}
}
