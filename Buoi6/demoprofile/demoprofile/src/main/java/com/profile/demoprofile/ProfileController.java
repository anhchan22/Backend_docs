package com.profile.demoprofile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProfileController {
    @GetMapping("/profile")
    public String getProfile(Model model) {
        List<String> soThichList = List.of("Ăn", "Ngủ", "Du lịch", "Móc len");
        Profile profile = new Profile("Trần Ngọc Ánh", "B23DCAT022", "D23CQAT02-B",
                "trananh22052005@gmail.com", "Nam Định", "Độc thân", 20, soThichList);
        model.addAttribute("thongtin", profile);
        return "profile";
    }
}
