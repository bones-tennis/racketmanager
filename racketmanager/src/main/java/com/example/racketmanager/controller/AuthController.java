package com.example.racketmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.racketmanager.model.User;
import com.example.racketmanager.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ======================
    // サインアップ画面
    // ======================
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // ======================
    // 登録処理
    // ======================
    @PostMapping("/signup")  // ← ここを「/register」から「/signup」に統一
    public String register(@ModelAttribute("user") User user, Model model) {
        try {
            // Service層でユーザー登録
            userService.registerUser(
                user.getUsername(),
                user.getPassword(),
                "CUSTOMER"
            );

            model.addAttribute("success", "アカウントを登録しました！");
            return "login";  // 登録後にログイン画面へ
        } catch (Exception e) {
        	e.printStackTrace();
            model.addAttribute("error", "登録に失敗しました: " + e.getMessage());
            return "signup";
        }
    }
}
