package BE_PemesananTiket.BE_PemesananTiket.Repository;

import BE_PemesananTiket.BE_PemesananTiket.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookCode(String bookCode);
}
