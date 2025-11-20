package com.example.racketmanager.controller;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.model.User;
import com.example.racketmanager.repository.RacketOrderRepository;
import com.example.racketmanager.repository.UserRepository;
import com.example.racketmanager.security.EncryptionUtil;

@Controller
@RequestMapping("/staff/customers")
public class StaffCustomerController {

    private final UserRepository userRepo;
    private final RacketOrderRepository orderRepo;
    private final PasswordEncoder passwordEncoder;

    public StaffCustomerController(UserRepository userRepo,
                                   RacketOrderRepository orderRepo,
                                   PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ===== 顧客一覧（検索機能付き） =====
    @GetMapping
    public String listCustomers(@RequestParam(required = false) String keyword, Model model) {
        List<User> customers = userRepo.findByRole("ROLE_CUSTOMER");

        if (keyword != null && !keyword.isBlank()) {
            String lower = keyword.toLowerCase();
            customers = customers.stream()
                    .filter(c -> c.getDisplayName() != null &&
                                 c.getDisplayName().toLowerCase().contains(lower))
                    .toList();
        }

        model.addAttribute("customers", customers);
        model.addAttribute("keyword", keyword);
        return "staff_customers";
    }

    // ===== 顧客編集 =====
    @GetMapping("/{id}/edit")
    public String editCustomer(@PathVariable Long id, Model model) {
        return userRepo.findById(id)
                .map(customer -> {
                    model.addAttribute("customer", customer);
                    return "staff_edit_customer";
                })
                .orElse("redirect:/staff/customers");
    }

    @PostMapping("/{id}/update")
    public String updateCustomer(
            @PathVariable Long id,
            @RequestParam String displayName,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String password) {

        userRepo.findById(id).ifPresent(user -> {
            user.setDisplayName(displayName);
            user.setUsername(EncryptionUtil.encrypt(username));

            if (!"google".equals(user.getProvider())) {
                user.setEmail(EncryptionUtil.encrypt(email));
            }

            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepo.save(user);
        });

        return "redirect:/staff/customers";
    }

    // ===== 顧客削除（依頼のcustomer_idもnullに） =====
    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id) {
        userRepo.findById(id).ifPresent(user -> {
            orderRepo.findByCustomer(user).forEach(order -> {
                order.setCustomer(null);  // 顧客削除時に外部キー解除
                orderRepo.save(order);
            });
            userRepo.delete(user);
        });

        return "redirect:/staff/customers";
    }
}
