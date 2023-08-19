package com.nwachukwufavour.dronetransport.controller;

import com.nwachukwufavour.dronetransport.exception.MedicationException;
import com.nwachukwufavour.dronetransport.model.ResponseData;
import com.nwachukwufavour.dronetransport.payload.request.drone.CreateDroneRequest;
import com.nwachukwufavour.dronetransport.payload.request.medication.AddMedicationToDroneRequest;
import com.nwachukwufavour.dronetransport.service.DroneService;
import com.nwachukwufavour.dronetransport.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drone")
public class DroneController {

    private static final Logger logger = LogManager.getLogger(DroneController.class);

    private final DroneService droneService;
    private final MedicationService medicationService;

    @PostMapping
    public Mono<ResponseEntity<ResponseData>> createDrones(@Valid @RequestBody CreateDroneRequest createDroneRequest) {

        return droneService.createDrones(createDroneRequest);
    }

    @GetMapping
    public Mono<ResponseEntity<ResponseData>> getDrones() {
        return droneService.getDrones();
    }

    @PutMapping("/{serialNo}/medication")
    public Mono<ResponseEntity<ResponseData>> addMedicationToDrone(@PathVariable String serialNo,
                                                                 @Valid @RequestBody AddMedicationToDroneRequest addMedicationToDroneRequest) throws MedicationException {

            return droneService.addMedicationToDrone(serialNo, addMedicationToDroneRequest);

    }

    @GetMapping("/{serialNo}/medication")
    public Mono<ResponseEntity<ResponseData>> checkLoadedMedicationForDrone(@PathVariable String serialNo) {
        return droneService.checkLoadedMedicationForDrone(serialNo);
    }

    @GetMapping("/available")
    public Mono<ResponseEntity<ResponseData>> getAvailableDrones() {
        return droneService.getAvailableDrones();
    }

    @GetMapping("/{serialNo}/batteryInfo")
    public Mono<ResponseEntity<ResponseData>> checkDroneBatteryInformation(@PathVariable String serialNo) {
        return droneService.checkDroneBatteryInformation(serialNo);
    }

}
