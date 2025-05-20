package com.example.hackerthon.Utils;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class FeatureUtils {

    public static Double safeDivide(int numerator, int denominator) {
        if (denominator == 0) return 0.0;
        return (double) numerator / denominator;
    }

    public static Map<String, Double> breakdownTimestamp(Long ts) {
        Map<String, Double> map = new HashMap<>();
        if (ts == null || ts <= 0) {
            map.put("hour", -1.0);
            map.put("day", -1.0);
            map.put("weekday", -1.0);
            map.put("month", -1.0);
        } else {
            ZonedDateTime zdt = Instant.ofEpochSecond(ts).atZone(ZoneId.of("UTC"));
            map.put("hour", (double) zdt.getHour());
            map.put("day", (double) zdt.getDayOfMonth());
            map.put("weekday", (double) zdt.getDayOfWeek().getValue());
            map.put("month", (double) zdt.getMonthValue());
        }
        return map;
    }

    public static boolean checkInactivity(long lastTimestamp, Duration threshold) {
        if (lastTimestamp <= 0) return true;
        long now = Instant.now().getEpochSecond();
        return (now - lastTimestamp) > threshold.getSeconds();
    }
}

