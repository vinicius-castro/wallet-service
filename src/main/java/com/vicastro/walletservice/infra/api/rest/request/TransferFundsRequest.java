package com.vicastro.walletservice.infra.api.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransferFundsRequest(
        @JsonProperty("from_wallet_id")
        String fromWalletId,
        @JsonProperty("to_wallet_id")
        String toWalletId,
        @JsonProperty("amount")
        Long amount) { }
