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

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signup(@RequestParam(required = false) String lineUserId, Model model) {
        User user = new User();
        user.setLineUserId(lineUserId); // ✅ hiddenに入れる用
        model.addAttribute("user", user);
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute("user") User user, Model model) {
        try {
            // ✅ lineUserId が無い登録は弾く（LIFF経由の初回登録前提なら）
            if (user.getLineUserId() == null || user.getLineUserId().isBlank()) {
                model.addAttribute("error", "LINE連携が確認できません。公式LINEから開いてください。");
                return "signup";
            }

            userService.registerUser(
                user.getUsername(),
                user.getPassword(),
                "CUSTOMER",
                user.getLineUserId()
            );

            model.addAttribute("success", "アカウントを登録しました！");
            return "login";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "登録に失敗しました: " + e.getMessage());
            return "signup";
        }
    }
}

