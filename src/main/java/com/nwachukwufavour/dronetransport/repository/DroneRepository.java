package com.nwachukwufavour.dronetransport.repository;

import com.nwachukwufavour.dronetransport.model.Drone;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DroneRepository extends ReactiveCrudRepository<Drone, Integer> {
    Mono<Drone> findBySerialNo(String serialNo);
    Flux<Drone> findByState(String state);
}
