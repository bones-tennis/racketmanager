package com.example.racketmanager.controller;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.racketmanager.repository.RacketOrderRepository;
import com.example.racketmanager.repository.UserRepository;
import com.example.racketmanager.security.EncryptionUtil;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final RacketOrderRepository orderRepo;
    private final UserRepository userRepo;

    public CustomerController(RacketOrderRepository orderRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    // ✅ 自分の依頼一覧
    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        // 👇 hash → encrypt に修正
        String encryptedUsername = EncryptionUtil.encrypt(principal.getName());

        // 👇 findByHashedUsername → findByUsername に修正
        return userRepo.findByUsername(encryptedUsername)
            .map(user -> {
                var orders = orderRepo.findByCustomer(user);
                model.addAttribute("orders", orders);
                return "customer_orders";
            })
            .orElse("redirect:/login");
    }

    // ✅ マイページ
    @GetMapping("/mypage")
    public String myPage(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String encryptedUsername = EncryptionUtil.encrypt(principal.getName());
        return userRepo.findByUsername(encryptedUsername)
            .map(user -> {
                model.addAttribute("user", user);
                return "customer_mypage";
            })
            .orElse("redirect:/login");
    }

    // ✅ マイページ情報更新
    @PostMapping("/mypage/update")
    public String updateMyPage(
            @RequestParam String username,
            @RequestParam String email,
            Principal principal,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (principal == null) return "redirect:/login";

        String encryptedUsername = EncryptionUtil.encrypt(principal.getName());
        return userRepo.findByUsername(encryptedUsername)
            .map(user -> {
                System.out.println("更新前 username: " + user.getUsername());

                // 🔐 更新時もencryptして保存
                user.setUsername(EncryptionUtil.encrypt(username));
                userRepo.save(user);

                System.out.println("更新後 username: " + username);
                System.out.println("更新後 email: " + email);

                redirectAttributes.addFlashAttribute("updated", true);
                new SecurityContextLogoutHandler().logout(request, response, null);
                return "redirect:/login?usernameChanged";
            })
            .orElse("redirect:/login");
    }
}
