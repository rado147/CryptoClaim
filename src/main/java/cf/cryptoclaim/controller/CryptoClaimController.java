package cf.cryptoclaim.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cf.cryptoclaim.constants.CryptoClaimConstants;
import cf.cryptoclaim.crypto.ClaimEncryptionService;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.model.CryptoMessage;
import cf.cryptoclaim.model.MessageInformation;

@RestController
@RequestMapping("/")
public class CryptoClaimController {

	@Autowired
	private ClaimEncryptionService claimEncryptionService;
	
	@GetMapping
	public ResponseEntity<String> defaultEndpoint() {
		return ResponseEntity.ok("CryptoClaim");
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerTenant(@RequestParam(value = "client_id", required = true) String clientId, 
			@RequestParam(value = "password", required = true) String password) throws CryptoClaimException {
		return ResponseEntity.ok().body(claimEncryptionService.registerTenant(clientId, password));
	}
	
	@PostMapping("/send")
	public ResponseEntity<?> sendMessage(HttpServletRequest httpServletRequest, @RequestParam(value = "client_id", required = true) String clientId, 
			@RequestBody(required = true) CryptoMessage cryptoMessage) throws CryptoClaimException {
		claimEncryptionService.encryptMessageAndSave(cryptoMessage.getReceivingClient(), clientId, cryptoMessage);
		
		return ResponseEntity.ok().body("Message send");
	}
	
	@GetMapping("/read")
	public ResponseEntity<CryptoMessage> readMessage(HttpServletRequest httpServletRequest, @RequestParam(value = "client_id", required = true) String clientId, 
			@RequestParam(value = "message_id", required = true) String messageId) throws CryptoClaimException {
		return ResponseEntity.ok(claimEncryptionService.decryptMessage(clientId, messageId));
	}
	
	@GetMapping("/list")
	public ResponseEntity<Page<MessageInformation>> getAllUnreadMessages(HttpServletRequest httpServletRequest, @RequestParam(value = "client_id", required = true) String clientId, 
			@RequestParam(value = "attributes", required = false) Set<String> attributes, 
			@PageableDefault(page = CryptoClaimConstants.DEFAULT_PAGE_NUMBER, size = CryptoClaimConstants.DEFAULT_PAGE_SIZE, sort = "sendAt", direction = Direction.ASC) Pageable pageable) {
		return ResponseEntity.ok(claimEncryptionService.getMessages(clientId, attributes, pageable));
	}
	
}
