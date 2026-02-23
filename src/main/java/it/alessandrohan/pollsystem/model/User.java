package it.alessandrohan.pollsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
            @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        })
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, length = 60)
    @NotBlank
    private String username;

    @Column(name = "email", nullable = false, length = 120)
    @NotBlank
    @Email
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    @NotBlank
    private String passwordHash;
}
