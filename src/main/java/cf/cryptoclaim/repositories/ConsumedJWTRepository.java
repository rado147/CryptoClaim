/**
 * Copyright (c) 2018 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 * This software is the confidential and proprietary information
 * of SAP SE, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Apr 24, 2018 by I030336
 */

package cf.cryptoclaim.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cf.cryptoclaim.model.ConsumedJWT;

@Repository
public interface ConsumedJWTRepository extends MongoRepository<ConsumedJWT, String> {

}
