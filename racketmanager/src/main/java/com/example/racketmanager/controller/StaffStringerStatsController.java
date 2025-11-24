package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.repository.RacketOrderRepository;

@Controller
@RequestMapping("/staff/stringers")
public class StaffStringerStatsController {

    private final RacketOrderRepository orderRepo;

    public StaffStringerStatsController(RacketOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping
    public String stringerStats(
            @RequestParam(required = false) String targetMonth,
            Model model) {

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

        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        // 集計用
        Map<String, Integer> polyester = new HashMap<>();
        Map<String, Integer> nylon = new HashMap<>();
        Map<String, Integer> natural = new HashMap<>();

        for (RacketOrder o : orders) {
            if (o.getStringerName() == null || o.getStringMaterial() == null) continue;

            String name = o.getStringerName();
            switch (o.getStringMaterial()) {
                case "ポリエステル" -> polyester.merge(name, 1, Integer::sum);
                case "ナイロン" -> nylon.merge(name, 1, Integer::sum);
                case "ナチュラル" -> natural.merge(name, 1, Integer::sum);
            }
        }

        model.addAttribute("polyester", polyester);
        model.addAttribute("nylon", nylon);
        model.addAttribute("natural", natural);

        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("targetMonth", targetMonth);

        return "staff_stringer_stats";
    }
}
