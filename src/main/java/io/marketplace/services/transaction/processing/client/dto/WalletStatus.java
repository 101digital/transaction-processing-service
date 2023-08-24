package io.marketplace.services.transaction.processing.client.dto;

public enum WalletStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BLOCKED("BLOCKED"),
    EXPIRED("EXPIRED"),
    LOCKED("LOCKED"),
    DEBIT_LOCKED("DEBIT_LOCKED"),
    TXN_SYNCING("TXN_SYNCING"),
    MATURATED("MATURATED"),
    TERMINATING("TERMINATING"),
    TERMINATED("TERMINATED"),
    CLOSED("CLOSED");
	
    private final String text;

    /**
     * @param text
     */
    WalletStatus(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}