package cf.cryptoclaim.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageInformation {

	private String id;

	private String sendingClient;
	
	private Date sendAt;
	
	public void setId(String id) {
		this.id = id;
	}

	public void setSendingClient(String sendingClient) {
		this.sendingClient = sendingClient;
	}

	public void setSendAt(Date sendAt) {
		this.sendAt = sendAt;
	}

	public Date getSendAt() {
		return sendAt;
	}

	public String getSendingClient() {
		return sendingClient;
	}

	public String getMessageId() {
		return id;
	}
}
