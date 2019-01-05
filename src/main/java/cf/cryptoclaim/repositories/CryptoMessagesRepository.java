package cf.cryptoclaim.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.CryptoMessage;
import cf.cryptoclaim.model.MessageInformation;

@Repository
public interface CryptoMessagesRepository extends MongoRepository<CryptoMessage, String> {
	public Page<MessageInformation> findByReceivingClient(String receivingClient, Pageable pageable);
}
