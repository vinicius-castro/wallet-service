package com.vicastro.walletservice.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record WalletBalance(String walletId, Long balance, OffsetDateTime referenceDate) implements Serializable {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String walletId;
        private Long balance;
        private OffsetDateTime referenceDate;

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder balance(Long balance) {
            this.balance = balance;
            return this;
        }

        public Builder referenceDate(OffsetDateTime referenceDate) {
            this.referenceDate = referenceDate;
            return this;
        }

        public WalletBalance build() {
            return new WalletBalance(walletId, balance, referenceDate);
        }
    }
}
