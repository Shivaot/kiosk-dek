package com.hitachi.kioskdesk.repository;

import com.hitachi.kioskdesk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Shiva Created on 04/01/22
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}
