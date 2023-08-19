package com.nwachukwufavour.dronetransport.payload.request.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMedicationToDroneRequest {

    @NotEmpty
    private List<String> medicationCodes;
}
