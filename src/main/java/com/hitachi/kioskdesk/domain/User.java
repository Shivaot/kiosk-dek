package com.hitachi.kioskdesk.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Shiva Created on 04/01/22
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean enabled;
    private String role;

}
