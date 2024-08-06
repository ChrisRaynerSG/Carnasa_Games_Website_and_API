package com.sparta.cr.carnasagameswebsiteandapi;

import com.sparta.cr.carnasagameswebsiteandapi.models.HighScoreModel;
import com.sparta.cr.carnasagameswebsiteandapi.repositories.HighScoreRepository;
import com.sparta.cr.carnasagameswebsiteandapi.services.implementations.HighScoreServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class HighScoreTest {

    @Mock
    HighScoreRepository highScoreRepository;

    @InjectMocks
    HighScoreServiceImpl highScoreService;

    private HighScoreModel highScore1;
    private HighScoreModel highScore2;
    private HighScoreModel highScore3;
    private List<HighScoreModel> highScoreModels;


    @BeforeEach
    void setUp() {
        highScore1 = new HighScoreModel();
        highScore2 = new HighScoreModel();
        highScore3 = new HighScoreModel();
        highScoreModels = new ArrayList<HighScoreModel>();

        highScoreModels.add(highScore1);
        highScoreModels.add(highScore2);
        highScoreModels.add(highScore3);
    }

    @Test
    void testGetAllHighScores() {
        int expected = 3;
        when(highScoreRepository.findAll()).thenReturn(highScoreModels);
        int actual = highScoreService.getAllHighScores().size();
        Assertions.assertEquals(expected, actual);
    }

}
