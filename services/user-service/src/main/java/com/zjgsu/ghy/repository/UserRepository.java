package com.zjgsu.ghy.repository;
import com.zjgsu.ghy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {}
