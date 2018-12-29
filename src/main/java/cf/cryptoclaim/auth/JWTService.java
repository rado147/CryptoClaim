package cf.cryptoclaim.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import cf.cryptoclaim.constants.CryptoClaimConstants;
import cf.cryptoclaim.exception.AuthenticationFailedException;
import cf.cryptoclaim.exception.WrongPasswordException;
import cf.cryptoclaim.model.ConsumedJWT;
import cf.cryptoclaim.model.CryptoClaimClient;
import cf.cryptoclaim.repositories.ConsumedJWTRepository;
import cf.cryptoclaim.repositories.UsersRepository;

@Component
public class JWTService {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private ConsumedJWTRepository consumedJWTRepository;
	
	private MessageDigest messageDigest;
	
	private static final String PASSWORD_CLAIM = "psw";

	public JWTService() throws NoSuchAlgorithmException {
		messageDigest = MessageDigest.getInstance(CryptoClaimConstants.HASHING_ALGORITHM);
	}
	
	public DecodedJWT verifyJWT(HttpServletRequest httpRequest, String assertion, String clientId, RSAPublicKey publicKey)		       {
		    Algorithm algorithm = Algorithm.RSA256(publicKey, null);
		    Verification verification = JWT.require(algorithm);
		    verification.withIssuer(clientId);
		    verification.withAudience(httpRequest.getRequestURL().toString());
		    verification.acceptIssuedAt(300);
		    JWTVerifier verifier = verification.build();
		    DecodedJWT decodedJWT;
		    try {
		      decodedJWT = verifier.verify(assertion);
		      verifyAdditionalClaims(decodedJWT, clientId);
		      finalizeAndRegister(decodedJWT);
	    } catch (JWTVerificationException e) {
	    	throw new AuthenticationFailedException("Authentication failed", e);
	    }
	    return decodedJWT;
	  }
	
	private void verifyAdditionalClaims(DecodedJWT decodedJWT, String username) {
		CryptoClaimClient user = usersRepository.findByName(username).get(0);
		
		if(!Arrays.equals(user.getPassword(), messageDigest.digest(decodedJWT.getClaim(PASSWORD_CLAIM).asString().getBytes(StandardCharsets.UTF_8)))) {
			throw new WrongPasswordException("Password doesn't match the user");
		}
	}
	
	public void finalizeAndRegister(DecodedJWT decodedJWT) {

	    ConsumedJWT consumedJWT = new ConsumedJWT();
	    consumedJWT.setIssuer(decodedJWT.getIssuer());
	    consumedJWT.setJti(decodedJWT.getId());
	    consumedJWT.setConsumedAt(new Date());
	    try {
	      consumedJWTRepository.insert(consumedJWT);
	    } catch (DuplicateKeyException e) {
	      throw new JWTVerificationException("JWT verification failed: The Token had already been used.", e);
	    }
	  }
	
}
