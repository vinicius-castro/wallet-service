package com.vicastro.walletservice.infra.api.rest.response;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record WalletBalanceResponse(String walletId, BigDecimal balance) {

    @Override
    public BigDecimal balance() {
        return balance.divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
    }
}
