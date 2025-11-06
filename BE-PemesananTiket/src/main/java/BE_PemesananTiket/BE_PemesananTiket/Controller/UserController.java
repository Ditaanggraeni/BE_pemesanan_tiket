package BE_PemesananTiket.BE_PemesananTiket.Controller;

import BE_PemesananTiket.BE_PemesananTiket.DTO.UserLoginReq;
import BE_PemesananTiket.BE_PemesananTiket.DTO.UserRegistrationReq;
import BE_PemesananTiket.BE_PemesananTiket.Model.User;
import BE_PemesananTiket.BE_PemesananTiket.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users/")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationReq registrationReq){
        try{
            User newUser = userService.registerNewUser(
                    registrationReq.getUsername(),
                    registrationReq.getPassword(),
                    registrationReq.getEmail(),
                    registrationReq.getFullName()
            );
            return new ResponseEntity<>(Map.of(
                    "id", newUser.getId(),
                    "username", newUser.getUsername(),
                    "message", "Registrasi berhasil"
            ), HttpStatus.CREATED);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginReq loginReq){
        String username = loginReq.getUsername();
        String password = loginReq.getPassword();

        Optional<User> authenticateUser = userService.authenticateUser(username, password);

        if (authenticateUser.isPresent()) {
            User user = authenticateUser.get();

            return ResponseEntity.ok(Map.of(
                    "message", "Login berhasil",
                    "userId", user.getId(),
                    "username", user.getUsername()
            ));
        } else {
            // Mengembalikan status 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Username atau kata sandi salah."));
        }
    }
}
