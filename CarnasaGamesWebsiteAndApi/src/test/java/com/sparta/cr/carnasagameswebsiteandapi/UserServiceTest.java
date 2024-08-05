package com.sparta.cr.carnasagameswebsiteandapi;

import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.UserRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.interfaces.UserServiceable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceable userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordEncoder passwordEncoderTest;

    private UserModel user1;
    private UserModel user2;
    private List<UserModel> users;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {

        user1 = new UserModel();
        user1.setId(1234);
        user1.setUsername("admin");
        user1.setPassword(passwordEncoderTest.encode("Password@1"));
        user1.setEmail("admin@admin.com");

        user2 = new UserModel();
        user2.setId(5678);
        user2.setUsername("user");
        user2.setPassword(passwordEncoderTest.encode("Password@1"));
        user2.setEmail("user@user.com");

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
    }

    @Test
    void createUserReturnsNullWhenUserIdAlreadyExists(){
        UserModel user = new UserModel();
        user.setId(1234);
        user.setUsername("newUser");
        user.setPassword("Password@1");
        user.setEmail("newUser@email.com");
        when(userRepository.findAll()).thenReturn(users);
        UserModel actual = userServiceImpl.createUser(user);
        Assertions.assertNull(actual);
    }
    @Test
    void createUserReturnsNullWhenUserNameAlreadyExists(){
        UserModel user = new UserModel();
        user.setId(123456);
        user.setUsername("admin");
        user.setPassword("Password@1");
        user.setEmail("newUser@email.com");
        when(userRepository.findAll()).thenReturn(users);
        UserModel actual = userServiceImpl.createUser(user);
        Assertions.assertNull(actual);
    }
    @Test
    void createNewUserReturnsNullWhenPasswordIsInvalid(){
        UserModel user = new UserModel();
        user.setId(123456);
        user.setUsername("admin2");
        user.setPassword("password");
        user.setEmail("newUser@email.com");
        Assertions.assertNull(userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserReturnsNullIfEmailIsInvalid(){
        UserModel user = new UserModel();
        user.setId(123456);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("invalid");
        Assertions.assertNull(userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserReturnsNullIfEmailAlreadyExists(){
        UserModel user = new UserModel();
        user.setId(123456);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        Assertions.assertNull(userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserReturnsUserIfSuccessfullyCreated(){
        UserModel user = new UserModel();
        user.setId(123456);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin2@admin.com");

        when(userRepository.findAll()).thenReturn(users);
        when(passwordEncoder.encode(any(String.class))).thenReturn(passwordEncoderTest.encode(user.getPassword()));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserModel createdUser = userServiceImpl.createUser(user);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(user.getId(), createdUser.getId());
        Assertions.assertEquals(user.getUsername(), createdUser.getUsername());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserReturnsNullWhenUserIdDoesNotExist(){
        UserModel user = new UserModel();
        user.setId(123456);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin2@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        UserModel updatedUser = userServiceImpl.updateUser(user);
        Assertions.assertNull(updatedUser);
    }

    @Test
    void updateUserReturnsNullIfNewPasswordIsInvalid(){
        UserModel user = new UserModel();
        user.setId(1234);
        user.setUsername("admin");
        user.setPassword("password");
        user.setEmail("admin@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        UserModel updatedUser = userServiceImpl.updateUser(user);
        Assertions.assertNull(updatedUser);
    }
    @Test
    void updateUserReturnsNullIfNewEmailIsInvalid(){
        UserModel user = new UserModel();
        user.setId(1234);
        user.setUsername("admin");
        user.setPassword(user1.getPassword());
        user.setEmail("admin@admin");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        UserModel updatedUser = userServiceImpl.updateUser(user);
        Assertions.assertNull(updatedUser);
    }
    @Test
    void updateUserReturnsCorrectUserIfSuccessfullyUpdatedPassword(){
        UserModel user = new UserModel();
        user.setId(1234);
        user.setUsername("admin");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(any(String.class))).thenReturn(passwordEncoderTest.encode(user.getPassword()));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserModel updatedUser = userServiceImpl.updateUser(user);
        Assertions.assertTrue(passwordEncoderTest.matches("Password@1", updatedUser.getPassword()));
    }
}
