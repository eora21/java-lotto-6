package domain;

import dto.LottoStatisticsResult;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class LottoWinningStatistics {
    private LottoWinningStatistics() {

    }

    public static LottoStatisticsResult calculateStatistics(Amount purchaseAmount,
                                                            List<Optional<LottoWinningTier>> optionalTiers) {
        Map<LottoWinningTier, Integer> winningTierCountStatistics = createWinningTierCountStatistics();
        calculateWinningTierCount(winningTierCountStatistics, optionalTiers);

        Amount totalWinningAmount = calculateTotalWinningAmount(winningTierCountStatistics);
        double percentOfTotalWinningAmount = totalWinningAmount.calculatePercentByUnitAmount(purchaseAmount);

        return new LottoStatisticsResult(winningTierCountStatistics, percentOfTotalWinningAmount);
    }

    private static Map<LottoWinningTier, Integer> createWinningTierCountStatistics() {
        Map<LottoWinningTier, Integer> winningTierCountStatistics = new EnumMap<>(LottoWinningTier.class);
        Arrays.stream(LottoWinningTier.values())
                .forEach(tier -> winningTierCountStatistics.put(tier, 0));

        return winningTierCountStatistics;
    }

    private static void calculateWinningTierCount(Map<LottoWinningTier, Integer> winningTierCountStatistics,
                                                  List<Optional<LottoWinningTier>> optionalTiers) {
        optionalTiers.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(tier -> increaseWinningTierCount(winningTierCountStatistics, tier));
    }

    private static void increaseWinningTierCount(Map<LottoWinningTier, Integer> winningTierCountStatistics,
                                                 LottoWinningTier winningTier) {
        winningTierCountStatistics.put(winningTier, addOneToBeforeValue(winningTierCountStatistics, winningTier));
    }

    private static int addOneToBeforeValue(Map<LottoWinningTier, Integer> winningTierCountStatistics,
                                           LottoWinningTier winningTier) {
        return winningTierCountStatistics.get(winningTier) + 1;
    }

    private static Amount calculateTotalWinningAmount(Map<LottoWinningTier, Integer> winningTierCountStatistics) {
        return winningTierCountStatistics.entrySet()
                .stream()
                .map(LottoWinningStatistics::calculateTotalTierAmount)
                .reduce(Amount::plusAmount)
                .orElseGet(Amount::createZeroAmount);
    }

    private static Amount calculateTotalTierAmount(Entry<LottoWinningTier, Integer> tierCountEntry) {
        LottoWinningTier tier = tierCountEntry.getKey();
        Integer count = tierCountEntry.getValue();
        return tier.getPrizeAmount()
                .calculateMultiplyAmount(count);
    }
}
