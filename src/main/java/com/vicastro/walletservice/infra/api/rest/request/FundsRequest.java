package com.vicastro.walletservice.infra.api.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FundsRequest(@JsonProperty("wallet_id") String walletId, Long amount) { }
