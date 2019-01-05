package cf.cryptoclaim.crypto;

import static cf.cryptoclaim.constants.CryptoClaimConstants.C9M_MASTER_KEY;
import static cf.cryptoclaim.constants.CryptoClaimConstants.RSA_ENCRYPTION_ALGORITHM;
import static cf.cryptoclaim.constants.CryptoClaimConstants.UTF8_ENCODING;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import cf.cryptoclaim.constants.CryptoClaimConstants;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.exception.CryptoClaimRuntimeException;
import cf.cryptoclaim.exception.MongoInconsistencyException;
import cf.cryptoclaim.model.CryptoClaimClient;
import cf.cryptoclaim.model.CryptoMessage;
import cf.cryptoclaim.model.MessageInformation;
import cf.cryptoclaim.repositories.CryptoMessagesRepository;
import cf.cryptoclaim.repositories.UsersRepository;

@Component
public class ClaimEncryptionService {

	private static final String SEND_AT_ATTRIBUTE = "sendAt";
	private static final String ID_ATTRIBUTE = "id";
	private static final String SENDING_CLIENT_ATTRIBUTE = "sendCLIENT";
	private static final Set<String> VALID_LISTING_ATTRIBUTES = new HashSet<>(Arrays.asList(SEND_AT_ATTRIBUTE, ID_ATTRIBUTE, SENDING_CLIENT_ATTRIBUTE));
	
	private static final String AES_ENCRYPTION_ALGORITHM = "AES";
	private static final String AES_WITH_MODE = AES_ENCRYPTION_ALGORITHM + "/ECB/PKCS5Padding";

	private KeyPairManager keyPairManager;
	
	private Cipher symmetricCipher;
	private Cipher asymmetricCipher;
	
	private MessageDigest messageDigest;
	
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
		asymmetricCipher = Cipher.getInstance(RSA_ENCRYPTION_ALGORITHM);
		
