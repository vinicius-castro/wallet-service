package com.vicastro.walletservice.infra.api.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateWalletResponse(@JsonProperty("wallet_id") String walletId) { }
