package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.repository.RacketOrderRepository;

@Controller
public class StaffStringerStatsController {

    private final RacketOrderRepository orderRepo;

    public StaffStringerStatsController(RacketOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/staff/stringer-stats")
    public String stats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        LocalDate end = LocalDate.of(year, month, 10);
        LocalDate start = end.minusMonths(1).plusDays(1);

        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        Map<String, int[]> stats = new LinkedHashMap<>();

        for (RacketOrder o : orders) {

            String name = (o.getStringerName() == null ? "不明" : o.getStringerName());
            stats.putIfAbsent(name, new int[]{0, 0, 0});

            switch (o.getStringMaterial()) {
                case "ポリエステル": stats.get(name)[0]++; break;
                case "ナイロン":     stats.get(name)[1]++; break;
                case "ナチュラル":   stats.get(name)[2]++; break;
            }
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (String s : stats.keySet()) {
            int[] arr = stats.get(s);
            int total = arr[0] * 800 + arr[1] * 800 + arr[2] * 1000;

            Map<String, Object> row = new HashMap<>();
            row.put("name", s);
            row.put("poly", arr[0]);
            row.put("nylon", arr[1]);
            row.put("natural", arr[2]);
            row.put("total", total);
            list.add(row);
        }

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("stringerStats", list);

        return "staff_stringer_stats";
    }
}
