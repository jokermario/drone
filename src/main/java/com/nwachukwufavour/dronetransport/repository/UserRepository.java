package com.nwachukwufavour.dronetransport.repository;

import com.nwachukwufavour.dronetransport.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Integer> {

    Mono<User> findByUsername(String username);
    Mono<User> findById(Integer id);
}
