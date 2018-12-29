
package cf.cryptoclaim.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.ConsumedJWT;

@Repository
public interface ConsumedJWTRepository extends MongoRepository<ConsumedJWT, String> {

}
