package com.nwachukwufavour.dronetransport.scheduler;

import com.nwachukwufavour.dronetransport.model.Drone;
import com.nwachukwufavour.dronetransport.service.DroneService;
import com.nwachukwufavour.dronetransport.utility.LogUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class DroneScheduler {

    private static final Logger logger = LogManager.getLogger(DroneScheduler.class);

    private final DroneService droneService;

    @Scheduled(fixedDelay = 90000, initialDelay = 500)
    public Mono<Void> batteryDrainingCriteria(){
        List<Drone> drones = new ArrayList<>();
        droneService.findAll()
                .flatMap(drone -> {
                    var battCap = drone.getBatteryCapacity();
                    switch (drone.getState()){
                        case IDLE:
                            //battery drops by 5% every 30 seconds
                            double batteryCapIdle = 0;
                            if (battCap > 24){
                                batteryCapIdle = battCap - (((double) 5 / 100) * battCap);
                            }
                            drone.setBatteryCapacity(Math.max((int) batteryCapIdle, 20));
                            break;
                        case LOADING:
                            //battery drops by 8% every 30 seconds
                            double batteryCapLoading = 0;
                            if (battCap > 24){
                                batteryCapLoading = battCap - (((double) 8 / 100) * battCap);
                            }
                            drone.setBatteryCapacity(Math.max((int) batteryCapLoading, 20));
                            break;
                        case LOADED:
                            System.out.println(drone.getState().name());
                            //battery drops by 10% every 5 minutes
                            double batteryCapLoaded = 0;
                            if (battCap > 24){
                                batteryCapLoaded = battCap - (((double) 10 / 100) * battCap);
                            }
                            drone.setBatteryCapacity(Math.max((int) batteryCapLoaded, 20));
                            break;
                        case DELIVERING:
                            //battery drops by 16% every 30 seconds
                            double batteryCapDelivering = 0;
                            if (battCap > 24) {
                                batteryCapDelivering = battCap - (((double) 16 / 100) * battCap);
                            }
                            drone.setBatteryCapacity(Math.max((int) batteryCapDelivering, 20));
                            break;
                        case DELIVERED:
                            //battery drops by 11% every 30 seconds
                            double batteryCapDelivered = 0;
                            if (battCap > 24){
                                batteryCapDelivered = battCap - (((double) 11 / 100) * battCap);
                            }
                            drone.setBatteryCapacity(Math.max((int) batteryCapDelivered, 20));
                            break;
                        case RETURNING:
                            //battery drops by 15% every 30 seconds
                            double batteryCapReturning = 0;
                            if (battCap > 24){
                                batteryCapReturning = battCap - (((double) 15 / 100) * battCap);
                            }
                            drone.setBatteryCapacity(Math.max((int) batteryCapReturning, 20));
                            break;
                        default:
                            throw new IllegalArgumentException(String.format("Drone state %s: not supported", drone.getState()));
                    }
                    return Mono.just(drone);
                })
                .map(drones::add)
                .subscribe();

        droneService.saveAll(Flux.fromIterable(drones)).subscribe();
        return Mono.empty();
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 500)
    public Mono<Void> checkBatteryLevel(){
        //check battery level every 10 secs
        droneService.findAll()
                .flatMap(drone -> {
                    logger.info(LogUtil.logData(drone.getSerialNo(), drone.getState().name(), drone.getBatteryCapacity()));
                    return Mono.empty();
                }).subscribe();
        return Mono.empty();
    }

}
