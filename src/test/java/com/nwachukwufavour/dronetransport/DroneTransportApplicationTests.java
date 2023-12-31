package com.nwachukwufavour.dronetransport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nwachukwufavour.dronetransport.enums.DroneState;
import com.nwachukwufavour.dronetransport.model.Drone;
import com.nwachukwufavour.dronetransport.model.Medication;
import com.nwachukwufavour.dronetransport.model.User;
import com.nwachukwufavour.dronetransport.payload.request.auth.AuthenticationRequest;
import com.nwachukwufavour.dronetransport.payload.request.drone.CreateDroneRequest;
import com.nwachukwufavour.dronetransport.payload.request.medication.AddMedicationToDroneRequest;
import com.nwachukwufavour.dronetransport.payload.request.medication.CreateMedicationRequest;
import com.nwachukwufavour.dronetransport.repository.DroneRepository;
import com.nwachukwufavour.dronetransport.repository.MedicationRepository;
import com.nwachukwufavour.dronetransport.repository.UserRepository;
import com.nwachukwufavour.dronetransport.service.DroneService;
import com.nwachukwufavour.dronetransport.service.MedicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class DroneTransportApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private DroneService droneService;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String access_token;

    @BeforeEach
    public void setup() {
//        List<String> statements = new ArrayList<>();
//        String dropUsersTable = "DROP TABLE IF EXISTS users;";
//        String dropDroneTable = "DROP TABLE IF EXISTS medication;";
//        String dropMedicationTable = "DROP TABLE IF EXISTS drone;";
//        String setTimeZone = "SET TIME ZONE 'Africa/Lagos';";
//        String createUsersTable = "CREATE TABLE users ( id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, username" +
//                " VARCHAR(100) NOT NULL, password VARCHAR(100) NOT NULL, active BOOLEAN NOT NULL, roles VARCHAR(100) " +
//                "NOT NULL );";
//        String createDroneTable = "CREATE TABLE drones ( id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
//                "serial_no VARCHAR(100) NOT NULL, model VARCHAR(13) NOT NULL, weight_limit INT NOT NULL, " +
//                "battery_capacity INTEGER NOT NULL, state VARCHAR(10) NOT NULL, created_at TIMESTAMP, updated_at " +
//                "TIMESTAMP, created_by VARCHAR(100), updated_by VARCHAR(100), CONSTRAINT serial_no_unique UNIQUE " +
//                "(serial_no) );";
//        String createMedicationTable = "CREATE TABLE medications (\n" +
//                "    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
//                "    name VARCHAR(100) NOT NULL,\n" +
//                "    weight INT NOT NULL,\n" +
//                "    code VARCHAR(100) NOT NULL,\n" +
//                "    image VARCHAR(500) NOT NULL,\n" +
//                "    drone_serial_no VARCHAR(100),\n" +
//                "    created_at TIMESTAMP,\n" +
//                "    updated_at TIMESTAMP,\n" +
//                "    created_by VARCHAR(100),\n" +
//                "    updated_by VARCHAR(100),\n" +
//                "    CONSTRAINT fk_drone_serial_no\n" +
//                "        FOREIGN KEY (drone_serial_no)\n" +
//                "            REFERENCES drones(serial_no),\n" +
//                "    CONSTRAINT code_unique UNIQUE (code)\n" +
//                ");";
//
//        statements.add(dropUsersTable);
//        statements.add(dropDroneTable);
//        statements.add(dropMedicationTable);
//        statements.add(setTimeZone);
//        statements.add(createUsersTable);
//        statements.add(createDroneTable);
//        statements.add(createMedicationTable);
//
//        statements.forEach(it -> databaseClient.sql(it)
//                .fetch()
//                .rowsUpdated()
//                .block()
//        );

        //create user in db
        this.userRepository.deleteAll()
                .thenMany(
                        Flux.just("user")
                                .flatMap(username -> {
                                    List<String> roles = List.of("ROLE_USER");

                                    User user = User.builder()
                                            .roles(Collections.singletonList(String.valueOf(roles)))
                                            .username(username)
                                            .password(passwordEncoder.encode("password"))
                                            .build();

                                    return this.userRepository.save(user);
                                })
                ).blockLast();

        this.droneRepository.deleteAll();
        this.medicationRepository.deleteAll();
    }

    @Test
    public void authenticate() throws JsonProcessingException {

        var authenticationRequest = AuthenticationRequest.builder()
                .username("user")
                .password("password")
                .build();

        var p  = webTestClient.post().uri("/auth/v1/token")
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token").isNotEmpty()
                .returnResult();

        this.access_token = new ObjectMapper().readTree(new String(Objects.requireNonNull(p.getResponseBody()), StandardCharsets.UTF_8)).at("/access_token").toString().replaceAll("^\"|\"$", "");
    }

    @Test
    public void createDrone() throws JsonProcessingException {
        authenticate();
        List<Drone> drones = new ArrayList<>();
        var drone1 = Drone.builder()
                .weightLimit(270)
                .batteryCapacity(79)
                .state(DroneState.LOADING)
                .build();
        var drone2 = Drone.builder()
                .weightLimit(350)
                .batteryCapacity(79)
                .state(DroneState.IDLE)
                .build();
        drones.add(drone1);
        drones.add(drone2);

        var createDroneRequest = CreateDroneRequest.builder().drones(drones).build();

        webTestClient.post().uri("/api/v1/drone")
                .header("Authorization", "Bearer "+access_token)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(createDroneRequest), CreateDroneRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("201 CREATED");
    }

    @Test
    public void createMedication() throws JsonProcessingException {
        authenticate();
        List<Medication> medicationList = new ArrayList<>();
        var med1 = Medication.builder()
                .id(1)
                .name("med11")
                .weight(150)
                .code("4D847137_F04A_494E_BE10_1D06950D2174")
                .image("linktoimage")
                .build();
        var med2 = Medication.builder()
                .id(2)
                .name("med12")
                .weight(100)
                .code("2B135FAB_3A9B_493A_B30D_E353CB75160E")
                .image("linktoimage")
                .build();
        var med3 = Medication.builder()
                .id(3)
                .name("med13")
                .weight(340)
                .code("E7F7AA6E_42EA_4F14_B5B6_3FD2D49EF2EC")
                .image("linktoimage")
                .build();
        medicationList.add(med1);
        medicationList.add(med2);
        medicationList.add(med3);

        var createMedicationRequest = CreateMedicationRequest.builder()
                .medications(medicationList)
                .build();

        webTestClient.post().uri("/api/v1/medication")
                .header("Authorization", "Bearer "+access_token)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(createMedicationRequest), CreateMedicationRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("201 CREATED");
    }

    @Test
    public void getDrones() throws JsonProcessingException {
        createDrone();
        webTestClient.get().uri("/api/v1/drone")
                .header("Authorization", "Bearer "+access_token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("200 OK");
    }

    @Test
    public void addMedicationToDrone() throws JsonProcessingException {
        createDrone();
        createMedication();

        var addMedicationToDroneRequest = AddMedicationToDroneRequest.builder()
                .medicationCodes(Arrays.asList("4D847137_F04A_494E_BE10_1D06950D2174", "2B135FAB_3A9B_493A_B30D_E353CB75160E"))
                .build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path("/api/v1/drone/{serialNo}/medication").build("qwertiop"))
                .header("Authorization", "Bearer "+access_token)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(addMedicationToDroneRequest), AddMedicationToDroneRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("200 OK");

    }

    @Test
    public void checkLoadedMedicationForDrone() throws JsonProcessingException {
        addMedicationToDrone();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/drone/{serialNo}/medication").build("qwertiop"))
                .header("Authorization", "Bearer "+access_token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("200 OK");
    }

    @Test
    public void getAvailableDrones() throws JsonProcessingException {
        createDrone();

        webTestClient.get().uri("/api/v1/drone/available")
                .header("Authorization", "Bearer "+access_token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("200 OK");
    }

    @Test
    public void checkDroneBatteryInformation() throws JsonProcessingException {
        createDrone();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/drone/{serialNo}/batteryInfo").build("qwertyuiop"))
                .header("Authorization", "Bearer "+access_token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isNotEmpty()
                .jsonPath("$.status").isEqualTo("200 OK");
    }
}
