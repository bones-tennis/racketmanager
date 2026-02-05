package com.example.racketmanager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.racketmanager.model.User;
import com.example.racketmanager.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    @Value("${line.liff-id:}")
    private String liffId;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("liffId", "2009047193-TnS19M6D");
        return "signup";
    }


    @PostMapping("/signup")
    public String register(@ModelAttribute("user") User user,
                           @RequestParam String lineUserId,
                           Model model) {
        try {
            // ✅ LINE連携必須（空なら弾く）
            if (lineUserId == null || lineUserId.isBlank()) {
                model.addAttribute("error", "LINE連携が必要です。LINE連携してから登録してください。");
                model.addAttribute("liffId", liffId);
                return "signup";
            }

            userService.registerUser(
                user.getUsername(),
                user.getPassword(),
                "CUSTOMER",
                lineUserId
            );

            model.addAttribute("success", "アカウントを登録しました！");
            return "login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "登録に失敗しました: " + e.getMessage());
            model.addAttribute("liffId", liffId);
            return "signup";
        }
    }
}
