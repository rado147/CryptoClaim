package cf.cryptoclaim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class CryptoClaimApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoClaimApplication.class, args);
	}
}
