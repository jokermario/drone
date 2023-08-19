package com.nwachukwufavour.dronetransport.repository;

import com.nwachukwufavour.dronetransport.model.Medication;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MedicationRepository extends ReactiveCrudRepository<Medication, Integer> {
    Flux<Medication> findByDroneSerialNo(String droneId);

    Mono<Medication> findByCode(String medicationCode);

    @Query("SELECT * FROM medications INNER JOIN drones ON medications.drone_serial_no = drones.serial_no WHERE " +
            "medications.drone_serial_no = $1")
    Flux<Medication> findLoadedMedication(String serialNo);
}
