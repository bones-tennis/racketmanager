package com.example.racketmanager.controller;

import java.time.LocalDate;
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
    public String salesPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        // 年月が無い場合 → 今月扱い
        LocalDate today = LocalDate.now();
        if (year == null) year = today.getYear();
        if (month == null) month = today.getMonthValue();

        // 10日締め：当月11日〜翌月10日
        LocalDate start = LocalDate.of(year, month, 1).withDayOfMonth(11);
        LocalDate end = start.plusMonths(1).withDayOfMonth(10);

        // 該当期間の注文取得
        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        // 種類別カウント & 張り代（純利益）
        int poly = 0;
        int nylon = 0;
        int natural = 0;

        int polyRevenue = 0;
        int nylonRevenue = 0;
        int naturalRevenue = 0;

        for (RacketOrder o : orders) {
            if (o.getStringMaterial() == null) continue; // 念のため

            String m = o.getStringMaterial();

            switch (m) {
                case "ポリエステル":
                    poly++;
                    polyRevenue += (o.getPrice() != null ? o.getPrice() : 0) - 800;
                    break;
                case "ナイロン":
                    nylon++;
                    nylonRevenue += (o.getPrice() != null ? o.getPrice() : 0) - 800;
                    break;
                case "ナチュラル":
                    natural++;
                    naturalRevenue += (o.getPrice() != null ? o.getPrice() : 0) - 1000;
                    break;
            }
        }

        int totalRevenue = polyRevenue + nylonRevenue + naturalRevenue;

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        model.addAttribute("poly", poly);
        model.addAttribute("nylon", nylon);
        model.addAttribute("natural", natural);

        model.addAttribute("polyRevenue", polyRevenue);
        model.addAttribute("nylonRevenue", nylonRevenue);
        model.addAttribute("naturalRevenue", naturalRevenue);
        model.addAttribute("totalRevenue", totalRevenue);

        return "staff_sales";
    }
}
