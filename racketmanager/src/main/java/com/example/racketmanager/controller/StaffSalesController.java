package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.util.Arrays;
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

        // ▼ 初回表示（今年の今月）
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        // ▼ 10日締め（例：11月 → 10/11〜11/10）
        LocalDate end = LocalDate.of(year, month, 10);
        LocalDate start = end.minusMonths(1).plusDays(1);

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

        // ▼ 売上
        int polyPrice = poly * 800;
        int nylonPrice = nylon * 800;
        int naturalPrice = natural * 1000;
        int salesTotal = polyPrice + nylonPrice + naturalPrice;

        // ▼ 年間データ（12ヶ月）
        int[] polyYear = new int[12];
        int[] nylonYear = new int[12];
        int[] naturalYear = new int[12];

        for (int m = 1; m <= 12; m++) {
            LocalDate e = LocalDate.of(year, m, 10);
            LocalDate s = e.minusMonths(1).plusDays(1);

            List<RacketOrder> monthly = orderRepo.findByDueDateBetween(s, e);

            for (RacketOrder o : monthly) {
                if (o.getStringMaterial() == null) continue;

                switch (o.getStringMaterial()) {
                    case "ポリエステル": polyYear[m - 1]++; break;
                    case "ナイロン": nylonYear[m - 1]++; break;
                    case "ナチュラル": naturalYear[m - 1]++; break;
                }
            }
        }

        // ▼ model に渡す (★ここが重要★)
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

        model.addAttribute("polyYear", Arrays.toString(polyYear));
        model.addAttribute("nylonYear", Arrays.toString(nylonYear));
        model.addAttribute("naturalYear", Arrays.toString(naturalYear));

        return "staff_sales";
    }
}
