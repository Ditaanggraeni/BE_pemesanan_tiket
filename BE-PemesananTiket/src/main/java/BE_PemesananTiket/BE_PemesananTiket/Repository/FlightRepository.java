package BE_PemesananTiket.BE_PemesananTiket.Repository;

import BE_PemesananTiket.BE_PemesananTiket.Model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findFlight(String origin, String destination, LocalDateTime startTime, LocalDateTime endTime);
}
