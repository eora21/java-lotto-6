package lotto.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.Amount;
import domain.LottoWinningStatistics;
import domain.LottoWinningTier;
import dto.LottoStatisticsResult;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LottoWinningStatisticsTest {
    @Test
    @DisplayName("불가능한 금액을 입력할 시 예외가 발생한다.")
    void calculateStatisticsByImpossibleAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                LottoWinningStatistics.calculateStatistics(new Amount(-1_000),
                        List.of(Optional.of(LottoWinningTier.FIRST_TIER))));
    }

    @ParameterizedTest
    @DisplayName("통계 정상 반환")
    @MethodSource("AmountWinningTiersAndExpectPercentOfTotalWinningAmount")
    void calculateStatistics(Amount amount, List<Optional<LottoWinningTier>> winningTiers, double expectPercent) {
        LottoStatisticsResult lottoStatisticsResult = assertDoesNotThrow(
                () -> LottoWinningStatistics.calculateStatistics(amount, winningTiers));

        assertThat(lottoStatisticsResult.getPercentOfTotalWinningAmount()).isEqualTo(expectPercent);
    }

    public static Stream<Arguments> AmountWinningTiersAndExpectPercentOfTotalWinningAmount() {
        return Stream.of(
                Arguments.of(new Amount(2_000),
                        List.of(Optional.empty(),
                                Optional.of(LottoWinningTier.FIRST_TIER)),
                        100_000_000),

                Arguments.of(new Amount(1_000),
                        List.of(Optional.of(LottoWinningTier.FIRST_TIER)),
                        200_000_000),

                Arguments.of(new Amount(3_000),
                        List.of(
                                Optional.of(LottoWinningTier.FIRST_TIER),
                                Optional.of(LottoWinningTier.FIRST_TIER),
                                Optional.of(LottoWinningTier.FIRST_TIER)),
                        200_000_000),

                Arguments.of(new Amount(8_000),
                        List.of(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.of(LottoWinningTier.FIFTH_TIER)),
                        62.5),

                Arguments.of(new Amount(4_000),
                        List.of(
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty()),
                        0)
        );
    }

}
