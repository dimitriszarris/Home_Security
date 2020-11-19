package com.interactivehome.main_service.repository.events;

import com.interactivehome.main_service.model.events.entity.PersonSignIn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonSignInRepository extends MongoRepository<PersonSignIn, String> {

}
