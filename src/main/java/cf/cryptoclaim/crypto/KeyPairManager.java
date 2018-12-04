package cf.cryptoclaim.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import cf.cryptoclaim.constants.CryptoClaimConstants;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.exception.KeyDerivationException;

public class KeyPairManager {
	
	private static final int KEY_SIZE = 2 * 1024;
	
	private KeyFactory keyFactory;
	private KeyPairGenerator keyPairGenerator;

	private static KeyPairManager keyPairBuilder;
	
	public static KeyPairManager getInstance() throws NoSuchAlgorithmException {
		if(keyPairBuilder == null) {
			keyPairBuilder = new KeyPairManager(KeyPairGenerator.getInstance(CryptoClaimConstants.RSA_ENCRYPTION_ALGORITHM), 
					KeyFactory.getInstance(CryptoClaimConstants.RSA_ENCRYPTION_ALGORITHM));
		}
		return keyPairBuilder;
	}
	
	private KeyPairManager(KeyPairGenerator keyPairGenerator, KeyFactory keyFactory) {
		this.keyPairGenerator = keyPairGenerator;
		this.keyPairGenerator.initialize(KEY_SIZE);
		this.keyFactory = keyFactory;
	}
	
	KeyPair generateKeyPair() {
		return keyPairGenerator.generateKeyPair();
	}
	
	PublicKey derivePublicKey(byte[] publicKeyBytes) throws CryptoClaimException {
		try {
			return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
		} catch (InvalidKeySpecException e) {
			throw new KeyDerivationException("Error in deriving public key", e);
		}
	}
	
	PrivateKey derivePrivateKey(byte[] privateKeyBytes) throws CryptoClaimException {
		try {
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
		} catch (InvalidKeySpecException e) {
			throw new KeyDerivationException("Error in deriving private key", e);
		}
	}
	
}
