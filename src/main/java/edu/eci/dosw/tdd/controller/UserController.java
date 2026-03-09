package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @PostMapping
    public User createUser(@RequestBody User user) {
        return user;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return new User(id, "Sample User", "user@example.com");
    }
}
