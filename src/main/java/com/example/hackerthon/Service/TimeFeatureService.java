package com.example.hackerthon.Service;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TimeFeatureService {

    // Thực hiện tính toán các tính năng thời gian từ các sự kiện
    public Map<String, Object> computeTimeFeatures(List<Map<String, Object>> events, int inactivityDays) {
        long nowTs = System.currentTimeMillis() / 1000;  // lấy thời gian hiện tại tính theo giây
        long firstTs = Long.MAX_VALUE;
        long lastTs = Long.MIN_VALUE;
        Long lastSwapTs = null;
        String lastSwapTxId = null;

        // Lọc các sự kiện swap
        for (Map<String, Object> event : events) {
            long timestamp = (long) event.get("timestamp");

            // Lấy timestamp đầu tiên và cuối cùng
            firstTs = Math.min(firstTs, timestamp);
            lastTs = Math.max(lastTs, timestamp);

            if ("SWAP".equals(event.get("type"))) {
                lastSwapTs = timestamp;
                lastSwapTxId = (String) event.get("signature");
            }
        }

        // Tính inactivity (thời gian không hoạt động)
        long daysSinceLast = (lastTs != Long.MIN_VALUE) ? (nowTs - lastTs) / (60 * 60 * 24) : Long.MAX_VALUE;
        boolean inactive = daysSinceLast > inactivityDays;

        // Tạo các features trả về
        return Map.of(
                "FIRST_POOL_ACTIVITY_TIMESTAMP", firstTs == Long.MAX_VALUE ? null : firstTs,
                "LAST_POOL_ACTIVITY_TIMESTAMP", lastTs == Long.MIN_VALUE ? null : lastTs,
                "LAST_SWAP_TIMESTAMP", lastSwapTs,
                "LAST_SWAP_TX_ID", lastSwapTxId,
                "INACTIVITY_STATUS", inactive
        );
    }
}
