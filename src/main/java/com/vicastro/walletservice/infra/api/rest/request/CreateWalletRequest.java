package com.vicastro.walletservice.infra.api.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateWalletRequest(@JsonProperty("user_id") String userId) { }
