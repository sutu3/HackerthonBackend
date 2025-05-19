package com.example.hackerthon.Service;

import com.example.hackerthon.Dto.Response.PredictResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HeliusService {
    private final Set<String> relevantTypes = Set.of("ADD_LIQUIDITY", "REMOVE_LIQUIDITY", "SWAP", "UNKNOWN");
    String heliusApiKey = "5862dba5-6c72-4f88-a463-b2a0edfbf675";
    RestTemplate restTemplate = new RestTemplate();
    ObjectMapper mapper = new ObjectMapper();
    Duration INACTIVITY_THRESHOLD = Duration.ofDays(30);

    public String getPredict(String address) throws Exception {
        Map<String, Object> features = extractPoolFeatures(address);
        // TODO: gọi Flask server nếu cần
        return features.toString();
    }

    public Map<String, Object> extractPoolFeatures(String address) {
        String url = "https://api.helius.xyz/v0/addresses/" + address + "/transactions?api-key=" + heliusApiKey;
        log.info("Calling Helius API: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode root = response.getBody();
        if (root == null || !root.isArray()) {
            log.warn("No transaction array returned for address {}", address);
            return Collections.emptyMap();
        }

        Set<String> mintTokens = new HashSet<>();
        double totalAddedLiquidity = 0.0;
        int numLiquidityAdds = 0;

        double totalRemovedLiquidity = 0.0;
        int numLiquidityRemoves = 0;

        long lastSwapTimestamp = 0;
        String lastSwapTxId = null;

        List<Long> timestamps = new ArrayList<>();

        for (JsonNode tx : root) {
            String type = tx.path("type").asText();
            if (!relevantTypes.contains(type)) {
                continue; // Chỉ lấy các loại giao dịch liên quan
            }

            long ts = tx.path("timestamp").asLong(0);
            if (ts > 0) timestamps.add(ts);

            JsonNode tokenTransfers = tx.path("tokenTransfers");
            if (tokenTransfers.isArray()) {
                for (JsonNode tt : tokenTransfers) {
                    String mint = tt.path("mint").asText(null);
                    if (mint != null) {
                        mintTokens.add(mint);
                    }
                    double amount = tt.path("tokenAmount").asDouble(0);

                    if ("ADD_LIQUIDITY".equals(type)) {
                        totalAddedLiquidity += amount;
                    } else if ("REMOVE_LIQUIDITY".equals(type)) {
                        totalRemovedLiquidity += amount;
                    }
                }
            }

            if ("ADD_LIQUIDITY".equals(type)) {
                numLiquidityAdds++;
            } else if ("REMOVE_LIQUIDITY".equals(type)) {
                numLiquidityRemoves++;
            }

            if ("SWAP".equals(type) || "UNKNOWN".equals(type)) {
                if (ts > lastSwapTimestamp) {
                    lastSwapTimestamp = ts;
                    lastSwapTxId = tx.path("signature").asText(null);
                }
            }
        }

        long firstTs = timestamps.stream().min(Long::compareTo).orElse(0L);
        long lastTs = timestamps.stream().max(Long::compareTo).orElse(0L);

        Double addToRemoveRatio = null;
        if (numLiquidityRemoves > 0) {
            addToRemoveRatio = (double) numLiquidityAdds / numLiquidityRemoves;
        }

        long nowTs = Instant.now().getEpochSecond();
        boolean inactivityStatus = (lastTs > 0) && ((nowTs - lastTs) > INACTIVITY_THRESHOLD.getSeconds());

        Map<String, Object> features = new HashMap<>();
        features.put("LIQUIDITY_POOL_ADDRESS", address); // truyền từ ngoài, có thể thay bằng param thực
        features.put("MINT", new ArrayList<>(mintTokens));
        features.put("TOTAL_ADDED_LIQUIDITY", totalAddedLiquidity);
        features.put("TOTAL_REMOVED_LIQUIDITY", totalRemovedLiquidity);
        features.put("NUM_LIQUIDITY_ADDS", numLiquidityAdds);
        features.put("NUM_LIQUIDITY_REMOVES", numLiquidityRemoves);
        features.put("ADD_TO_REMOVE_RATIO", addToRemoveRatio);
        features.put("FIRST_POOL_ACTIVITY_TIMESTAMP", firstTs);
        features.put("LAST_POOL_ACTIVITY_TIMESTAMP", lastTs);
        features.put("LAST_SWAP_TIMESTAMP", lastSwapTimestamp == 0 ? null : lastSwapTimestamp);
        features.put("LAST_SWAP_TRANSACTION_ID", lastSwapTxId);
        features.put("INACTIVITY_STATUS", inactivityStatus);

        log.info("Extracted features for {} : {}", address, features);
        return features;
    }
}

