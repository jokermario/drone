package com.nwachukwufavour.dronetransport.payload.request.medication;

import com.nwachukwufavour.dronetransport.model.Medication;
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
public class CreateMedicationRequest {
    @NotEmpty
    private List<@Valid Medication> medications;
}
