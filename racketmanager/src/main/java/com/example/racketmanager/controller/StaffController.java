package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.repository.RacketOrderRepository;
import com.example.racketmanager.repository.UserRepository;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final RacketOrderRepository orderRepo;
    private final UserRepository userRepo;

    public StaffController(RacketOrderRepository orderRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    // ======================== 依頼一覧 =============================
    @GetMapping("/orders")
    public String orders(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String stringType,
            @RequestParam(required = false) String stringMaterial,
            @RequestParam(required = false) String stringerName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sort,
            Model model) {

        Sort sortSpec = Sort.unsorted();
        if ("dueDateAsc".equalsIgnoreCase(sort)) {
            sortSpec = Sort.by(Sort.Direction.ASC, "dueDate");
        } else if ("dueDateDesc".equalsIgnoreCase(sort)) {
            sortSpec = Sort.by(Sort.Direction.DESC, "dueDate");
        }

        List<RacketOrder> orders = sortSpec.isUnsorted()
                ? orderRepo.findAll()
                : orderRepo.findAll(sortSpec);

        // --- フィルタリング ---
        if (customerName != null && !customerName.isBlank()) {
            orders = orders.stream()
                    .filter(o -> (o.getCustomer() != null && o.getCustomer().getDisplayName() != null
                            && o.getCustomer().getDisplayName().contains(customerName))
                            || (o.getCustomer() == null && o.getCustomerName() != null
                            && o.getCustomerName().contains(customerName)))
                    .toList();
        }
        if (stringType != null && !stringType.isBlank()) {
            orders = orders.stream()
                    .filter(o -> o.getStringType() != null && o.getStringType().contains(stringType))
                    .toList();
        }
        if (stringMaterial != null && !stringMaterial.isBlank()) {
            orders = orders.stream()
                    .filter(o -> stringMaterial.equals(o.getStringMaterial()))
                    .toList();
        }
        if (stringerName != null && !stringerName.isBlank()) {
            orders = orders.stream()
                    .filter(o -> o.getStringerName() != null && o.getStringerName().contains(stringerName))
                    .toList();
        }
        if (status != null && !status.isBlank()) {
            orders = orders.stream()
                    .filter(o -> status.equals(o.getStatus()))
                    .toList();
        }
        if (startDate != null && !startDate.isBlank() &&
                endDate != null && !endDate.isBlank()) {

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            orders = orders.stream()
                    .filter(o -> o.getDueDate() != null &&
                            !o.getDueDate().isBefore(start) &&
                            !o.getDueDate().isAfter(end))
                    .toList();
        }

        model.addAttribute("orders", orders);

        // 保存した検索条件も返す
        model.addAttribute("sort", sort == null ? "" : sort);
        model.addAttribute("customerName", customerName);
        model.addAttribute("stringType", stringType);
        model.addAttribute("stringMaterial", stringMaterial);
        model.addAttribute("stringerName", stringerName);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "staff_orders";
    }

    // ======================== 新規依頼登録 =============================
    @PostMapping("/orders")
    public String createOrder(
            @RequestParam String stringType,
            @RequestParam String stringMaterial,
            @RequestParam String tensionMain,
            @RequestParam String tensionCross,
            @RequestParam String dueDate,
            @RequestParam Long customerId,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String stringerName) {

        RacketOrder o = new RacketOrder();
        o.setStringType(stringType);
        o.setStringMaterial(stringMaterial);
        o.setTensionMain(tensionMain);
        o.setTensionCross(tensionCross);
        o.setDueDate(LocalDate.parse(dueDate));
        o.setRequestDate(LocalDate.now());
        o.setStatus("未着手");
        o.setPrice(price);
        o.setStringerName(stringerName);

        // 顧客紐付け
        userRepo.findById(customerId).ifPresent(customer -> {
            o.setCustomer(customer);
            o.setCustomerName(customer.getDisplayName());
        });

        orderRepo.save(o);
        return "redirect:/staff/orders";
    }

    @GetMapping("/orders/new")
    public String newOrderForm(Model model) {
        model.addAttribute("customers", userRepo.findByRole("ROLE_CUSTOMER"));
        model.addAttribute("racketOrder", new RacketOrder());
        return "staff_new_order";
    }

    // ======================== 編集フォーム =============================
    @GetMapping("/orders/{id}/edit")
    public String editOrderForm(@PathVariable Long id, Model model) {
        RacketOrder order = orderRepo.findById(id).orElse(null);
        if (order == null) return "redirect:/staff/orders";

        model.addAttribute("order", order);
        model.addAttribute("customers", userRepo.findByRole("ROLE_CUSTOMER"));
        return "staff_edit_order";
    }

    // ======================== 更新 =============================
    @PostMapping("/orders/{id}/update")
    public String updateOrder(
            @PathVariable Long id,
            @RequestParam(required = false) Long customerId,
            @RequestParam String stringType,
            @RequestParam String stringMaterial,
            @RequestParam String tensionMain,
            @RequestParam String tensionCross,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String stringerName,
            @RequestParam String dueDate,
            @RequestParam String status) {

        orderRepo.findById(id).ifPresent(order -> {
            order.setStringType(stringType);
            order.setStringMaterial(stringMaterial);
            order.setTensionMain(tensionMain);
            order.setTensionCross(tensionCross);
            order.setPrice(price);
            order.setStringerName(stringerName);
            order.setDueDate(LocalDate.parse(dueDate));
            order.setStatus(status);

            if (customerId != null) {
                userRepo.findById(customerId).ifPresent(customer -> {
                    order.setCustomer(customer);
                    order.setCustomerName(customer.getDisplayName());
                });
            } else {
                order.setCustomer(null);
            }

            orderRepo.save(order);
        });

        return "redirect:/staff/orders";
    }

    // ======================== ステータス変更 =============================
    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status) {
        orderRepo.findById(id).ifPresent(o -> {
            o.setStatus(status);
            orderRepo.save(o);
        });
        return "redirect:/staff/orders";
    }

    // ======================== 削除 =============================
    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderRepo.deleteById(id);
        return "redirect:/staff/orders";
    }
}
