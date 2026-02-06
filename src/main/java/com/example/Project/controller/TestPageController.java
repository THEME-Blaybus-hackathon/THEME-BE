package com.example.Project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPageController {

    @GetMapping("/test/ai-chat")
    public String aiChatTest() {
        return "ai-chat-test";
    }
}
