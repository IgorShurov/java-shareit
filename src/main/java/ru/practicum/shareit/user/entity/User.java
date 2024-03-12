package ru.practicum.shareit.user.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;

/**
 * Класс описывающий модель User
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Email
    @Column(name = "email", unique = true)
    private String email;
}
