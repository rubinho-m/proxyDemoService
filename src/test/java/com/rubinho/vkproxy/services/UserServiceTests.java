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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivationsRepository activationsRepository;
    @Mock
    private RestoresRepository restoresRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private User getUser() {
        return User
                .builder()
                .id(1L)
                .email("test@gmail.ru")
                .password("testHashPassword")
                .role(Role.ROLE_UNVERIFIED_USER)
                .build();
    }

    private UserDto getUserDto() {
        return UserDto
                .builder()
                .email("test@gmail.ru")
                .role(Role.ROLE_UNVERIFIED_USER)
                .build();
    }

    private SignUpDto getSignUpDto() {
        return SignUpDto
                .builder()
                .email("test@gmail.ru")
                .password("testHashPassword".toCharArray())
                .build();
    }

    private CredentialsDto getCredentialsDto() {
        return CredentialsDto
                .builder()
                .email("test@gmail.ru")
                .password("testHashPassword".toCharArray())
                .build();
    }

    private Activations getActivations(String code) {
        return Activations
                .builder()
                .code(code)
                .user(getUser())
                .build();
    }

    private Restores getRestores(String code) {
        return Restores
                .builder()
                .code(code)
                .user(getUser())
                .build();
    }


    @Test
    public void register_withValidDto_savesUser() {
        User user = getUser();
        UserDto userDto = getUserDto();

        SignUpDto signUpDto = getSignUpDto();

        Mockito.when(userMapper.signUpToUser(signUpDto)).thenReturn(user);
        Mockito.when(userRepository.existsByEmail(signUpDto.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto savedUser = userService.register(signUpDto, Role.ROLE_UNVERIFIED_USER);

        Assertions.assertThat(savedUser).isNotNull();

    }

    @Test
    public void register_withInvalidDto_throwsException() {
        SignUpDto signUpDto = getSignUpDto();

        Mockito.when(userRepository.existsByEmail(signUpDto.getEmail())).thenReturn(true);

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.register(signUpDto, Role.ROLE_UNVERIFIED_USER));


    }

    @Test
    public void login_withAlreadyExistingUser_throwsException() {
        CredentialsDto credentialsDto = getCredentialsDto();

        Mockito.when(userRepository.findByEmail(credentialsDto.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.login(credentialsDto));

    }

    @Test
    public void login_withInvalidPassword_throwsException() {
        CredentialsDto credentialsDto = getCredentialsDto();
        User user = getUser();

        Mockito.when(userRepository.findByEmail(credentialsDto.getEmail())).thenReturn(Optional.ofNullable(user));
        Mockito.when(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())).thenReturn(false);

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.login(credentialsDto));

    }

    @Test
    public void login_withValidPassword_returnsDto() {
        CredentialsDto credentialsDto = getCredentialsDto();
        User user = getUser();


        Mockito.when(userRepository.findByEmail(credentialsDto.getEmail())).thenReturn(Optional.ofNullable(user));
        Mockito.when(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())).thenReturn(true);
        Mockito.when(userMapper.toUserDto(user)).thenReturn(getUserDto());

        UserDto userDto = userService.login(credentialsDto);
        Assertions.assertThat(userDto).isNotNull();

    }

    @Test
    public void activate_unknownUser_throwsException() {
        User user = getUser();

        String code = "test";

        Mockito.when(activationsRepository.findByUser(user)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.activateUser(user, code));


    }

    @Test
    public void activate_withWrongCode_throwsException() {
        User user = getUser();
        String code = "test";

        Activations activations = getActivations("test2");


        Mockito.when(activationsRepository.findByUser(user)).thenReturn(Optional.ofNullable(activations));

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.activateUser(user, code));

    }

    @Test
    public void activate_Valid_returnsUserDto() {
        User user = getUser();

        String code = "test";
        Activations activations = getActivations(code);

        Mockito.when(activationsRepository.findByUser(user)).thenReturn(Optional.ofNullable(activations));

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Mockito.when(userMapper.toUserDto(user)).thenReturn(getUserDto());

        UserDto userDto = userService.activateUser(user, code);
        Assertions.assertThat(userDto).isNotNull();


    }

    @Test
    public void restore_unknownUser_throwsException() {
        User user = getUser();

        String code = "test";
        String password = "testPassword";

        Mockito.when(restoresRepository.findByUser(user)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.restorePassword(user, code, password));


    }

    @Test
    public void restore_withWrongCode_throwsException() {
        User user = getUser();
        String code = "test";
        String password = "testPassword";

        Restores restores = getRestores("test2");


        Mockito.when(restoresRepository.findByUser(user)).thenReturn(Optional.ofNullable(restores));

        Assertions.assertThatExceptionOfType(AppException.class)
                .isThrownBy(() -> userService.restorePassword(user, code, password));

    }

    @Test
    public void restore_Valid_returnsUserDto() {
        User user = getUser();

        String code = "test";

        String password = "testPassword";

        Restores restores = getRestores(code);

        Mockito.when(restoresRepository.findByUser(user)).thenReturn(Optional.ofNullable(restores));
        Mockito.when(passwordEncoder.encode(CharBuffer.wrap(password))).thenReturn("hashPassword");
        Mockito.when(userMapper.toUserDto(user)).thenReturn(getUserDto());

        UserDto userDto = userService.restorePassword(user, code, password);
        Assertions.assertThat(userDto).isNotNull();


    }


}
