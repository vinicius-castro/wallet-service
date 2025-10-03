package com.vicastro.walletservice.infra.api.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record WalletBalanceResponse(@JsonProperty("wallet_id") String walletId, BigDecimal balance) {

    @Override
    public BigDecimal balance() {
        return balance.divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
    }
}
