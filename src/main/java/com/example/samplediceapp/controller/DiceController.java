package com.example.samplediceapp.controller;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

@RestController
public class DiceController {

    private static final Logger logger = Logger.getLogger(DiceController.class.getName());
    private final Random random = new Random();
    
    @Autowired
    private Tracer tracer;  // OpenTelemetryのTracerを自動注入

    @GetMapping("/roll")
    @WithSpan  // このメソッドを自動的にトレースするアノテーション
    public Map<String, Object> rollDice(
            @RequestParam(value = "sides", defaultValue = "6") int sides,
            @RequestParam(value = "rolls", defaultValue = "1") int rolls) {

        logger.info("Rolling a " + sides + "-sided dice " + rolls + " times");

        // 現在のSpanを取得し、カスタム属性を追加
        Span span = Span.current();
        span.setAttribute("dice.sides", sides);
        span.setAttribute("dice.rolls", rolls);

        Map<String, Object> result = new HashMap<>();
        int[] values = new int[rolls];

        // サイコロを振る（カスタムSpanでトレース）
        for (int i = 0; i < rolls; i++) {
            values[i] = rollSingleDice(sides);
        }

        result.put("sides", sides);
        result.put("rolls", rolls);
        result.put("values", values);

        return result;
    }

    @WithSpan  // このメソッドも自動的にトレース
    private int rollSingleDice(int sides) {
        Span.current().setAttribute("dice.sides", sides);
        
        // 故意に遅延を追加してスパンを見やすくする
        try {
            Thread.sleep(random.nextInt(50) + 10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return random.nextInt(sides) + 1;
    }
}
