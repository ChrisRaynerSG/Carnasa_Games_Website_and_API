package com.sparta.cr.carnasagameswebsiteandapi.services;

import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.InvalidUserException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.globalexceptions.ModelNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.exceptions.userexceptions.UserNotFoundException;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModelId;
import com.sparta.cr.carnasagameswebsiteandapi.models.GameModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.UserModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.FollowerRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.FollowerServiceImpl;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FollowerServiceTest {

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FollowerServiceImpl followerService;

    private UserModel user1;
    private UserModel user2;
    private UserModel user3;
    private UserModel user4;
    private FollowerModel follower1;
    private FollowerModel follower2;
    private FollowerModelId followerModelId1;

    @BeforeEach
    void setUp() {
        List<FollowerModel> followers = new ArrayList<>();
        user1 = new UserModel();
        user2 = new UserModel();
        user3 = new UserModel();
        user4 = new UserModel();

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
        user4.setId(4L);

        follower1 = new FollowerModel();
        follower2 = new FollowerModel();

        follower1.setFollower(user2);
        follower1.setUser(user1);
        follower2.setFollower(user3);
        follower2.setUser(user1);

        followerModelId1 = new FollowerModelId();
        followerModelId1.setFollower_id(1L);
        followerModelId1.setUser_id(2L);

        followers.add(follower1);
        followers.add(follower2);
        when(followerRepository.findAll()).thenReturn(followers);
    }
    @Test
    void testGetAllFollowersReturnsAllFollowers(){
        int expected = 2;
        int actual = followerService.getAllFollowers().size();
        assertEquals(expected, actual);
    }
    @Test
    void testGetFollowerModelByIdReturnsFollowerModel(){
        when(followerRepository.findById(any())).thenReturn(Optional.of(follower1));
        assertNotNull(followerService.getFollowerModelById(follower1.getId()));
    }
    @Test
    void testGetAllFollowersByUserIdReturnsAllFollowers(){
        int expected = 2;
        int actual = followerService.getAllFollowersByUserId(user1.getId()).size();
        assertEquals(expected, actual);
    }
    @Test
    void testGetAllFollowingByUserIdReturnsAllFollowing(){
        int expected = 1;
        int actual = followerService.getAllFollowingByUserId(user2.getId()).size();
        assertEquals(expected, actual);
    }
    @Test
    void testGetNumberOfFollowers(){
        Long expected = 2L;
        Long actual = followerService.getNumberOfFollowersByUserId(user1.getId());
        assertEquals(expected, actual);
    }
    @Test
    void testFollowNewUserThrowsExceptionIfUserNotFound(){
        when(userService.getUser(1L)).thenReturn(Optional.empty());
        when(userService.getUser(2L)).thenReturn(Optional.of(user2));
        FollowerModel followerModel = new FollowerModel();
        followerModel.setId(followerModelId1);
        followerModel.setUser(user1);
        followerModel.setFollower(user2);
        assertThrows(UserNotFoundException.class, () -> followerService.followNewUser(followerModel));
    }
    @Test
    void testFollowNewUserThrowsExceptionIfFollowerNotFound(){
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(userService.getUser(2L)).thenReturn(Optional.empty());
        FollowerModel followerModel = new FollowerModel();
        followerModel.setId(followerModelId1);
        followerModel.setUser(user1);
        followerModel.setFollower(user2);
        assertThrows(UserNotFoundException.class, () -> followerService.followNewUser(followerModel));
    }
    @Test
    void testFollowYourselfThrowsException(){
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        FollowerModel followerModel = new FollowerModel();
        FollowerModelId followerModelId = new FollowerModelId();
        followerModelId.setFollower_id(1L);
        followerModelId.setUser_id(1L);
        followerModel.setId(followerModelId);
        followerModel.setUser(user1);
        followerModel.setFollower(user1);
        assertThrows(InvalidUserException.class, () -> followerService.followNewUser(followerModel));
    }
    @Test
    void testFollowAlreadyFollowedUserThrowsException(){
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(userService.getUser(2L)).thenReturn(Optional.of(user2));
        when(followerRepository.findById(any())).thenReturn(Optional.of(follower1));
        FollowerModel followerModel = new FollowerModel();
        followerModel.setId(followerModelId1);
        followerModel.setUser(user1);
        followerModel.setFollower(user2);
        assertThrows(InvalidUserException.class, () -> followerService.followNewUser(followerModel), "You are already following user: " + followerModel.getUser().getUsername());
    }
    @Test
    void testFollowNewUserSuccess(){
        when(userService.getUser(1L)).thenReturn(Optional.of(user1));
        when(userService.getUser(2L)).thenReturn(Optional.of(user2));
        when(followerRepository.findById(any())).thenReturn(Optional.empty());
        when(followerRepository.save(any(FollowerModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        FollowerModel followerModel = new FollowerModel();
        followerModel.setId(followerModelId1);
        followerModel.setUser(user1);
        followerModel.setFollower(user2);
        FollowerModel createdFollower = followerService.followNewUser(followerModel);
        assertNotNull(createdFollower);
    }
    @Test
    void unfollowUserThrowsExceptionIfFollowerModelNotFound(){
        when(followerRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ModelNotFoundException.class, () -> followerService.unfollowUser(1L, 2L));
        verify(followerRepository, times(0)).delete(any(FollowerModel.class));
    }
    @Test
    void unfollowUserSuccessfulIfFollowerModelFound(){
        when(followerRepository.findById(any())).thenReturn(Optional.of(follower1));
        FollowerModel followerModel = followerService.unfollowUser(1L, 2L);
        assertNotNull(followerModel);
        verify(followerRepository, times(1)).delete(any(FollowerModel.class));
    }

}
