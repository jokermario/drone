package com.nwachukwufavour.dronetransport.service.impl;

import com.nwachukwufavour.dronetransport.model.Medication;
import com.nwachukwufavour.dronetransport.model.ResponseData;
import com.nwachukwufavour.dronetransport.payload.request.medication.CreateMedicationRequest;
import com.nwachukwufavour.dronetransport.repository.MedicationRepository;
import com.nwachukwufavour.dronetransport.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;

    @Override
    public Mono<Medication> findByCode(String medicationCode) {
        return medicationRepository.findByCode(medicationCode);
    }

    @Override
    public Flux<Medication> findAll() {
        return medicationRepository.findAll();
    }

    @Override
    public Flux<Medication> saveAll(Flux<Medication> medications) {
        return medicationRepository.saveAll(medications);
    }

    @Override
    public Mono<Medication> save(Medication medication) {
        return medicationRepository.save(medication);
    }

    @Override
    public Flux<Medication> findByDroneSerialNo(String serialNo) {
        return medicationRepository.findByDroneSerialNo(serialNo);
    }

    @Override
    public Flux<Medication> findLoadedMedication(String serialNo) {
        return medicationRepository.findLoadedMedication(serialNo);
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> createMedications(CreateMedicationRequest createMedicationRequest) {
        List<Medication> medications = new ArrayList<>();
        List<Medication> medicationList = new ArrayList<>(createMedicationRequest.getMedications());
        return Flux.fromIterable(medicationList)
                .flatMap(medication -> {

                    var code = Optional.ofNullable(medication.getCode()).orElse(UUID.randomUUID().toString());

                    medication.setCode(code);
                    medication.setCreatedAt(LocalDateTime.now());
                    medication.setUpdatedAt(LocalDateTime.now());

                    medications.add(medication);
                    return Mono.just(medication);
                })
                .flatMap(medication -> medicationRepository.saveAll(medications))
                .collectList()
                .map(medicationList1 -> {
                    if (!medicationList1.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("drones", medications);
                        var res = new ResponseData(HttpStatus.CREATED.value()+" "+HttpStatus.CREATED.name(), "Drone(s) created successfully", data);
                        return ResponseEntity.status(HttpStatus.CREATED.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.BAD_REQUEST.name(), "Error occurred while trying to create drone(s)");
                        return ResponseEntity.badRequest().body(res);
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> getMedications() {
        return medicationRepository.findAll()
                .collectList()
                .map(medicationList -> {
                    if (!medicationList.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("drones", medicationList);
                        var res = new ResponseData(HttpStatus.OK.value()+" "+HttpStatus.OK.name(), "Drone(s) created successfully", data);
                        return ResponseEntity.status(HttpStatus.OK.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.NOT_FOUND.name(), "No medication(s) at this moment");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                    }
                });
    }
}
