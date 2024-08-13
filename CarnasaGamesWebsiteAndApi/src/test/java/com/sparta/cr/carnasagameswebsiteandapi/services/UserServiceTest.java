package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelAlreadyExistsException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.*;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.dtos.UserDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @Autowired
    PasswordEncoder passwordEncoderTest;

    private UserModel user1;
    private UserModel user2;
    private List<UserModel> users;
    private Page<UserModel> page;
    private List<UserModel> usersByPage;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {

        user1 = new UserModel();
        user1.setId(1234L);
        user1.setUsername("admin");
        user1.setPassword(passwordEncoderTest.encode("Password@1"));
        user1.setEmail("admin@admin.com");
        user1.setRoles(Set.of("ROLE_ADMIN"));
        user1.setProfileImage("profileImage");
        user1.setPrivate(false);

        user2 = new UserModel();
        user2.setId(5678L);
        user2.setUsername("user");
        user2.setPassword(passwordEncoderTest.encode("Password@1"));
        user2.setEmail("user@user.com");
        user2.setRoles(Set.of("ROLE_USER"));

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        usersByPage = new ArrayList<>();

    }

    @Test
    void createUserReturnsNullWhenUserIdAlreadyExists(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("newUser");
        user.setPassword("Password@1");
        user.setEmail("newUser@email.com");
        when(userRepository.findById(1234L)).thenReturn(Optional.of(user));
        assertThrows(ModelAlreadyExistsException.class, () -> userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserThrowsExceptionIfUsernameInvalid(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("ad");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        assertThrows(InvalidUsernameException.class, () -> userServiceImpl.createUser(user));
    }
    @Test
    void createUserThrowsUsernameExistsExceptionWhenUserNameAlreadyExists(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin");
        user.setPassword("Password@1");
        user.setEmail("newUser@email.com");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        assertThrows(UsernameAlreadyExistsException.class, () -> userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserThrowsExceptionWhenPasswordIsInvalid(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin2");
        user.setPassword("password");
        user.setEmail("newUser@email.com");
        assertThrows(InvalidPasswordException.class, () -> userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserThrowsExceptionIfEmailIsInvalid(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("invalid");
        assertThrows(InvalidEmailException.class, () -> userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserThrowsExceptionIfEmailAlreadyExists(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(user));
        assertThrows(EmailAlreadyExistsException.class, () -> userServiceImpl.createUser(user));
    }
    @Test
    void createNewUserReturnsUserIfSuccessfullyCreated(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin2@admin.com");

        when(userRepository.findAll()).thenReturn(users);
        when(passwordEncoder.encode(any(String.class))).thenReturn(passwordEncoderTest.encode(user.getPassword()));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserModel createdUser = userServiceImpl.createUser(user);

        assertNotNull(createdUser);
        Assertions.assertEquals(user.getId(), createdUser.getId());
        Assertions.assertEquals(user.getUsername(), createdUser.getUsername());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }
    @Test
    void updateUserThrowsExceptionWhenUserIdDoesNotExist(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin2@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findById(123456L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ModelNotFoundException.class, () -> userServiceImpl.updateUser(user));
    }
    @Test
    void updateUserThrowsExceptionIfTryingToChangeUsername(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("ad");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        assertThrows(CantChangeUsernameException.class, () -> userServiceImpl.updateUser(user));
    }
    @Test
    void updateUserThrowsExceptionIfNewPasswordIsInvalid(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("admin");
        user.setPassword("password");
        user.setEmail("admin@admin.com");
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        assertThrows(InvalidPasswordException.class, () -> userServiceImpl.updateUser(user));
    }
    @Test
    void updateUserThrowsExceptionIfNewEmailIsInvalid(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("admin");
        user.setPassword("Password@1");
        user.setEmail("admin@admin");
        user.setRoles(Set.of("ROLE_ADMIN"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        assertThrows(InvalidEmailException.class, () -> userServiceImpl.updateUser(user));
    }
    @Test
    void updateUserThrowsExceptionIfRoleIsInvalid(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("admin");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        user.setRoles(Set.of("ROLE_PENGUIN"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        assertThrows(InvalidRoleException.class, () -> userServiceImpl.updateUser(user));
    }
    @Test
    void updateUserReturnsCorrectUserIfSuccessfullyUpdatedPassword(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("admin");
        user.setPassword("Password@2");
        user.setEmail("admin@admin.com");
        user.setRoles(Set.of("ROLE_ADMIN"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(any(String.class))).thenReturn(passwordEncoderTest.encode(user.getPassword()));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserModel updatedUser = userServiceImpl.updateUser(user);
        Assertions.assertTrue(passwordEncoderTest.matches("Password@2", updatedUser.getPassword()));
    }
    @Test
    void deleteUserReturnsNullWhenUserIdDoesNotExist(){
        UserModel user = new UserModel();
        user.setId(123456L);
        user.setUsername("admin2");
        user.setPassword("Password@1");
        user.setEmail("admin2@admin.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        UserModel deletedUser = userServiceImpl.deleteUser(user.getId());
        Assertions.assertNull(deletedUser);
    }
    @Test
    void deleteUserReturnsUserIfUserDoesExist(){
        UserModel user = new UserModel();
        user.setId(1234L);
        user.setUsername("admin");
        user.setPassword("Password@1");
        user.setEmail("admin@admin.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user1));
        UserModel deletedUser = userServiceImpl.deleteUser(user.getId());
        Assertions.assertEquals(deletedUser.getId(), user1.getId());
    }
    @Test
    void getUsersByNameReturnsUsersWithName(){
        usersByPage.add(user1);
        page = new PageImpl<>(usersByPage);
        int expected = 1;
        when(userRepository.findByUsernameContainingIgnoreCase(eq("adm"),any(Pageable.class))).thenReturn(page);
        int actual = userServiceImpl.getUsersByName("adm",0,10).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void getUsersByNameReturnsEmptyListIfNoUsersFound(){
        page = new PageImpl<>(usersByPage);
        int expected = 0;
        when(userRepository.findByUsernameContainingIgnoreCase(eq("jerry"),any(Pageable.class))).thenReturn(page);
        int actual = userServiceImpl.getUsersByName("jerry",0,10).toList().size();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    void testConvertUserModelToUserModelDto(){
        UserDto modelToDto = userServiceImpl.convertUserToDto(user1);
        assertEquals(user1.getId(), modelToDto.getId());
        assertEquals(user1.getDescription(), modelToDto.getDescription());
        assertEquals(user1.getProfileImage(), modelToDto.getProfileImage());
        assertEquals(user1.getUsername(), modelToDto.getUsername());
        assertEquals(user1.getRoles(), modelToDto.getRoles());
        assertEquals(user1.getId(), modelToDto.getId());
        assertEquals(user1.getEmail(), modelToDto.getEmail());
        assertFalse(modelToDto.isPrivate());
    }
}
