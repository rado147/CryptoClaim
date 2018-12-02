package cf.cryptoclaim.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cf.cryptoclaim.config.MongoConfiguration;

@RestController
@RequestMapping("/")
public class CryptoClaimController {

	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(MongoConfiguration.class);
    private static MongoOperations mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");
	
	@GetMapping
	public int test(HttpServletRequest request) {
		return mongoOperation.collectionExists("asd") ? 1 : 0;
	}
	
}
