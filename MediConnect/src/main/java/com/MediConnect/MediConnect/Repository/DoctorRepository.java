package com.MediConnect.MediConnect.Repository;

import com.MediConnect.MediConnect.Model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Doctor findByEmail(String email);
}