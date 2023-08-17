package io.marketplace.services.transaction.processing.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "configuration_param")
public class ConfigurationParamEntity {

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "configuration_id", referencedColumnName = "id", nullable = false)
	private ConfigurationEntity configurationId;

	@Column(name = "param_name")
	private String paramName;

	@Column(name = "value")
	private String value;
}
