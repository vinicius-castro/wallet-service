package com.vicastro.walletservice.domain;

import java.io.Serializable;

public record WalletBalance(String walletId, Long balance) implements Serializable {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String walletId;
        private Long balance;

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder balance(Long balance) {
            this.balance = balance;
            return this;
        }

        public WalletBalance build() {
            return new WalletBalance(walletId, balance);
        }
    }
}
