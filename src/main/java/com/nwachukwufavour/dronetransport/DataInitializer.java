package com.nwachukwufavour.dronetransport;

import com.nwachukwufavour.dronetransport.enums.DroneModel;
import com.nwachukwufavour.dronetransport.enums.DroneState;
import com.nwachukwufavour.dronetransport.model.Drone;
import com.nwachukwufavour.dronetransport.model.Medication;
import com.nwachukwufavour.dronetransport.model.User;
import com.nwachukwufavour.dronetransport.repository.DroneRepository;
import com.nwachukwufavour.dronetransport.repository.MedicationRepository;
import com.nwachukwufavour.dronetransport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;

    private static final Logger logger = LogManager.getLogger(DataInitializer.class);

    @EventListener(value = ApplicationReadyEvent.class)
    public void init() {
        logger.info("start data initialization...");


        var initUsers = this.users.deleteAll()
                .thenMany(
                        Flux.just("user")
                                .flatMap(username -> {
                                    List<String> roles = List.of("ROLE_USER");

                                    User user = User.builder()
                                            .roles(Collections.singletonList(String.valueOf(roles)))
                                            .username(username)
                                            .password(passwordEncoder.encode("password"))
                                            .build();

                                    return this.users.save(user);
                                })
                );

        List<Drone> drones = new ArrayList<>();
        var drone1 = Drone.builder()
                .serialNo("qwertyuiop")
                .weightLimit(270)
                .batteryCapacity(100)
                .model(DroneModel.CRUISERWEIGHT)
                .state(DroneState.LOADING)
                .build();
        var drone2 = Drone.builder()
                .serialNo("qwertiop")
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(350)
                .batteryCapacity(100)
                .state(DroneState.IDLE)
                .build();
        var drone3 = Drone.builder()
                .serialNo("qwertiop1")
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(350)
                .batteryCapacity(100)
                .state(DroneState.LOADED)
                .build();
        var drone4 = Drone.builder()
                .serialNo("qwertiop2")
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(350)
                .batteryCapacity(100)
                .state(DroneState.RETURNING)
                .build();
        var drone5 = Drone.builder()
                .serialNo("qwertiop3")
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(350)
                .batteryCapacity(100)
                .state(DroneState.DELIVERED)
                .build();
        var drone6 = Drone.builder()
                .serialNo("qwertiop5")
                .model(DroneModel.CRUISERWEIGHT)
                .weightLimit(350)
                .batteryCapacity(100)
                .state(DroneState.DELIVERING)
                .build();
        drones.add(drone1);
        drones.add(drone2);
        drones.add(drone3);
        drones.add(drone4);
        drones.add(drone5);
        drones.add(drone6);

        List<Medication> medications = new ArrayList<>();
        var med1 = Medication.builder()
                .name("med1")
                .weight(150)
                .code("AS291__")
                .image("linktoimage")
                .build();
        var med2 = Medication.builder()
                .name("med2")
                .weight(100)
                .code("AS261__")
                .image("linktoimage")
                .build();
        var med3 = Medication.builder()
                .name("med3")
                .weight(340)
                .code("AS231__")
                .image("linktoimage")
                .build();
        medications.add(med1);
        medications.add(med2);
        medications.add(med3);

        var initDrones = this.droneRepository.deleteAll()
                        .thenMany(Flux.just(drones)
                                        .flatMap(droneList -> this.droneRepository.saveAll(drones)));

        var initMedications = this.medicationRepository.deleteAll()
                .thenMany(Flux.just(medications)
                        .flatMap(droneList -> this.medicationRepository.saveAll(medications)));

        initUsers.doOnSubscribe(data -> logger.info("data:" + data))
                .thenMany(initDrones)
                .thenMany(initMedications)
                .subscribe(
                        data -> logger.info("data:" + data), err -> logger.error("error:" + err),
                        () -> logger.info("done initialization...")
                );
    }
}
