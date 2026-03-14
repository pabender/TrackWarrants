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
                .line2From("CHICAGO UNION STATION")
                .line2To("ST. LOUIS TERMINAL")
                .line2Track("ILLINOIS MAIN LINE")
                .line6Time(now.plusHours(8).toLocalTime().toString().substring(0, 5))
                .line13Mph("79")
                .line13From("CHICAGO UNION STATION")
                .line13To("ST. LOUIS TERMINAL")
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
                .line2From("DENVER YARD")
                .line2To("SALT LAKE CITY")
                .line2Track("MOUNTAIN SUBDIVISION")
                .line6Time(now.minusHours(2).plusHours(10).toLocalTime().toString().substring(0, 5))
                .line12From("DENVER YARD")
                .line12To("SALT LAKE CITY")
                .line13Mph("60")
                .line13From("DENVER YARD")
                .line13To("SALT LAKE CITY")
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
                .line2From("PITTSBURGH JUNCTION")
                .line2To("CLEVELAND TERMINAL")
                .line2Track("OHIO MAIN LINE")
                .line6Time(now.minusHours(1).toLocalTime().toString().substring(0, 5))
                .line13Mph("70")
                .line13From("PITTSBURGH JUNCTION")
                .line13To("CLEVELAND TERMINAL")
                .build();

            repository.save(warrant1);
            repository.save(warrant2);
            repository.save(warrant3);

            log.info("Sample data initialized. Created {} track warrants.", repository.count());
        };
    }
}