		messageDigest = MessageDigest.getInstance(CryptoClaimConstants.HASHING_ALGORITHM);
	}
	
	public Map<String, Object> registerTenant(String clientId, String password) throws CryptoClaimException {
		if(password.length() < 5 || clientId.length() < 5) {
			throw new IllegalArgumentException("Arguments should fulfill the requirements");
		}
		
		KeyPair keyPair = keyPairManager.generateKeyPair();
		
		if(usersRepository.existsByName(clientId)) {
			throw new CryptoClaimException("User with the given username already exists");
		}
		byte[] privateKey = keyPair.getPrivate().getEncoded();
		
		Map<String, Object> propertiesToReturn = new HashMap<>();
		propertiesToReturn.put("name", clientId);
		propertiesToReturn.put("private_key", privateKey);
		
		CryptoClaimClient user = new CryptoClaimClient(clientId, messageDigest.digest(password.getBytes(StandardCharsets.UTF_8)), keyPair.getPublic().getEncoded(), performSymmetricEncryption(privateKey));
		
		usersRepository.save(user);
		
		return propertiesToReturn;
	}
	
	public String getPublicKeyAsString(String clientId) {
		List<CryptoClaimClient> result = usersRepository.findByName(clientId);
		
		validateGetResult(result);
		CryptoClaimClient user = result.get(0);
		return new String(user.getPublicKey());
	}
	
	public RSAPublicKey getPublicKey(String clientId) throws CryptoClaimException {
		List<CryptoClaimClient> result = usersRepository.findByName(clientId);
		
		validateGetResult(result);
		CryptoClaimClient user = result.get(0);
		
		return (RSAPublicKey) keyPairManager.derivePublicKey(user.getPublicKey());
	}
	
	public void encryptMessageAndSave(String receivingClientId, String sendingClientId, CryptoMessage cryptoMessage) throws CryptoClaimException {
		if(cryptoMessage.getReceivingClient() == null) {
			throw new CryptoClaimRuntimeException("Receiving client not set");
		}
		cryptoMessage.setSendAt(new Date());
		cryptoMessage.setSendingClient(sendingClientId);
		
		
		List<CryptoClaimClient> result = usersRepository.findByName(receivingClientId);
		validateGetResult(result);
		CryptoClaimClient client = result.get(0);

		byte[] publicKeyBytes = client.getPublicKey();
		
		cryptoMessage.setEncryptedData(performAsymmetricEncryption(cryptoMessage.getRawData(), keyPairManager.derivePublicKey(publicKeyBytes)));
		cryptoMessage.setRawData(null);
		
		cryptoMessagesRepository.save(cryptoMessage);
	}
	
	public CryptoMessage decryptMessage(String clientId, String messageId) throws CryptoClaimException {
		List<CryptoClaimClient> result = usersRepository.findByName(clientId);
		
		validateGetResult(result);
		CryptoClaimClient client = result.get(0);
		byte[] encrryptedPrivateKeyBytes = client.getPrivateKey();
		
		Optional<CryptoMessage> cryptoMessageOptional = cryptoMessagesRepository.findById(messageId);
		if(!cryptoMessageOptional.isPresent()) {
			throw new CryptoClaimException("Message not found");
		}
		
		CryptoMessage cryptoMessage = cryptoMessageOptional.get();
		cryptoMessage.setRawData(performAsymmetricDecryption(cryptoMessage.getEncryptedData(), keyPairManager.derivePrivateKey(performSymmetricDecryption(encrryptedPrivateKeyBytes))));
	
		return cryptoMessage;
	}
	
	public Page<MessageInformation> getMessages(String clientId, Set<String> attributes, Pageable pageable) {
		Page<MessageInformation> messages = cryptoMessagesRepository.findByReceivingClient(clientId, getValidPageable(pageable));

		if(attributes != null && !attributes.isEmpty()) {
			prepareMessagesForListing(getValidAttributes(attributes), messages);
		}
		
		return messages;
	}
	
	private Pageable getValidPageable(Pageable pageable) {
		if(pageable.getPageSize() > CryptoClaimConstants.MAX_PAGE_SIZE || pageable.getPageSize() < 0) {
			return PageRequest.of(pageable.getPageNumber(), CryptoClaimConstants.DEFAULT_PAGE_SIZE, pageable.getSort());
		}
		return pageable;
	}
	
	private void prepareMessagesForListing(Set<String> attributes, Page<MessageInformation> messages) {
		if(!attributes.contains(SEND_AT_ATTRIBUTE)) {
			for(MessageInformation message : messages) {
				message.setSendAt(null);
			}
		}
		if(!attributes.contains(SENDING_CLIENT_ATTRIBUTE)) {
			for(MessageInformation message : messages) {
				message.setSendingClient(null);
			}
		}
		if(!attributes.contains(ID_ATTRIBUTE)) {
			for(MessageInformation message : messages) {
				message.setId(null);
			}
		}
	}
	
	private Set<String> getValidAttributes(Set<String> attributes) {
		Set<String> validAttributes = new HashSet<>();
		for(String attribute : attributes) {
			if(VALID_LISTING_ATTRIBUTES.contains(attribute)) {
				validAttributes.add(attribute);
			}
		}
		return validAttributes;
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
			asymmetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			return asymmetricCipher.doFinal(message);
		} catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
			throw new CryptoClaimException("Error while decrypting", e);
		}
	}
	
	private byte[] performAsymmetricEncryption(byte[] message, PublicKey publicKey) throws CryptoClaimException {
		byte[] encryptedData = null;
		try {
			asymmetricCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			encryptedData = asymmetricCipher.doFinal(message);
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
	
	private void validateGetResult(List<CryptoClaimClient> result) {
		if (result == null || result.isEmpty()) {
			throw new NoSuchElementException("No results found");
		} else if (result.size() > 1) {
			throw new MongoInconsistencyException("Expected only one result");
		}
	}
}
