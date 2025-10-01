package com.vicastro.walletservice.application.dto;

public record CreateWalletOutput(String walletId) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String walletId;

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public CreateWalletOutput build() {
            return new CreateWalletOutput(walletId);
        }
    }
}
