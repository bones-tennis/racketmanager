package com.example.racketmanager.controller;

import com.example.racketmanager.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LiffController {

    private final UserRepository userRepo;

    // あなたのLIFF ID
    private static final String LIFF_ID = "2009047193-TnS19M6D";

    public LiffController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ✅ LIFF起動入口（公開）
    @GetMapping("/liff")
    public String liff(Model model) {
        model.addAttribute("liffId", LIFF_ID);
        return "liff";
    }

    // ✅ LINE userId で「初回/既存」を振り分け
    @GetMapping("/liff/resolve")
    public String resolve(@RequestParam String lineUserId) {
        boolean exists = userRepo.findByLineUserId(lineUserId).isPresent();
        if (exists) {
            return "redirect:/login"; // 既存 → ログインへ
        }
        return "redirect:/signup?lineUserId=" + lineUserId; // 初回 → 会員登録へ
    }
}
