package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.repository.RacketOrderRepository;

@Controller
@RequestMapping("/staff/sales")
public class StaffSalesController {

    private final RacketOrderRepository orderRepo;

    public StaffSalesController(RacketOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    // 売上ページ表示
    @GetMapping
    public String salesPage(
            @RequestParam(required = false) String targetMonth,
            Model model) {

        // 例: targetMonth = "2025-01"
        LocalDate start;
        LocalDate end;

        if (targetMonth == null) {
            LocalDate now = LocalDate.now();
            start = LocalDate.of(now.getYear(), now.getMonth(), 11).minusMonths(1);
            end = LocalDate.of(now.getYear(), now.getMonth(), 10);
        } else {
            LocalDate base = LocalDate.parse(targetMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            start = LocalDate.of(base.getYear(), base.getMonth(), 11).minusMonths(1);
            end = LocalDate.of(base.getYear(), base.getMonth(), 10);
        }

        // 対象期間の注文を取得
        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        int polyesterCount = 0;
        int nylonCount = 0;
        int naturalCount = 0;

        for (RacketOrder o : orders) {
            if (o.getStringMaterial() == null) continue;

            switch (o.getStringMaterial()) {
                case "ポリエステル" -> polyesterCount++;
                case "ナイロン" -> nylonCount++;
                case "ナチュラル" -> naturalCount++;
            }
        }

        int total =
                polyesterCount * 800 +
                nylonCount * 800 +
                naturalCount * 1000;

        model.addAttribute("polyesterCount", polyesterCount);
        model.addAttribute("nylonCount", nylonCount);
        model.addAttribute("naturalCount", naturalCount);
        model.addAttribute("total", total);

        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("targetMonth", targetMonth);

        return "staff_sales";
    }
}
