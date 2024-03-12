package com.rubinho.vkproxy.services;

import com.rubinho.vkproxy.dto.CredentialsDto;
import com.rubinho.vkproxy.dto.SignUpDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.exceptions.AppException;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.model.Activations;
import com.rubinho.vkproxy.model.Restores;
import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.repositories.ActivationsRepository;
import com.rubinho.vkproxy.repositories.RestoresRepository;
import com.rubinho.vkproxy.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final ActivationsRepository activationsRepository;
    private final RestoresRepository restoresRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${SECURED_WORD}")
    private String SECURED_WORD;


    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);

    }

    public UserDto register(SignUpDto userDto, Role role) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AppException("User with such email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
        user.setRole(role);

        userRepository.save(user);

        return userMapper.toUserDto(user);

    }

    public void checkAdminPermissionsForRegistration(String securedWord) {
        if (!securedWord.equals(SECURED_WORD)) {
            throw new AppException("Not valid secured word", HttpStatus.FORBIDDEN);
        }
    }

    public UserDto changeRoleForUser(Long id, Role role) {
        userRepository.changeRole(id, role);
        return userMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND)));

    }

    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }


    public UserDto restorePassword(User user, String code, String password) {
        Restores restores = restoresRepository.findByUser(user)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (!restores.getCode().equals(code)) {
            throw new AppException("Invalid code", HttpStatus.UNAUTHORIZED);
        }

        String hashPassword = passwordEncoder.encode(CharBuffer.wrap(password));
        userRepository.changePassword(user.getId(), hashPassword);
        user.setPassword(hashPassword);

        return userMapper.toUserDto(user);
    }

    public UserDto activateUser(User user, String code) {
        Activations activations = activationsRepository.findByUser(user)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (!activations.getCode().equals(code)) {
            throw new AppException("Invalid code", HttpStatus.UNAUTHORIZED);
        }

        changeRoleForUser(user.getId(), Role.ROLE_VERIFIED_USER);
        user.setRole(Role.ROLE_VERIFIED_USER);

        return userMapper.toUserDto(user);
    }

    private String getCode() {
        return UUID.randomUUID().toString();
    }

    public String setActivationCodeForUser(User user) {
        String code = getCode();

        Activations activations = Activations
                .builder()
                .user(user)
                .code(code)
                .build();

        activationsRepository.save(activations);

        return code;
    }

    public String setRestorePasswordCodeForUser(User user) {
        String code = getCode();

        Restores restores = Restores
                .builder()
                .user(user)
                .code(code)
                .build();

        restoresRepository.save(restores);

        return code;
    }

    private static List<GrantedAuthority> getAuthorities(List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.toString()));
        }
        return authorities;
    }


    public UserDetails getUserDetails(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        List<Role> roles = new ArrayList<>();
        roles.add(user.getRole());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthorities(roles));


    }

}