package cf.cryptoclaim.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.CryptoMessage;
import cf.cryptoclaim.model.MessageId;

@Repository
public interface CryptoMessagesRepository extends MongoRepository<CryptoMessage, String> {
	public List<MessageId> findByReceivingClient(String receivingClient);
}
