package com.example.hackerthon.Service;

import com.example.hackerthon.Client.AiModelClient;
import com.example.hackerthon.Client.HeliusApiClient;
import com.example.hackerthon.Dto.Response.PredictResponse;
import com.example.hackerthon.Dto.Response.RugPullResponse;
import com.example.hackerthon.Utils.CheckLiquidity;
import com.example.hackerthon.Utils.FeatureUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HeliusService {
    HeliusApiClient heliusApiClient;
    AiModelClient aiModelClient;
    Set<String> relevantTypes = Set.of(
            "ADD_LIQUIDITY", "REMOVE_LIQUIDITY", "SWAP", "UNKNOWN",
            "INCREASE_LIQUIDITY", "DECREASE_LIQUIDITY", "LIQUIDITY_ADD", "LIQUIDITY_REMOVE"
    );
    String heliusApiKey = "5862dba5-6c72-4f88-a463-b2a0edfbf675"; // Cân nhắc đưa vào properties file
    RestTemplate restTemplate = new RestTemplate();
    Duration INACTIVITY_THRESHOLD = Duration.ofDays(30);
    CheckLiquidity checkLiquidity;

    private double parsePositiveDouble(String value) {
        try {
            return Math.abs(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            log.warn("Cannot parse double value: {}", value);
            return 0.0;
        }
    }



    public RugPullResponse getPredict(String address) {
        try {
            PredictResponse predictResponse= extractPoolFeatures(address);
            return aiModelClient.predict(predictResponse);
        } catch (Exception e) {
            log.error("Error extracting features for address {}: {}", address, e.getMessage(), e);
            return null;
        }
    }

    public PredictResponse extractPoolFeatures(String address) throws Exception {
        JsonNode root = heliusApiClient.getTransactions(address,heliusApiKey);
        if (root == null || !root.isArray()) {
            log.warn("No transaction array returned for address {}", address);
            throw new Exception("No transactions found for address " + address); // Thêm thông tin address vào exception
        }

        Set<String> mintTokens = new HashSet<>();

        // Sử dụng BigDecimal để tích lũy tổng cho độ chính xác cao
        BigDecimal cumulativeTotalAddedLiquidityBD = BigDecimal.ZERO;
        BigDecimal cumulativeTotalRemovedLiquidityBD = BigDecimal.ZERO;

        int numLiquidityAdds = 0;
        int numLiquidityRemoves = 0;

        long lastSwapTimestamp = 0;
        String lastSwapTxId = null;

        List<Long> timestamps = new ArrayList<>();

        for (JsonNode tx : root) {
            String type = tx.path("type").asText();
            if (!relevantTypes.contains(type)) {
                continue;
            }

            long ts = tx.path("timestamp").asLong(0);
            if (ts > 0) timestamps.add(ts);

            boolean isAddTxType = checkLiquidity.isAddLiquidityType(type);
            boolean isRemoveTxType = checkLiquidity.isRemoveLiquidityType(type);

            JsonNode tokenTransfers = tx.path("tokenTransfers");
            if (tokenTransfers.isArray()) {
                // Không khởi tạo lại BigDecimal ở đây nữa
                // BigDecimal totalAddedLiquidityBD_Tx = BigDecimal.ZERO; // Biến này không cần thiết nếu chỉ cộng dồn
                // BigDecimal totalRemovedLiquidityBD_Tx = BigDecimal.ZERO;
                log.info("Processing tokenTransfers for tx type: {}", type);

                for (JsonNode tt : tokenTransfers) {
                    String mint = tt.path("mint").asText(null);
                    if (mint != null) mintTokens.add(mint);

                    String tokenAmountStr = tt.path("tokenAmount").asText("0");
                    // log.info("Current tt: {}, tokenAmountStr: {}", tt.toString(), tokenAmountStr); // Giảm bớt log chi tiết

                    double parsedAmount = parsePositiveDouble(tokenAmountStr); // Luôn dương
                    // log.info("Parsed double amount for tokenTransfers: {}", parsedAmount);

                    BigDecimal amountBD = BigDecimal.valueOf(parsedAmount); // Chuyển sang BigDecimal
                    // log.info("BigDecimal amount for tokenTransfers: {}", amountBD.toPlainString());

                    // log.info("Value of 'type' being checked for tokenTransfers: {}", type);
                    if (isAddTxType) {
                        cumulativeTotalAddedLiquidityBD = cumulativeTotalAddedLiquidityBD.add(amountBD);
                        log.debug("Added to cumulativeTotalAddedLiquidityBD (tokenTransfers): {}, new total: {}", amountBD.toPlainString(), cumulativeTotalAddedLiquidityBD.toPlainString());
                    } else if (isRemoveTxType) {
                        cumulativeTotalRemovedLiquidityBD = cumulativeTotalRemovedLiquidityBD.add(amountBD);
                        log.debug("Added to cumulativeTotalRemovedLiquidityBD (tokenTransfers): {}, new total: {}", amountBD.toPlainString(), cumulativeTotalRemovedLiquidityBD.toPlainString());
                    }
                }
                // Không gán vào biến double ở đây nữa
                // log.info("After tokenTransfers for this tx: cumulativeTotalAddedLiquidityBD: {}, cumulativeTotalRemovedLiquidityBD: {}", cumulativeTotalAddedLiquidityBD.toPlainString(), cumulativeTotalRemovedLiquidityBD.toPlainString());
            } else {
                log.warn("tokenTransfers is not an array or is null for tx type: {}", type);
            }

            // Xử lý accountData (thường chứa thông tin chi tiết hơn hoặc là nguồn thay thế)
            JsonNode accountDataArray = tx.path("accountData");
            if (accountDataArray.isArray()) {
                for (JsonNode accData : accountDataArray) { // Đổi tên biến để tránh nhầm lẫn với acc
                    JsonNode tokenBalanceChanges = accData.path("tokenBalanceChanges");
                    if (tokenBalanceChanges.isArray()) {
                        for (JsonNode change : tokenBalanceChanges) {
                            String mint = change.path("mint").asText(null);
                            if (mint != null) mintTokens.add(mint);

                            // Giả định rawTokenAmount.tokenAmount là số lượng token đã có decimal
                            // và có thể âm hoặc dương. Chúng ta dùng parsePositiveDouble để lấy giá trị tuyệt đối.
                            String rawAmountStr = change.path("rawTokenAmount").path("tokenAmount").asText("0");
                            // log.info("rawAmountStr from accountData: "+rawAmountStr);

                            double rawAmount = parsePositiveDouble(rawAmountStr); // Luôn dương
                            int decimals = change.path("rawTokenAmount").path("decimals").asInt(0);
                            double amount = rawAmount; // Nếu tokenAmount đã là số lượng thực
                            if (decimals > 0 && rawAmountStr.matches("\\d+")) { // Chỉ chia nếu rawAmountStr là số nguyên không dấu (raw value)
                                amount = rawAmount / Math.pow(10, decimals);
                            }
                            // log.info("Parsed amount from accountData: {}", amount);

                            BigDecimal amountChangeBD = BigDecimal.valueOf(amount);

                            // Logic ở đây cần xem xét cẩn thận:
                            // `parsePositiveDouble` làm mất dấu.
                            // Nếu một giao dịch `ADD_LIQUIDITY` liên quan đến việc token giảm ở ví người dùng và tăng ở ví pool,
                            // hoặc token được chuyển đến pool.
                            // Dựa vào logic cũ của bạn: `totalAddedLiquidity += amount` khi amount > 0
                            // và `totalRemovedLiquidity += -amount` khi amount < 0 (sau đó đổi thành += amount với amount dương)
                            // Điều này có nghĩa là bạn đang dùng `type` của giao dịch để quyết định cộng vào added hay removed,
                            // và `amount` từ `tokenBalanceChanges` (đã được `parsePositiveDouble`) là độ lớn.
                            if (isAddTxType) {
                                if (amountChangeBD.compareTo(BigDecimal.ZERO) > 0) {
                                    cumulativeTotalAddedLiquidityBD = cumulativeTotalAddedLiquidityBD.add(amountChangeBD);
                                    log.debug("Added to cumulativeTotalAddedLiquidityBD (accountData): {}, new total: {}", amountChangeBD.toPlainString(), cumulativeTotalAddedLiquidityBD.toPlainString());
                                }
                            } else if (isRemoveTxType) {
                                // Logic cũ: if (amount < 0) totalRemovedLiquidity += -amount;
                                // Vì amount từ parsePositiveDouble luôn >= 0, điều kiện amount < 0 không bao giờ đúng.
                                // Sửa lại: Nếu là loại giao dịch remove, thì amount này được tính là removed.
                                if (amountChangeBD.compareTo(BigDecimal.ZERO) > 0) {
                                    cumulativeTotalRemovedLiquidityBD = cumulativeTotalRemovedLiquidityBD.add(amountChangeBD);
                                    log.debug("Added to cumulativeTotalRemovedLiquidityBD (accountData): {}, new total: {}", amountChangeBD.toPlainString(), cumulativeTotalRemovedLiquidityBD.toPlainString());
                                }
                            }
                        }
                    }
                }
            }

            // Cập nhật số lần thêm/xóa dựa trên loại giao dịch
            if (isAddTxType) numLiquidityAdds++;
            else if (isRemoveTxType) numLiquidityRemoves++;

            // Xử lý SWAP/UNKNOWN
            if ("SWAP".equals(type) || "UNKNOWN".equals(type)) { // Cần xem xét "UNKNOWN" có nên tính là SWAP không
                // Nếu "UNKNOWN" có thể là liquidity event, cần logic riêng
                if (ts > lastSwapTimestamp) {
                    lastSwapTimestamp = ts;
                    lastSwapTxId = tx.path("signature").asText(null);
                }
            }
        } // Kết thúc vòng lặp qua các giao dịch

        // Chuyển đổi BigDecimal tổng sang double SAU KHI vòng lặp kết thúc
        double finalTotalAddedLiquidity = cumulativeTotalAddedLiquidityBD.doubleValue();
        double finalTotalRemovedLiquidity = cumulativeTotalRemovedLiquidityBD.doubleValue();

        log.info("Final cumulative values for address {}: Added={}, Removed={}, NumAdds={}, NumRemoves={}",
                address, finalTotalAddedLiquidity, finalTotalRemovedLiquidity, numLiquidityAdds, numLiquidityRemoves);


        long firstTs = timestamps.stream().min(Long::compareTo).orElse(-1L);
        long lastTs = timestamps.stream().max(Long::compareTo).orElse(-1L);

        Double addToRemoveRatio = FeatureUtils.safeDivide(numLiquidityAdds, numLiquidityRemoves);
        boolean inactivityStatus = FeatureUtils.checkInactivity(lastTs, INACTIVITY_THRESHOLD);

        Map<String, Double> firstTsMap = FeatureUtils.breakdownTimestamp(firstTs);
        Map<String, Double> lastTsMap = FeatureUtils.breakdownTimestamp(lastTs);
        Map<String, Double> lastSwapTsMap = FeatureUtils.breakdownTimestamp(lastSwapTimestamp);

        return PredictResponse.builder()
                .TOTAL_ADDED_LIQUIDITY(finalTotalAddedLiquidity) // Sử dụng giá trị cuối cùng
                .TOTAL_REMOVED_LIQUIDITY(finalTotalRemovedLiquidity) // Sử dụng giá trị cuối cùng
                .NUM_LIQUIDITY_ADDS(numLiquidityAdds)
                .NUM_LIQUIDITY_REMOVES(numLiquidityRemoves)
                .ADD_TO_REMOVE_RATIO(addToRemoveRatio)
                /*.LAST_SWAP_TRANSACTION_ID(lastSwapTxId)
                .INACTIVITY_STATUS(inactivityStatus)*/
                .INACTIVITY_STATUS_Active(!inactivityStatus)
                .INACTIVITY_STATUS_Inactive(inactivityStatus)

                .FIRST_POOL_ACTIVITY_TIMESTAMP_hour(firstTsMap.get("hour"))
                .FIRST_POOL_ACTIVITY_TIMESTAMP_day(firstTsMap.get("day"))
                .FIRST_POOL_ACTIVITY_TIMESTAMP_weekday(firstTsMap.get("weekday"))
                .FIRST_POOL_ACTIVITY_TIMESTAMP_month(firstTsMap.get("month"))

                .LAST_POOL_ACTIVITY_TIMESTAMP_hour(lastTsMap.get("hour"))
                .LAST_POOL_ACTIVITY_TIMESTAMP_day(lastTsMap.get("day"))
                .LAST_POOL_ACTIVITY_TIMESTAMP_weekday(lastTsMap.get("weekday"))
                .LAST_POOL_ACTIVITY_TIMESTAMP_month(lastTsMap.get("month"))

                .LAST_SWAP_TIMESTAMP_hour(lastSwapTsMap.get("hour"))
                .LAST_SWAP_TIMESTAMP_day(lastSwapTsMap.get("day"))
                .LAST_SWAP_TIMESTAMP_weekday(lastSwapTsMap.get("weekday"))
                .LAST_SWAP_TIMESTAMP_month(lastSwapTsMap.get("month"))
                .build();
    }
}