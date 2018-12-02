package cf.cryptoclaim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
	
	@Override
	public String getDatabaseName() {
		return "test";
	}

	@Override
	public MongoClient mongoClient() {
		MongoClientURI uri = new MongoClientURI(
			    "mongodb+srv://rado147:******@cluster0-jorr7.mongodb.net/test?retryWrites=true");

		return new MongoClient(uri);
	}

}
