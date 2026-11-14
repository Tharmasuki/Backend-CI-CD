package com.MediConnect.MediConnect.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

@Data
@Document(collection = "user")
public class User {
    @Id
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String image = "";

    private Map<String, String> address = Map.of("line1", "", "line2", "");

    private String gender = "Not Selected";

    private String dob = "Not Selected";

    private String phone = "000000000";
}
