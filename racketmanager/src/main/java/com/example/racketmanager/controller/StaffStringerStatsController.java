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

        // ▼ 初回表示
        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        // ▼ 10日締めの計算
        LocalDate end = LocalDate.of(year, month, 10);
        LocalDate start = end.minusMonths(1).plusDays(1);

        // DB 取得
        List<RacketOrder> orders = orderRepo.findByDueDateBetween(start, end);

        // ▼ 張り人ごとに集計（Map 使用）
        // Map<String name, int[poly, nylon, natural]>
        Map<String, int[]> stats = new LinkedHashMap<>();

        for (RacketOrder o : orders) {

            String name = o.getStringerName();
            if (name == null || name.isBlank()) {
                name = "不明";
            }

            String material = o.getStringMaterial();
            if (material == null) continue;

            stats.putIfAbsent(name, new int[]{0, 0, 0});
            int[] arr = stats.get(name);

            switch (material) {
                case "ポリエステル": arr[0]++; break;
                case "ナイロン":     arr[1]++; break;
                case "ナチュラル":   arr[2]++; break;
            }
        }

        // ▼ HTML へ渡す List 形式に変換
        List<Map<String, Object>> stringerStats = new ArrayList<>();

        for (String name : stats.keySet()) {
            int[] arr = stats.get(name);

            int poly = arr[0];
            int nylon = arr[1];
            int natural = arr[2];

            int total = poly * 800 + nylon * 800 + natural * 1000;

            Map<String, Object> row = new HashMap<>();
            row.put("name", name);
            row.put("poly", poly);
            row.put("nylon", nylon);
            row.put("natural", natural);
            row.put("total", total);

            stringerStats.add(row);
        }

        // ▼ 用意したデータを model へ
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start.toString());
        model.addAttribute("end", end.toString());
        model.addAttribute("stringerStats", stringerStats);

        return "staff_stringer_stats";
    }
}
