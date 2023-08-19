package com.nwachukwufavour.dronetransport.model;

import com.nwachukwufavour.dronetransport.enums.DroneModel;
import com.nwachukwufavour.dronetransport.enums.DroneState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("drones")
@Builder
public class Drone {

    @Id
    private Integer id;

    @Column("serial_no")
    @Size(max = 100)
    private String serialNo;

    @Column
    private DroneModel model;

    @Column("weight_limit")
    @NotNull
    @Max(500)
    @Min(50)
    private int weightLimit;

    @Column("battery_capacity")
    @NotNull
    @Max(100)
    @Min(0)
    private int batteryCapacity;

    @Column
    private DroneState state;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("created_by")
    @CreatedBy
    private String createdBy;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column("updated_by")
    @LastModifiedBy
    private String updatedBy;
}
