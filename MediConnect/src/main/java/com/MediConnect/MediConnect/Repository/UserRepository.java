package com.MediConnect.MediConnect.Repository;


import com.MediConnect.MediConnect.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
