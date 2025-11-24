package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.repository.RacketOrderRepository;

@Controller
public class StaffSalesController {

    private final RacketOrderRepository orderRepo;

    public StaffSalesController(RacketOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/staff/sales")
    public String sales(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        // ▼ 初回表示：今年の今月に自動設定
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        // ▼ 〆日ロジック（月10日締め）
        // 例：11月分 → 10月11日〜11月10日
        YearMonth ym = YearMonth.of(year, month);
        LocalDate end = LocalDate.of(year, month, 10);
        LocalDate start = end.minusMonths(1).plusDays(1);

        // DB取得
        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        // ▼ 本数カウント
        int poly = 0, nylon = 0, natural = 0;

        for (RacketOrder o : orders) {
            if (o.getStringMaterial() == null) continue;

            switch (o.getStringMaterial()) {
                case "ポリエステル": poly++; break;
                case "ナイロン": nylon++; break;
                case "ナチュラル": natural++; break;
            }
        }

        // ▼ 売上計算
        int polyPrice = poly * 800;
        int nylonPrice = nylon * 800;
        int naturalPrice = natural * 1000;
        int salesTotal = polyPrice + nylonPrice + naturalPrice;

        // ▼ HTML へ値を渡す
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start.toString());
        model.addAttribute("end", end.toString());

        model.addAttribute("poly", poly);
        model.addAttribute("nylon", nylon);
        model.addAttribute("natural", natural);

        model.addAttribute("polyPrice", polyPrice);
        model.addAttribute("nylonPrice", nylonPrice);
        model.addAttribute("naturalPrice", naturalPrice);
        model.addAttribute("salesTotal", salesTotal);

        return "staff_sales";
    }
}
