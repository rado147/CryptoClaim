package cf.cryptoclaim.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "cryptoClaimTenants")
public class CryptoClaimTenant {

	@Id
	@org.springframework.data.mongodb.core.mapping.Field("_id")
	private String name;
	@JsonIgnore
	private byte[] publicKey;
	@JsonIgnore
	private byte[] privateKey;

	public CryptoClaimTenant(String name, byte[] publicKey, byte[] privateKey) {
		super();
		this.name = name;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
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
