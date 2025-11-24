package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // ✅ Import Principal
import java.util.List;

@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository; // ✅ Inject repository properly

    @PostMapping
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user, Principal principal) {
        // ✅ Allow anonymous user to create a non-admin account
        if (principal == null) {
            if (user.getRole().equals("ROLE_ADMIN")) {
                throw new RuntimeException("❌ Only superadmin can create admin accounts");
            }
        } else {
            AppUser creator = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Creator not found"));

            if (user.getRole().equals("ROLE_ADMIN") && !creator.getUsername().equals("superadmin")) {
                throw new RuntimeException("❌ Only superadmin can create other admins");
            }
        }

        if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole());
        }

        return ResponseEntity.ok(appUserService.saveUser(user));
    }

    @GetMapping
    public List<AppUser> getAllUsers() {
        System.out.println("🔐 Authenticated user accessing /users");
        return appUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public AppUser getUserById(@PathVariable Long id) {
        return appUserService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}