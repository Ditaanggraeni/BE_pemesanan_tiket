package BE_PemesananTiket.BE_PemesananTiket.Service;

import BE_PemesananTiket.BE_PemesananTiket.Model.Booking;
import BE_PemesananTiket.BE_PemesananTiket.Model.Flight;
import BE_PemesananTiket.BE_PemesananTiket.Repository.BookingRepository;
import BE_PemesananTiket.BE_PemesananTiket.Repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    @Autowired
    UserService userService;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    BookingRepository bookingRepository;

    //search flight
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime depatureDate) {
        //pencarian untuk rentang waktu 24 jam di sekitar tanggal yang diminta
        LocalDateTime startTime = depatureDate.toLocalDate().atStartOfDay();
        LocalDateTime endTime = depatureDate.toLocalDate().atTime(23, 59, 59);

        return flightRepository.findFlight(origin, destination, startTime, endTime);
    }

    public Optional<Flight> selectFlight(Long flightId){
        return flightRepository.findById(flightId);
    }

    @Transactional
    public Booking saveBooking(Long flightId, Long userId, String passengerName, String contactEmail){
        if (userService.findById(userId).isEmpty()){
            throw new RuntimeException("Pengguna dengan ID " + userId + " tidak ditemukan.");
        }

        //get data penerbangan untuk update inventaris
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Penerbangan tidak ditemukan."));

        if (flight.getAvailableSeats() <= 0) {
            throw new RuntimeException("Penerbangan penuh. Pemesanan dibatalkan");
        }

        //kurangi inventaris setiap terjadi pemesanan
        flight.setAvailableSeats(flight.getAvailableSeats()-1);
        flightRepository.save(flight);

        //create and save booking
        Booking newBooking = new Booking();
        newBooking.setFlightId(flightId);
        newBooking.setUserId(userId);
        newBooking.setBookCode(UUID.randomUUID().toString().substring(0, 6).toLowerCase());
        newBooking.setPassangerName(passengerName);
        newBooking.setEmail(newBooking.getEmail());
        newBooking.setBookDate(LocalDateTime.now());
        newBooking.setStatus("CONFIRMED");

        return bookingRepository.save(newBooking);
    }

    public Optional<Booking> updateDetailBooking(String bookCode, String newEmail, String newName){
        return bookingRepository.findByBookCode(bookCode).map(booking -> {
            booking.setEmail(newEmail);
            booking.setPassangerName(newName);
            //save update
            return bookingRepository.save(booking);
        });
    }

    @Transactional
    public boolean cancelBooking(String bookCode){
        Optional<Booking> bookOpt = bookingRepository.findByBookCode(bookCode);
        //validasi jika booking codenya kosong atau statusnya sudah canceled
        if (bookOpt.isEmpty() || bookOpt.get().getStatus().equals("CANCELED")){
            return false;
        }

        Booking booking = bookOpt.get();

        //tambahkan kembali kursi ke inventaris flight
        Optional<Flight> flightOpt = flightRepository.findById(booking.getFlightId());

        flightOpt.ifPresent(flight -> {
            flight.setAvailableSeats(flight.getAvailableSeats() + 1); //tambahkan 1 kursi kembali
            flightRepository.save(flight);
        });

        //perbarui status flight
        booking.setStatus("CANCELED");
        bookingRepository.save(booking);

        return  true;
    }
}
