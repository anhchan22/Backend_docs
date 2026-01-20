package com.qlsv.dkmh.repository;

import com.qlsv.dkmh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    boolean existsByMaSV(String maSV);
    Optional<User> findByMaSV(String maSV);
    Optional<User> findByTen(String ten);
}
