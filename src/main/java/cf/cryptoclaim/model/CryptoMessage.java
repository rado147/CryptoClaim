package cf.cryptoclaim.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "cryptoMessages")
public class CryptoMessage {

	private String sendingClient;
	
	@JsonIgnore
	private String receivingClient;
	
	private byte[] rawData;
	
	@JsonIgnore
	private byte[] encryptedData;
	
	private Date sendAt;

	@Id
	@org.springframework.data.mongodb.core.mapping.Field("_id")
	private String id;
	
	private CryptoMessage() {
		this.setId(UUID.randomUUID().toString());
	}
	
	public String getSendingClient() {
		return sendingClient;
	}
	
	public String getReceivingClient() {
		return receivingClient;
	}
	
	public byte[] getEncryptedData() {
		return encryptedData;
	}
	
	public Date getSendAt() {
		return sendAt;
	}
	
	public void setSendingClient(String sendingClient) {
		this.sendingClient = sendingClient;
	}
	
	public void setReceivingClient(String receivingClient) {
		this.receivingClient = receivingClient;
	}
	
	public void setEncryptedData(byte[] encryptedData) {
		this.encryptedData = encryptedData;
	}
	
	public void setSendAt(Date sendAt) {
		this.sendAt = sendAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}
}
