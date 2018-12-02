package cf.cryptoclaim.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CryptoClaimController {

	@Autowired
	private MongoTemplate mongoTemplate;

	@GetMapping
	public int test(HttpServletRequest request) {
		return mongoTemplate.collectionExists("asd") ? 1 : 0;
	}
	
}
