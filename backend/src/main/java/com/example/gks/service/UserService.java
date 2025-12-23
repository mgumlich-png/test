package com.example.gks.service;

import com.example.gks.domain.Role;
import com.example.gks.domain.UserAccount;
import com.example.gks.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new User(account.getEmail(), account.getPassword(),
                account.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).collect(Collectors.toSet()));
    }

    public UserAccount ensureAdmin() {
        return userAccountRepository.findByEmail("admin@gk.local").orElseGet(() -> {
            UserAccount account = new UserAccount();
            account.setEmail("admin@gk.local");
            account.setPassword(passwordEncoder.encode("admin"));
            account.setRoles(Set.of(Role.TENANT_ADMIN));
            return userAccountRepository.save(account);
        });
    }
}
