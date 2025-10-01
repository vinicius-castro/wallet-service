package com.vicastro.walletservice.application.dto;

public record CreateWalletInput(String userId) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public CreateWalletInput build() {
            return new CreateWalletInput(userId);
        }
    }
}
