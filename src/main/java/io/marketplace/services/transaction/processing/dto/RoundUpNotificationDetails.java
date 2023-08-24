package io.marketplace.services.transaction.processing.dto;


import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoundUpNotificationDetails {
	
	private String userId;
	private String savingsPotName;
	private String currency;
	private BigDecimal amount;
	private String templateName;
	

}
