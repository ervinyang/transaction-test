package me.ervin.yang.repositories;

import me.ervin.yang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhiyingyang
 * @version 2018-05-14 10:29
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}