
package cf.cryptoclaim.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import cf.cryptoclaim.constants.CryptoClaimConstants;

@Document(collection = "jwts")
@CompoundIndexes({ @CompoundIndex(name = "jwtIndex", def = "{'issuer' : 1, 'jti': 1, 'type':1}", unique = true) })
public class ConsumedJWT {
  public static final int CONSUMED_JWT_EXPIRE_SECONDS = CryptoClaimConstants.JWT_CLAIM_IAT_CLOCK_SKEW_SECONDS * 2;

  @Indexed(name = "consumedAtFieldIndex", expireAfterSeconds = CONSUMED_JWT_EXPIRE_SECONDS)
  private Date consumedAt;

  private String issuer;

  private String jti;

  public Date getConsumedAt() {
    return consumedAt;
  }

  public void setConsumedAt(Date consumedAt) {
    this.consumedAt = consumedAt;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getJti() {
    return jti;
  }

  public void setJti(String jti) {
    this.jti = jti;
  }

}
