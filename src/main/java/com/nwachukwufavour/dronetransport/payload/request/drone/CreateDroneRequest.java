package com.nwachukwufavour.dronetransport.payload.request.drone;

import com.nwachukwufavour.dronetransport.model.Drone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDroneRequest {

    @NotEmpty
    private List<@Valid Drone> drones;
}
