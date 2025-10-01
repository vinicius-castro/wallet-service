package com.vicastro.walletservice.domain;

import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;

import java.time.LocalDateTime;

public record Transaction(String id,
                          String walletId,
                          Operation operation,
                          Origin origin,
                          Long valueInCents,
                          LocalDateTime createdAt
) { }
