# Issue JWT Token

### This is an example class that might be used for issuing JWT tokens

##### JWT tokens are used for authentication against the RESTful API of CryptoClaim

```java

package test.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

public class SignedJWT {
	
  private static final String PRIVATE_KEY = "<-- PRIVATE_KEY -->";
  private static final String AUDIENCE_URL = "https://cryptoclaim.cfapps.sap.hana.ondemand.com/<postfix>";
  private static final String PASSWORD_CLAIM = "psw";
  
  public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {	
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    byte[] binaryKey = Base64.getDecoder().decode(PRIVATE_KEY);
    RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(new PKCS8EncodedKeySpec(binaryKey));  
    Algorithm algorithm = Algorithm.RSA256(null, privateKey);
    Builder jwtBuilder = JWT.create();
    jwtBuilder.withIssuer("<issuer>");
    jwtBuilder.withAudience(AUDIENCE_URL);
    jwtBuilder.withIssuedAt(new Date());
    jwtBuilder.withJWTId(UUID.randomUUID().toString());
    jwtBuilder.withClaim(PASSWORD_CLAIM, "<password>");

    String signedJWT = jwtBuilder.sign(algorithm);
    
    System.out.println(signedJWT);
  }

}

```

##### An example result (JWT token) should look like:
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJodHRwczovL2NyeXB0b2NsYWltLmNmYXBwcy5zYXAuaGFuYS5vbmRlbWFuZC5jb20vc2VuZCIsInBzdyI6InByb2JhMyIsImlzcyI6InByb2JhMyIsImlhdCI6MTU0NjEwNTQ5NSwianRpIjoiYTI4ZDk4ZDItMTdlOS00Zjk3LWFmNWUtNWUwMTE1ZGM2MDhkIn0.ROqM7_6GVU7tqaaltqwUlR33kWMQUXudqeOTPUlDgGfQpM1HTbKeh906Pes1POaHRXmbcStcQYUNj6dCnFT0TADEyR9vG8Hs4EIZpcsL_8J4Q2bu01Xhx88-ubA7P_ryh3j_8mRQ9qC8mhD57oscuGVnFkGzEvhDn0oHH24qUtkFpG-h3Biwk0WLKQgMcNW9bUO2O2Jux4TkrH-0lhfEyGvOHFry4QzS-IO4hjkafePfn3F_OMvbCNlSzDk4sZ7cRdSUWLpY4yCgHsKIqBn4nQao-eiXWP8_z6yNpYwg5rIuWoYLEZ0FjHUqF8tndNX7Q3sfJjZUVc5UX-CHIbcBDA

