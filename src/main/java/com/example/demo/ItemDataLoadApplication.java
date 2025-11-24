package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.AppUser; // ✅ Import your entity
import com.example.demo.repository.AppUserRepository;

@SpringBootApplication
public class ItemDataLoadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemDataLoadApplication.class, args);
	}

	@Bean
	CommandLineRunner initSuperAdmin(AppUserRepository repo) {
	    return args -> {
	        if (repo.findByUsername("superadmin").isEmpty()) {
	            AppUser superadmin = new AppUser("superadmin", "superpass", "ROLE_ADMIN", true); // ✅ Use full constructor
	            repo.save(superadmin);
	            System.out.println("✅ Superadmin seeded");
	        }
	    };
	}
}