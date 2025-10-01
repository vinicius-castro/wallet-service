package com.vicastro.walletservice.domain;

public record Wallet(String code,
                     String userId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private String userId;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Wallet build() {
            return new Wallet(code, userId);
        }
    }
}
