package org.trainbeans.trackwarrants.main.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;
import org.trainbeans.trackwarrants.main.repository.TrackWarrantRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configuration class to initialize sample data in the database.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final TrackWarrantRepository repository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("Initializing sample track warrants...");

            LocalDateTime now = LocalDateTime.now();
            String today = now.format(DateTimeFormatter.ISO_LOCAL_DATE);

            // Create sample warrant 1
            TrackWarrant warrant1 = TrackWarrant.builder()
                .warrantId(today + "-1")
                .warrantNumber(1)
                .warrantDate(today)
                .trainId("BNSF-4523")
                .trainCrew("ENGINEER JONES / CONDUCTOR SMITH")
                .location("CHICAGO UNION STATION")
                .startingLocation("CHICAGO UNION STATION")
                .issuedDateTime(now)
                .status(TrackWarrant.WarrantStatus.ACTIVE)
                .issuedBy("Dispatcher Jones")
                .okTime("0930")
                .dispatcher("JONES")
                .copiedBy("SMITH")
                .line2Instruction("Proceed from CHICAGO UNION STATION to ST. LOUIS TERMINAL on ILLINOIS MAIN LINE Track")
                .line6Instruction("This Authority Expires at " + now.plusHours(8).toLocalTime().toString().substring(0, 5) + "M")
                .line13Instruction("Do not exceed 79 MPH between CHICAGO UNION STATION and ST. LOUIS TERMINAL")
                .build();

            // Create sample warrant 2
            TrackWarrant warrant2 = TrackWarrant.builder()
                .warrantId(today + "-2")
                .warrantNumber(2)
                .warrantDate(today)
                .trainId("UP-7845")
                .trainCrew("ENGINEER DAVIS / CONDUCTOR LEE")
                .location("DENVER YARD")
                .startingLocation("DENVER YARD")
                .issuedDateTime(now.minusHours(2))
                .status(TrackWarrant.WarrantStatus.ACTIVE)
                .issuedBy("Dispatcher Martinez")
                .okTime("0715")
                .dispatcher("MARTINEZ")
                .copiedBy("LEE")
                .line2Instruction("Proceed from DENVER YARD to SALT LAKE CITY on MOUNTAIN SUBDIVISION Track")
                .line6Instruction("This Authority Expires at " + now.minusHours(2).plusHours(10).toLocalTime().toString().substring(0, 5) + "M")
                .line12Instruction("Between DENVER YARD and SALT LAKE CITY Make All Movements at restricted speed and stop short of Men or Machines fouling track.")
                .line13Instruction("Do not exceed 60 MPH between DENVER YARD and SALT LAKE CITY")
                .build();

            // Create sample warrant 3 (completed)
            TrackWarrant warrant3 = TrackWarrant.builder()
                .warrantId(today + "-3")
                .warrantNumber(3)
                .warrantDate(today)
                .trainId("NS-9012")
                .trainCrew("ENGINEER BROWN / CONDUCTOR WILSON")
                .location("PITTSBURGH JUNCTION")
                .startingLocation("PITTSBURGH JUNCTION")
                .issuedDateTime(now.minusHours(6))
                .status(TrackWarrant.WarrantStatus.COMPLETED)
                .issuedBy("Dispatcher Smith")
                .okTime("0345")
                .dispatcher("SMITH")
                .copiedBy("WILSON")
                .limitsClearAt("CLEVELAND TERMINAL")
                .limitsClearBy("CONDUCTOR WILSON")
                .line2Instruction("Proceed from PITTSBURGH JUNCTION to CLEVELAND TERMINAL on OHIO MAIN LINE Track")
                .line6Instruction("This Authority Expires at " + now.minusHours(1).toLocalTime().toString().substring(0, 5) + "M")
                .line13Instruction("Do not exceed 70 MPH between PITTSBURGH JUNCTION and CLEVELAND TERMINAL")
                .build();

            repository.save(warrant1);
            repository.save(warrant2);
            repository.save(warrant3);

            log.info("Sample data initialized. Created {} track warrants.", repository.count());
        };
    }
}
