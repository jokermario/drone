package com.nwachukwufavour.dronetransport.model;

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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("medications")
@Builder
public class Medication {

    @Id
    private Integer id;

    @Pattern(regexp = "^[A-Za-z0-9_-]*[A-Za-z0-9][A-Za-z0-9_-]*$")
    @Column
    private String name;

    @NotNull
    @Column
    @Min(0)
    private int weight;

    @Pattern(regexp = "^[A-Z0-9_]*[A-Z0-9][A-Z0-9_]*$")
    @Column
    private String code;

    @NotBlank
    @Column
    private String image;

    @Column("drone_serial_no")
    @Size(max = 100)
    private String droneSerialNo;

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
