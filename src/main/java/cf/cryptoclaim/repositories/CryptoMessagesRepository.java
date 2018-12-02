package cf.cryptoclaim.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.CryptoMessage;

@Repository
public interface CryptoMessagesRepository extends MongoRepository<CryptoMessage, String> {

}
