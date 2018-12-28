package cf.cryptoclaim.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "cryptoClaimTenants")
public class CryptoClaimUser {

	@Id
	@org.springframework.data.mongodb.core.mapping.Field("_id")
	private String name;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private byte[] publicKey;
	private byte[] privateKey;

	public CryptoClaimUser(String name, String password, byte[] publicKey, byte[] privateKey) {
		super();
		this.name = name;
		this.password = password;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}
	
}
