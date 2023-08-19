package com.nwachukwufavour.dronetransport.service;

import com.nwachukwufavour.dronetransport.model.Medication;
import com.nwachukwufavour.dronetransport.model.ResponseData;
import com.nwachukwufavour.dronetransport.payload.request.medication.CreateMedicationRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MedicationService {
    Mono<Medication> findByCode(String medicationCode);
    Flux<Medication> findAll();
    Flux<Medication> saveAll(Flux<Medication> medications);
    Mono<Medication> save(Medication medication);
    Flux<Medication> findByDroneSerialNo(String serialNo);
    Flux<Medication> findLoadedMedication(String serialNo);
    Mono<ResponseEntity<ResponseData>> createMedications(CreateMedicationRequest createMedicationRequest);
    Mono<ResponseEntity<ResponseData>> getMedications();
}
