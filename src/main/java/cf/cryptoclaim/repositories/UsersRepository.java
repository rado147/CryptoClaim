package cf.cryptoclaim.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.CryptoClaimUser;

@Repository
public interface UsersRepository extends MongoRepository<CryptoClaimUser, String> {
	public List<CryptoClaimUser> findByName(String name);
	
	public boolean existsByName(String name);
}
