package cf.cryptoclaim.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.CryptoClaimClient;

@Repository
public interface UsersRepository extends MongoRepository<CryptoClaimClient, String> {
	public List<CryptoClaimClient> findByName(String name);
	
	public boolean existsByName(String name);
}
