package cf.cryptoclaim.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.CryptoClaimTenant;

@Repository
public interface TenantsRepository extends MongoRepository<CryptoClaimTenant, String> {
	public List<CryptoClaimTenant> findByName(String name);
}
