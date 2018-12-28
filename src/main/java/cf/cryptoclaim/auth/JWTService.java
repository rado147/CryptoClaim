package cf.cryptoclaim.auth;

import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import cf.cryptoclaim.exception.AuthenticationFailedException;
import cf.cryptoclaim.exception.WrongPasswordException;
import cf.cryptoclaim.model.CryptoClaimUser;
import cf.cryptoclaim.repositories.UsersRepository;

@Component
public class JWTService {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UsersRepository usersRepository;
	
	private static final String PASSWORD_CLAIM = "psw";
	
	public DecodedJWT verifyJWT(HttpServletRequest httpRequest, String assertion, String username, RSAPublicKey publicKey)		       {
		    Algorithm algorithm = Algorithm.RSA256(publicKey, null);
		    Verification verification = JWT.require(algorithm);
		    verification.withIssuer(username);
		    verification.withAudience(httpRequest.getRequestURL().toString());
		    verification.acceptIssuedAt(300);
		    JWTVerifier verifier = verification.build();
		    DecodedJWT decodedJWT;
		    try {
		      decodedJWT = verifier.verify(assertion);
		      verifyAdditionalClaims(decodedJWT, username);
	    } catch (JWTVerificationException e) {
	    	throw new AuthenticationFailedException("Authentication failed", e);
	    }
	    return decodedJWT;
	  }
	
	private void verifyAdditionalClaims(DecodedJWT decodedJWT, String username) {
		CryptoClaimUser user = usersRepository.findByName(username).get(0);
		
		if(!user.getPassword().equals(passwordEncoder.encode(decodedJWT.getClaim(PASSWORD_CLAIM).asString()))) {
			throw new WrongPasswordException("Password doesn't match the user");
		}
	}
	
}
