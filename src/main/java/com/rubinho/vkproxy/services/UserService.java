package com.rubinho.vkproxy.services;

import com.rubinho.vkproxy.dto.CredentialsDto;
import com.rubinho.vkproxy.dto.SignUpDto;
import com.rubinho.vkproxy.dto.UserDto;
import com.rubinho.vkproxy.exceptions.AppException;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.model.Role;
import com.rubinho.vkproxy.model.User;
import com.rubinho.vkproxy.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;



    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);

    }

    public UserDto register(SignUpDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new AppException("User with such email already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
        user.setRole(Role.ROLE_UNVERIFIED_USER);

        userRepository.save(user);

        return userMapper.toUserDto(user);

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