package cf.cryptoclaim.crypto;

import static cf.cryptoclaim.constants.CryptoClaimConstants.C9M_MASTER_KEY;
import static cf.cryptoclaim.constants.CryptoClaimConstants.UTF8_ENCODING;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.exception.KeyDerivationException;
import cf.cryptoclaim.exception.MongoInconsistencyException;
import cf.cryptoclaim.model.CryptoClaimTenant;
import cf.cryptoclaim.model.CryptoMessage;
import cf.cryptoclaim.repositories.CryptoMessagesRepository;
import cf.cryptoclaim.repositories.TenantsRepository;

@Component
public class ClaimEncryptionService {

	private static final String RSA_ENCRYPTION_ALGORITHM = "RSA";
	private static final String AES_ENCRYPTION_ALGORITHM = "AES";
	private static final String AES_WITH_MODE = AES_ENCRYPTION_ALGORITHM + "/ECB/PKCS5Padding";

	private KeyFactory keyFactory;
	
	private Cipher symmetricCipher;
	private Cipher asymetricCipher;
	
	@Value("${" + C9M_MASTER_KEY + ":}")
	private String masterKey;
	
	@Autowired
	private TenantsRepository tenantsRepository;
	
	@Autowired
	private CryptoMessagesRepository cryptoMessagesRepository;
	
	public ClaimEncryptionService() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException {
		keyFactory = KeyFactory.getInstance(RSA_ENCRYPTION_ALGORITHM);
		
		symmetricCipher = Cipher.getInstance(AES_WITH_MODE);
		symmetricCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(masterKey.getBytes(UTF8_ENCODING), AES_ENCRYPTION_ALGORITHM));
		
		asymetricCipher = Cipher.getInstance(RSA_ENCRYPTION_ALGORITHM);
	}
	
	public void encryptMessage(String tenantName, byte[] message) throws CryptoClaimException {
		List<CryptoClaimTenant> tenantsResult = tenantsRepository.findByName(tenantName);
		
		validateGetResult(tenantsResult);
		CryptoClaimTenant tenant = tenantsResult.get(0);
		byte[] publicKeyBytes = tenant.getPublicKey();
		 
		PublicKey publicKey = derivePublicKey(publicKeyBytes);
		
		CryptoMessage cryptoMessage = new CryptoMessage();
		cryptoMessage.setEncryptedData(performAsymmetricEncryption(message, publicKey));
		cryptoMessage.setSendAt(new Date());
		cryptoMessage.setReceivingTenant(tenantName);
		cryptoMessage.setSendingTenant(null);
		
		cryptoMessagesRepository.save(cryptoMessage);
	}
	
	// TODO tenant derived from context
	public byte[] decryptMessage(byte[] message, String tenantName) throws CryptoClaimException {
		List<CryptoClaimTenant> tenantsResult = tenantsRepository.findByName(tenantName);
		
		validateGetResult(tenantsResult);
		CryptoClaimTenant tenant = tenantsResult.get(0);
		byte[] encrryptedPrivateKeyBytes = tenant.getPrivateKey();
		
		byte[] privateKeyBytes = performSymmetricDecryption(encrryptedPrivateKeyBytes);
		
		return performAsymmetricDecryption(message, privateKeyBytes);
	}
	
	private byte[] performSymmetricDecryption(byte[] encryptedKey) throws CryptoClaimException {
		try {
			return symmetricCipher.doFinal(encryptedKey);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoClaimException("Error while decrypting", e);
		}
	}
	
	private byte[] performAsymmetricDecryption(byte[] message, byte[] privateKeyBytes) throws CryptoClaimException {
		try {
			PrivateKey privateKey = derivePrivateKey(privateKeyBytes);
			asymetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			return asymetricCipher.doFinal(message);
		} catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
			throw new CryptoClaimException("Error while decrypting", e);
		}
	}

	private byte[] performSymmetricEncryption() {
		// TODO
		return null;
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
	
	private PrivateKey derivePrivateKey(byte[] privateKeyBytes) throws CryptoClaimException {
		try {
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
		} catch (InvalidKeySpecException e) {
			throw new KeyDerivationException("Error in deriving private key", e);
		}
	}
	
	private PublicKey derivePublicKey(byte[] publicKeyBytes) throws CryptoClaimException {
		try {
			return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
		} catch (InvalidKeySpecException e) {
			throw new KeyDerivationException("Error in deriving public key", e);
		}
	}
	
	private void validateGetResult(List<CryptoClaimTenant> result) {
		if (result == null || result.isEmpty()) {
			throw new NoSuchElementException("No results found");
		} else if (result.size() > 1) {
			throw new MongoInconsistencyException("Expected only one result");
		}
	}
}
