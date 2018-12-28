package cf.cryptoclaim.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cf.cryptoclaim.auth.JWTService;
import cf.cryptoclaim.crypto.ClaimEncryptionService;
import cf.cryptoclaim.exception.CryptoClaimException;
import cf.cryptoclaim.repositories.UsersRepository;

@RestController
@RequestMapping("/")
public class CryptoClaimController {

	@Autowired
	private ClaimEncryptionService claimEncryptionService;
	
	@Autowired
	private JWTService jwtService;
	
	@PostMapping("register")
	public ResponseEntity<?> registerTenant(@RequestParam(value = "username", required = true) String username, 
			@RequestParam(value = "password", required = true) String password) throws CryptoClaimException {
		return ResponseEntity.ok().body(claimEncryptionService.registerTenant(username, password));
	}
	
	@PostMapping("auth")
	public Object authenticate(HttpServletRequest httpRequest, @RequestParam(value = "client_id", required = true) String clientId,
		      @RequestParam(value = "client_assertion", required = true) String assertion) throws CryptoClaimException {
		return jwtService.verifyJWT(httpRequest, assertion, clientId, claimEncryptionService.getRealPublicKey(clientId));
	}
	
	@GetMapping("/")
	public ResponseEntity<?> getAnotherTenantPublicKey(@RequestParam(value = "username", required = true) String username) {
		String publicKey = claimEncryptionService.getPublicKey(username);
		
		return ResponseEntity.ok().body(publicKey);
	}
	
	
}
