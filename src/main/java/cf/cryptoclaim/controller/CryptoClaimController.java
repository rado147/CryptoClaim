package cf.cryptoclaim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cf.cryptoclaim.crypto.ClaimEncryptionService;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.model.CryptoClaimTenant;

@RestController
@RequestMapping("/")
public class CryptoClaimController {

	@Autowired
	private ClaimEncryptionService claimEncryptionService;
	
	@PostMapping("register")
	public ResponseEntity<?> registerTenant(@RequestParam(value = "username", required = true) String username, 
			@RequestParam(value = "password", required = true) String password) throws CryptoClaimException {
		CryptoClaimTenant tenant = claimEncryptionService.registerTenant(username, password);
		
		return ResponseEntity.ok().body(tenant);
	}
	
	
}
