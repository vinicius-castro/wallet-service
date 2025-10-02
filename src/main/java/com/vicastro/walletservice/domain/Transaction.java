package com.vicastro.walletservice.domain;

import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;

public record Transaction(String id,
                          String walletId,
                          String walletIdRelated,
                          Operation operation,
                          Origin origin,
                          Long valueInCents
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String walletId;
        private String walletIdRelated;
        private Operation operation;
        private Origin origin;
        private Long valueInCents;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder walletIdRelated(String walletIdRelated) {
            this.walletIdRelated = walletIdRelated;
            return this;
        }

        public Builder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder origin(Origin origin) {
            this.origin = origin;
            return this;
        }

        public Builder valueInCents(Long valueInCents) {
            this.valueInCents = valueInCents;
            return this;
        }

        public Transaction build() {
            return new Transaction(id, walletId, walletIdRelated, operation, origin, valueInCents);
        }
    }
}
