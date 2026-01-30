package com.qlsv.dkmh.config;

import com.qlsv.dkmh.entity.User;
import com.qlsv.dkmh.enums.Role;
import com.qlsv.dkmh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByMaSV("admin").isEmpty()){
                var roles = new HashSet<String>();
                roles.add(Role.ROLE_ADMIN.name());
                User user = User.builder()
                        .maSV("admin")
                        .matKhau(passwordEncoder.encode("admin"))
                        .ten("admin")
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("Initialized admin user with username 'admin' and password 'admin'");
            }
        };
    }
}
