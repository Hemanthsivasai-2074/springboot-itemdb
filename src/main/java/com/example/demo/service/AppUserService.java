package com.example.demo.service;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser saveUser(AppUser user) {
        // Ensure role starts with ROLE_
        if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole());
        }
        return appUserRepository.save(user);
    }

    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        AppUser user = appUserRepository.findById(id).orElse(null);
        if (user != null && Boolean.TRUE.equals(user.getIsImmutable())) {
            throw new RuntimeException("❌ Cannot delete superadmin");
        }
        appUserRepository.deleteById(id);
    }
}