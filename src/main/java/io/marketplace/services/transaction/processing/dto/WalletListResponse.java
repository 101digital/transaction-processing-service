package io.marketplace.services.transaction.processing.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletListResponse {
	private List<Wallet> data;
}
