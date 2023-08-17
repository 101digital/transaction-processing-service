package io.marketplace.services.transaction.processing.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "configuration")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ConfigurationEntity {

    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "wallet", nullable = false)
    private String wallet;
    
    @Column(name = "logic_code", nullable = false)
    private String logicCode;
    
    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "send_notification", nullable = false)
    private boolean sendNotification;

}
