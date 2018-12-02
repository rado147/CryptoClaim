package cf.cryptoclaim.config;

import static cf.cryptoclaim.constants.CryptoClaimConstants.C9E_DATABASE_NAME;
import static cf.cryptoclaim.constants.CryptoClaimConstants.C9M_MONGODB_USERNAME_KEY;
import static cf.cryptoclaim.constants.CryptoClaimConstants.C9M_MONGODB_PASSWORD_KEY;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import cf.cryptoclaim.constants.CryptoClaimConstants;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
	
	@Value("${"+ C9M_MONGODB_USERNAME_KEY + ":admin}")
	private String mongodbUsername;
	
	@Value("${"+ C9M_MONGODB_PASSWORD_KEY + ":admin}")
	private String mongodbPassword;
	
	@Override
	public String getDatabaseName() {
		return C9E_DATABASE_NAME;
	}

	@Override
	public MongoClient mongoClient() {
		MongoClientURI uri = new MongoClientURI(
			    "mongodb+srv://" + mongodbUsername + ":" + mongodbPassword + "@cluster0-jorr7.mongodb.net/test?retryWrites=true");

		return new MongoClient(uri);
	}

}
