package com.example.racketmanager.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.repository.RacketOrderRepository;

@Controller
@RequestMapping("/staff")
public class StaffSalesController {

    private final RacketOrderRepository orderRepo;

    public StaffSalesController(RacketOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/sales")
    public String showSales(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        LocalDate start = LocalDate.of(year, month, 11).minusMonths(1);
        LocalDate end = LocalDate.of(year, month, 10);

        var orders = orderRepo.findByDueDateBetween(start, end);

        int poly = (int) orders.stream()
                .filter(o -> "ポリエステル".equals(o.getStringMaterial()))
                .count();
        int nylon = (int) orders.stream()
                .filter(o -> "ナイロン".equals(o.getStringMaterial()))
                .count();
        int natural = (int) orders.stream()
                .filter(o -> "ナチュラル".equals(o.getStringMaterial()))
                .count();

        int polySales = poly * 800;
        int nylonSales = nylon * 800;
        int naturalSales = natural * 1000;
        int total = polySales + nylonSales + naturalSales;

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        model.addAttribute("poly", poly);
        model.addAttribute("nylon", nylon);
        model.addAttribute("natural", natural);

        model.addAttribute("polySales", polySales);
        model.addAttribute("nylonSales", nylonSales);
        model.addAttribute("naturalSales", naturalSales);
        model.addAttribute("salesTotal", total);

        return "staff_sales";
    }
}
