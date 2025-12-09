package org.example.demo_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Controller + @ResponseBody = @RestController
@Controller
public class Exam1Controller {

    // http://localhost:8080/example/test1
    @GetMapping("/example/test1")
    public String test1(Model model) {

        model.addAttribute("stringValue",
                "안녕 머스태치야~");
        model.addAttribute("intValue",
                1234);
        model.addAttribute("hasData",
                "false");
        model.addAttribute("data",
                "비밀글이야.");

        List<String> items = new ArrayList<>();
        items.add("사과");
        items.add("바나나");
        items.add("오렌지");

        model.addAttribute("items", items);

        Map<String, String> map = new HashMap<>();
        map.put("key1", "1234");
        map.put("key2", "5678");
        model.addAttribute("map", map);

        // "/templates/text1/mustache"
        return "test1";
    }

    // http://localhost:8080/example/test2
    @GetMapping("/example/test2")
    public String test2(Model model) {

        model.addAttribute("stringValue",
                "안녕 머스태치야~");
        model.addAttribute("intValue",
                1234);
        model.addAttribute("hasData",
                "false");
        model.addAttribute("data",
                "비밀글이야.");

        List<String> items = new ArrayList<>();
        items.add("사과");
        items.add("바나나");
        items.add("오렌지");

        model.addAttribute("items", items);

        Map<String, String> map = new HashMap<>();
        map.put("key1", "1234");
        map.put("key2", "5678");
        model.addAttribute("map", map);

        return "test2";
    }
}
