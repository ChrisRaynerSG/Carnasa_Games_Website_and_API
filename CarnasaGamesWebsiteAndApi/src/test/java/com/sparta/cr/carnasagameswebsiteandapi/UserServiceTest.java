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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserServiceable userService;

    private List<UserModel> users;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        UserModel user1 = new UserModel();
        user1.setId(1234);
        user1.setUsername("admin");
        user1.setPassword("admin");
        user1.setEmail("admin@admin.com");
        UserModel user2 = new UserModel();
        user2.setId(5678);
        user2.setUsername("user");
        user2.setPassword("password");
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
        UserModel createdUser = userServiceImpl.createUser(user);
        Assertions.assertNotNull(createdUser);
    }
}
