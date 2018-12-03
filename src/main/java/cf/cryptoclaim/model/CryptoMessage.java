package cf.cryptoclaim.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cryptoMessages")
public class CryptoMessage {

	private String sendingTenant;
	private String receivingTenant;
	private byte[] encryptedData;
	private Date sendAt;
	
	public String getSendingTenant() {
		return sendingTenant;
	}
	
	public String getReceivingTenant() {
		return receivingTenant;
	}
	
	public byte[] getEncryptedData() {
		return encryptedData;
	}
	
	public Date getSendAt() {
		return sendAt;
	}
	
	public void setSendingTenant(String sendingTenant) {
		this.sendingTenant = sendingTenant;
	}
	
	public void setReceivingTenant(String receivingTenant) {
		this.receivingTenant = receivingTenant;
	}
	
	public void setEncryptedData(byte[] encryptedData) {
		this.encryptedData = encryptedData;
	}
	
	public void setSendAt(Date sendAt) {
		this.sendAt = sendAt;
	}
}
