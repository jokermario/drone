package com.nwachukwufavour.dronetransport.controller;

import com.nwachukwufavour.dronetransport.model.ResponseData;
import com.nwachukwufavour.dronetransport.payload.request.medication.CreateMedicationRequest;
import com.nwachukwufavour.dronetransport.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medication")
public class MedicationController {

    private final MedicationService medicationService;

    @PostMapping
    public Mono<ResponseEntity<ResponseData>> createMedications(@Valid @RequestBody CreateMedicationRequest createMedicationRequest){

        return medicationService.createMedications(createMedicationRequest);
    }

    @GetMapping
    public Mono<ResponseEntity<ResponseData>> getMedications(){

       return medicationService.getMedications();
    }
}
