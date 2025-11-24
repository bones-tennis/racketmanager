package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
    public String statsPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        LocalDate today = LocalDate.now();
        if (year == null) year = today.getYear();
        if (month == null) month = today.getMonthValue();

        LocalDate start = LocalDate.of(year, month, 1).withDayOfMonth(11);
        LocalDate end = start.plusMonths(1).withDayOfMonth(10);

        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        // stringerName ごとに集計
        Map<String, int[]> stats = new HashMap<>();
        Map<String, Integer> revenueMap = new HashMap<>();

        for (RacketOrder o : orders) {

            if (o.getStringerName() == null || o.getStringerName().isBlank()) {
                continue; // 無名の張り人は無視
            }

            String name = o.getStringerName();
            stats.putIfAbsent(name, new int[] {0, 0, 0});
            revenueMap.putIfAbsent(name, 0);

            int[] arr = stats.get(name);
            int revenue = revenueMap.get(name);

            String m = o.getStringMaterial();

            if (m == null) continue;

            switch (m) {
                case "ポリエステル":
                    arr[0]++;
                    revenue += (o.getPrice() != null ? o.getPrice() : 0) - 800;
                    break;
                case "ナイロン":
                    arr[1]++;
                    revenue += (o.getPrice() != null ? o.getPrice() : 0) - 800;
                    break;
                case "ナチュラル":
                    arr[2]++;
                    revenue += (o.getPrice() != null ? o.getPrice() : 0) - 1000;
                    break;
            }

            revenueMap.put(name, revenue);
        }

        // 表示用リストに整形
        List<Map<String, Object>> stringerStats = new ArrayList<>();

        for (String name : stats.keySet()) {
            int[] a = stats.get(name);
            Map<String, Object> row = new HashMap<>();
            row.put("name", name);
            row.put("poly", a[0]);
            row.put("nylon", a[1]);
            row.put("natural", a[2]);
            row.put("total", revenueMap.get(name));
            stringerStats.add(row);
        }

        model.addAttribute("stringerStats", stringerStats);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        return "staff_stringer_stats";
    }
}
