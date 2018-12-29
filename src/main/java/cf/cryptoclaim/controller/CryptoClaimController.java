package cf.cryptoclaim.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cf.cryptoclaim.auth.JWTService;
import cf.cryptoclaim.crypto.ClaimEncryptionService;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.model.CryptoMessage;

@RestController
@RequestMapping("/")
public class CryptoClaimController {

	@Autowired
	private ClaimEncryptionService claimEncryptionService;
	
	@Autowired
	private JWTService jwtService;
	
	@PostMapping("/register")
	public ResponseEntity<?> registerTenant(@RequestParam(value = "client_id", required = true) String clientId, 
			@RequestParam(value = "password", required = true) String password) throws CryptoClaimException {
		return ResponseEntity.ok().body(claimEncryptionService.registerTenant(clientId, password));
	}
	
	@PostMapping("/send")
	public ResponseEntity<?> sendMessage(HttpServletRequest httpServletRequest, @RequestParam(value = "client_id", required = true) String clientId,
		      @RequestParam(value = "client_assertion", required = true) String clientAssertion, @RequestBody(required = true) CryptoMessage cryptoMessage) throws CryptoClaimException {
		authenticate(httpServletRequest, clientId, clientAssertion);
		
		claimEncryptionService.encryptMessageAndSave(cryptoMessage.getReceivingClient(), clientId, cryptoMessage);
		
		return ResponseEntity.ok().body("Message send");
	}
	
	@GetMapping("/read")
	public ResponseEntity<CryptoMessage> readMessage(HttpServletRequest httpServletRequest, @RequestParam(value = "client_id", required = true) String clientId,
		      @RequestParam(value = "client_assertion", required = true) String clientAssertion, @RequestParam(value = "message_id", required = true) String messageId) throws CryptoClaimException {
		authenticate(httpServletRequest, clientId, clientAssertion);
		
		return ResponseEntity.ok(claimEncryptionService.decryptMessage(clientId, messageId));
	}
	
	private void authenticate(HttpServletRequest httpServletRequest, String clientId, String clientAssertion) throws CryptoClaimException {
		jwtService.verifyJWT(httpServletRequest, clientAssertion, clientId, claimEncryptionService.getPublicKey(clientId));
	}
	
	
}
