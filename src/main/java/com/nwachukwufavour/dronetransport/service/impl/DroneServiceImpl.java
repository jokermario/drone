package com.nwachukwufavour.dronetransport.service.impl;

import com.nwachukwufavour.dronetransport.enums.DroneModel;
import com.nwachukwufavour.dronetransport.enums.DroneState;
import com.nwachukwufavour.dronetransport.exception.DroneException;
import com.nwachukwufavour.dronetransport.exception.MedicationException;
import com.nwachukwufavour.dronetransport.exception.ResourceNotFound;
import com.nwachukwufavour.dronetransport.model.Drone;
import com.nwachukwufavour.dronetransport.model.Medication;
import com.nwachukwufavour.dronetransport.model.ResponseData;
import com.nwachukwufavour.dronetransport.payload.request.drone.CreateDroneRequest;
import com.nwachukwufavour.dronetransport.payload.request.medication.AddMedicationToDroneRequest;
import com.nwachukwufavour.dronetransport.repository.DroneRepository;
import com.nwachukwufavour.dronetransport.service.DroneService;
import com.nwachukwufavour.dronetransport.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DroneServiceImpl implements DroneService {

    private static final Logger logger = LogManager.getLogger(DroneServiceImpl.class);

    private final DroneRepository droneRepository;
    private final MedicationService medicationService;
//    private final DroneService droneService;

    @Override
    public Flux<Drone> saveAll(Flux<Drone> drones) {
        return droneRepository.saveAll(drones);
    }

    @Override
    public Flux<Drone> findAll() {
        return droneRepository.findAll();
    }

    @Override
    public Mono<Drone> findBySerialNo(String serialNo) {
        return droneRepository.findBySerialNo(serialNo);
    }

    @Override
    public Flux<Drone> findByState(String state) {
        return droneRepository.findByState(state);
    }

    @Override
    public Mono<Integer> getDroneBatteryInformation(String serialNo) {
        return findBySerialNo(serialNo)
                .flatMap(drone -> Mono.just(drone.getBatteryCapacity()));
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> addMedicationToDrone(String serialNo,
                                                                   AddMedicationToDroneRequest addMedicationToDroneRequest) {
        List<Medication> medications = new ArrayList<>();
        List<Integer> mountedLoadOnTheDrone = new ArrayList<>();
        List<String> medicationCodes = new ArrayList<>(addMedicationToDroneRequest.getMedicationCodes());
        return Flux.fromIterable(medicationCodes)
                .flatMap(medicationService::findByCode)
                .switchIfEmpty(Mono.error(() -> new ResourceNotFound(String.format("Medication with code: %s does " +
                        "not exist", medicationCodes))))
                .flatMap(medication -> {
                    mountedLoadOnTheDrone.add(medication.getWeight());
                    if (medication.getDroneSerialNo() != null) {
                        return Mono.error(() -> new MedicationException(String.format("Medication with code %s is " +
                                "already " +
                                "loaded to a drone", medication.getCode())));
                    }
                    medication.setUpdatedAt(LocalDateTime.now());
                    return findBySerialNo(serialNo)
                            .switchIfEmpty(Mono.error(() -> new ResourceNotFound(String.format("Drone with " +
                                    "serialNo: %s does not exist", serialNo))))
                            .map(drone -> {
                                if (!drone.getState().name().equals(DroneState.IDLE.name())) {
                                    throw new DroneException(String.format("Medication can not be loaded to this " +
                                                    "drone state is: %s and not IDLE",
                                            drone.getState()));
                                }

                                if (drone.getBatteryCapacity() < 25) {
                                    throw new DroneException(String.format("Battery level: %s%%. " +
                                                    "This drone is low on" +
                                                    " battery power and needs to be plugged into a power supply.",
                                            drone.getBatteryCapacity()));
                                }

                                int sum = mountedLoadOnTheDrone.stream().reduce(0, Integer::sum);

                                if (sum > drone.getWeightLimit()) {
                                    throw new DroneException(String.format("This drone can only " +
                                                    "carry a maximum of " +
                                                    "%sgr. You have %sgr in excess at the moment",
                                            drone.getWeightLimit(),
                                            (sum - drone.getWeightLimit())));
                                }

                                medication.setDroneSerialNo(drone.getSerialNo());
                                medications.add(medication);
                                return medication;
                            });
                })
                .flatMap(medication -> medicationService.saveAll(Flux.fromIterable(medications)))
                .collectList()
                .map(medicationList -> {
                    if (!medicationList.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("medications", medications);
                        var res = new ResponseData(HttpStatus.OK.value() + " " + HttpStatus.OK.name(), "Medication(s)" +
                                " added successfully", data);
                        return ResponseEntity.status(HttpStatus.OK.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.BAD_REQUEST.name(), "Error occurred while trying to add" +
                                " medications to drone");
                        return ResponseEntity.badRequest().body(res);
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> createDrones(CreateDroneRequest createDroneRequest) {
        List<Drone> drones = new ArrayList<>();
        List<Drone> droneInfo = new ArrayList<>(createDroneRequest.getDrones());
        return Flux.fromIterable(droneInfo)
                .flatMap(drone -> {
                    var serialNo = Optional.ofNullable(drone.getSerialNo()).orElse(UUID.randomUUID().toString());

                    //if not specified in the request, drone state is IDLE
                    var state =
                            Optional.ofNullable(findByStateName(drone.getState().name().toUpperCase(Locale.ROOT)))
                                    .orElse(DroneState.IDLE);

                    var batteryCapacity = Optional.of(drone.getBatteryCapacity())
                            .orElse(100);

                    var droneWeight = drone.getWeightLimit();

                    drone.setSerialNo(serialNo);
                    drone.setState(state);
                    drone.setBatteryCapacity(batteryCapacity);
                    drone.setWeightLimit(droneWeight);
                    drone.setCreatedAt(LocalDateTime.now());
                    drone.setUpdatedAt(LocalDateTime.now());

                    //Model is detected dynamically. It depends on the weight specified
                    if (droneWeight >= 50 && droneWeight <= 150) {
                        drone.setModel(DroneModel.LIGHTWEIGHT);
                    } else if (droneWeight >= 151 && droneWeight <= 251) {
                        drone.setModel(DroneModel.MIDDLEWEIGHT);
                    } else if (droneWeight >= 251 && droneWeight <= 352) {
                        drone.setModel(DroneModel.CRUISERWEIGHT);
                    } else if (droneWeight >= 353 && droneWeight <= 500) {
                        drone.setModel(DroneModel.HEAVYWEIGHT);
                    }

                    drones.add(drone);
                    return Mono.just(drone);
                })
                .flatMap(drone -> droneRepository.saveAll(drones))
                .collectList()
                .map(droneList -> {
                    if (!droneList.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("drones", drones);
                        var res = new ResponseData(HttpStatus.CREATED.value() + " " + HttpStatus.CREATED.name(),
                                "Drone(s) created successfully", data);
                        return ResponseEntity.status(HttpStatus.CREATED.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.BAD_REQUEST.name(), "Error occurred while trying to " +
                                "create drone(s)");
                        return ResponseEntity.badRequest().body(res);
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> getDrones() {
        return findAll().collectList().map(droneList -> {
            if (!droneList.isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("drones", droneList);
                var res = new ResponseData(HttpStatus.OK.value() + " " + HttpStatus.OK.name(), "Drone(s) retrieved " +
                        "successfully", data);
                return ResponseEntity.status(HttpStatus.OK.value()).body(res);
            } else {
                var res = new ResponseData(HttpStatus.NOT_FOUND.name(), "Drone(s) do not exist at this time");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }
        });
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> checkLoadedMedicationForDrone(String serialNo) {
        return medicationService.findByDroneSerialNo(serialNo)
                .collectList()
                .map(medicationList -> {
                    if (!medicationList.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("drones", medicationList);
                        var res = new ResponseData(HttpStatus.OK.value() + " " + HttpStatus.OK.name(), "Medication(s)" +
                                " retrieved successfully", data);
                        return ResponseEntity.status(HttpStatus.OK.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.NOT_FOUND.name(), "Medications do not exist at this " +
                                "time.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> getAvailableDrones() {
        return findByState(DroneState.IDLE.name())
                .collectList()
                .map(droneList -> {
                    if (!droneList.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("drones", droneList);
                        var res = new ResponseData(HttpStatus.OK.value() + " " + HttpStatus.OK.name(), "Available " +
                                "drones retrieved successfully", data);
                        return ResponseEntity.status(HttpStatus.OK.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.NOT_FOUND.name(), "No available drone at this time.");
                        return ResponseEntity.badRequest().body(res);
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<ResponseData>> checkDroneBatteryInformation(String serialNo) {
        return getDroneBatteryInformation(serialNo)
                .map(batteryInfo -> {
                    if (batteryInfo != null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("battery_level", batteryInfo);
                        var res = new ResponseData(HttpStatus.OK.value() + " " + HttpStatus.OK.name(), "Battery " +
                                "Information retrieved successfully", data);
                        return ResponseEntity.status(HttpStatus.OK.value()).body(res);
                    } else {
                        var res = new ResponseData(HttpStatus.NOT_FOUND.name(), "Battery information do not exist at " +
                                "this time.");
                        return ResponseEntity.badRequest().body(res);
                    }
                });
    }

    private DroneState findByStateName(String name) {
        DroneState result = null;
        for (DroneState droneState : DroneState.values()) {
            if (droneState.name().equalsIgnoreCase(name)) {
                result = droneState;
                break;
            }
        }
        return result;
    }
}
