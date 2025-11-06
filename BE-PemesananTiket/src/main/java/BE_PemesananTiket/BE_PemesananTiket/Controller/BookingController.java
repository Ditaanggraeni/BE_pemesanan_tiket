package BE_PemesananTiket.BE_PemesananTiket.Controller;

import BE_PemesananTiket.BE_PemesananTiket.Model.Booking;
import BE_PemesananTiket.BE_PemesananTiket.Model.Flight;
import BE_PemesananTiket.BE_PemesananTiket.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/book")
public class BookingController {
    @Autowired
    BookingService bookingService;

    //search flight
    @GetMapping("/flight/search")
    public List<Flight> searchFlight(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime date) {
        return bookingService.searchFlights(origin, destination, date);
    }

    //select flight (pilih penerbangan)
    @GetMapping("/flights/{id}")
    public ResponseEntity<Flight> selectFlight(@PathVariable Long id) {
        Optional<Flight> flight = bookingService.selectFlight(id);
        return flight.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> saveBooking(@RequestBody Map<String, String> bookingRequest) {
        try {
            Long flightId = Long.parseLong(bookingRequest.get("flightId"));
//            Long userId = Long.parseLong(bookingRequest.get("userId"));
            String name = bookingRequest.get("passengerName");
            String email = bookingRequest.get("contactEmail");

            Booking booking = bookingService.saveBooking(flightId, name, email);
            return new ResponseEntity<>(booking, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Menangani kasus seperti penerbangan tidak ditemukan atau kursi penuh
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
