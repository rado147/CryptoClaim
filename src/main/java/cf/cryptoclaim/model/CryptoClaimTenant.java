package cf.cryptoclaim.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "cryptoClaimTenants")
public class CryptoClaimTenant {

	private String id;
	private String name;
	@JsonIgnore
	private String publicKey;
	@JsonIgnore
	private String privateKey;

	public CryptoClaimTenant(String name, String publicKey, String privateKey) {
		super();
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	@Id
	@org.springframework.data.mongodb.core.mapping.Field("_id")
	public String getStorageKey() {
		return name + "@" + id;
	}
	
}
