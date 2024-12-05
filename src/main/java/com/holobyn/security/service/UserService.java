package com.holobyn.security.service;

import com.holobyn.security.domain.User;
import com.holobyn.security.dto.ReqistrationRequestDto;
import com.holobyn.security.mapper.UserMapper;
import com.holobyn.security.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                                  .orElseThrow(() -> new EntityNotFoundException("User with email: %s not found".formatted(email)));
    }

    public User loadUserById(Long id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                             .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found".formatted(id)));
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(ReqistrationRequestDto reqistrationRequestDto) {
        User user = userMapper.toEntity(reqistrationRequestDto);
        user.setEnabled(true);  // todo: change to false
        return userRepository.save(user);
    }

    public User activate(Long id) {
        User user = loadUserById(id);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User changePassword(Long userId, String newPassword) {
        User user = loadUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User changeToptToken(Long userId, String token) {
        User user = loadUserById(userId);
        user.setTotpSecret(token);
        return userRepository.save(user);
    }

}
