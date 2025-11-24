package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.util.ArrayList;
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

        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        // ==== ① 当月の締め期間 ====
        LocalDate start = LocalDate.of(year, month, 1).minusMonths(1).withDayOfMonth(11);
        LocalDate end = LocalDate.of(year, month, 10);

        List<RacketOrder> monthOrders = orderRepo.findByDueDateBetween(start, end);

        int poly = 0, nylon = 0, natural = 0;

        for (RacketOrder o : monthOrders) {
            if (o.getStringMaterial() == null) continue;
            switch (o.getStringMaterial()) {
                case "ポリエステル" -> poly++;
                case "ナイロン" -> nylon++;
                case "ナチュラル" -> natural++;
            }
        }

        int monthTotal = poly * 800 + nylon * 800 + natural * 1000;

        // ==== ② 年間の1〜12月集計 ====
        List<Integer> polyList = new ArrayList<>();
        List<Integer> nylonList = new ArrayList<>();
        List<Integer> naturalList = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            LocalDate ms = LocalDate.of(year, m, 1).minusMonths(1).withDayOfMonth(11);
            LocalDate me = LocalDate.of(year, m, 10);

            List<RacketOrder> mo = orderRepo.findByDueDateBetween(ms, me);

            int p = 0, n = 0, na = 0;
            for (RacketOrder o : mo) {
                if (o.getStringMaterial() == null) continue;
                switch (o.getStringMaterial()) {
                    case "ポリエステル" -> p++;
                    case "ナイロン" -> n++;
                    case "ナチュラル" -> na++;
                }
            }

            polyList.add(p);
            nylonList.add(n);
            naturalList.add(na);
        }

        // ==== Modelへ ====
        model.addAttribute("poly", poly);
        model.addAttribute("nylon", nylon);
        model.addAttribute("natural", natural);
        model.addAttribute("salesTotal", monthTotal);

        model.addAttribute("polyList", polyList);
        model.addAttribute("nylonList", nylonList);
        model.addAttribute("naturalList", naturalList);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "staff_sales";
    }
}
