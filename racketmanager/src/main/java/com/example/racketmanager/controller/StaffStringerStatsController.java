package com.example.racketmanager.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.racketmanager.dto.StringerStats;
import com.example.racketmanager.repository.RacketOrderRepository;

@Controller
@RequestMapping("/staff")
public class StaffStringerStatsController {

    private final RacketOrderRepository orderRepo;

    public StaffStringerStatsController(RacketOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/stringer-stats")
    public String stats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        LocalDate now = LocalDate.now();
        if (year == null) year = now.getYear();
        if (month == null) month = now.getMonthValue();

        LocalDate start = LocalDate.of(year, month, 11).minusMonths(1);
        LocalDate end = LocalDate.of(year, month, 10);

        var orders = orderRepo.findByDueDateBetween(start, end);

        // 張り人名ごとに集計
        Map<String, List<String>> materialsByStringer =
                orders.stream()
                        .filter(o -> o.getStringerName() != null)
                        .collect(Collectors.groupingBy(
                                o -> o.getStringerName(),
                                Collectors.mapping(o -> o.getStringMaterial(), Collectors.toList())
                        ));

        // DTO のリスト
        List<StringerStats> statsList = new ArrayList<>();

        for (String stringer : materialsByStringer.keySet()) {
            List<String> list = materialsByStringer.get(stringer);

            int poly = (int) list.stream().filter(v -> v.equals("ポリエステル")).count();
            int nylon = (int) list.stream().filter(v -> v.equals("ナイロン")).count();
            int natural = (int) list.stream().filter(v -> v.equals("ナチュラル")).count();

            statsList.add(new StringerStats(stringer, poly, nylon, natural));
        }

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("stringerStats", statsList);

        return "staff_stringer_stats";
    }
}
