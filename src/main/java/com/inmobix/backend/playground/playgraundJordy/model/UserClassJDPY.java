package com.inmobix.backend.playgraundJordy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import jakarta.persistence.*;
//import lombok.*;
@Entity
@Table(name="userClass",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserClassJDPY {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    private String phone;
}
