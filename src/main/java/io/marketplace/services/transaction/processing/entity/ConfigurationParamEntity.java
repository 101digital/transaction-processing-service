package io.marketplace.services.transaction.processing.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "configuration_param")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigurationParamEntity {

	@Id
	@Column(name = "id")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "configurationId", insertable = false, updatable = false)
	private ConfigurationEntity configuration;

	private UUID configurationId;

	@Column(name = "param_name")
	private String paramName;

	@Column(name = "value")
	private String value;
}
