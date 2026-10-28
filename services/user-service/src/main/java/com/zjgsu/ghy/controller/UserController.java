package com.zjgsu.ghy.controller;
import com.zjgsu.ghy.model.User;
import com.zjgsu.ghy.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/users")
public class UserController {
    private final UserRepository repository;
    public UserController(UserRepository repository) { this.repository = repository; }
    @PostMapping public User create(@RequestBody User user) { return repository.save(user); }
    @GetMapping("/{id}") public User get(@PathVariable Long id) { return repository.findById(id).orElseThrow(); }
}
