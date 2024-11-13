package com.up.spa.auth.infrastructure.repository;


import com.mongodb.client.MongoCollection;
import com.up.spa.application.services.IMongoService;
import com.up.spa.auth.domain.port.UserRepository;
import com.up.spa.auth.domain.model.User;
import com.up.spa.application.enums.UserType;
import java.util.Optional;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MongoUserRepositoryImpl implements UserRepository {

  private static final String AUTHENTICATION = "Authentication";
  private static final String EMAIL = "email";
  private static final String PASSWORD = "password";
  private static final String USER_TYPE = "userType";

  private final IMongoService mongoService;

  @Autowired
  public MongoUserRepositoryImpl(IMongoService mongoService) {
    this.mongoService = mongoService;
  }




  @Override
  public Optional<User> findByEmail(String email) {
    MongoCollection<Document> userCollection = mongoService.getCollection(AUTHENTICATION);
    Document userDoc = userCollection.find(new Document(EMAIL, email)).first();
    return Optional.ofNullable(userDoc).map(this::mapToUser);
  }

  private User mapToUser(Document doc) {
    return User.builder()
        .email(doc.getString(EMAIL))
        .password(doc.getString(PASSWORD))
        .userType(UserType.fromValue(doc.getString(USER_TYPE)))
        .build();
  }


}
