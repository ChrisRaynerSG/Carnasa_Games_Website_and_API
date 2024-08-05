package com.sparta.cr.carnasagameswebsiteandapi.controllers.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/high_scores")
public class HighScoreController {

    private HighScoreServiceImpl highScoreService;

    @Autowired
    public HighScoreController(HighScoreServiceImpl highScoreService){
        this.highScoreService = highScoreService;
    }
}
