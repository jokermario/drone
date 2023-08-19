package com.nwachukwufavour.dronetransport.service;

import com.nwachukwufavour.dronetransport.model.Drone;
import com.nwachukwufavour.dronetransport.model.ResponseData;
import com.nwachukwufavour.dronetransport.payload.request.drone.CreateDroneRequest;
import com.nwachukwufavour.dronetransport.payload.request.medication.AddMedicationToDroneRequest;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DroneService {
    Flux<Drone> saveAll(Flux<Drone> drones);
    Flux<Drone> findAll();
    Mono<Drone> findBySerialNo(String serialNo);
    Flux<Drone> findByState(String state);
    Mono<Integer> getDroneBatteryInformation(String serialNo);
    Mono<ResponseEntity<ResponseData>> addMedicationToDrone(String serialNo, AddMedicationToDroneRequest addMedicationToDroneRequest);
    Mono<ResponseEntity<ResponseData>> createDrones(CreateDroneRequest createDroneRequest);
    Mono<ResponseEntity<ResponseData>> getDrones();
    Mono<ResponseEntity<ResponseData>> checkLoadedMedicationForDrone(String serialNo);
    Mono<ResponseEntity<ResponseData>> getAvailableDrones();
    Mono<ResponseEntity<ResponseData>> checkDroneBatteryInformation(String serialNo);
}
