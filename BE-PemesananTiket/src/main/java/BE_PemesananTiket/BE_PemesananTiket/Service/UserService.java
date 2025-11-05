package BE_PemesananTiket.BE_PemesananTiket.Service;

import BE_PemesananTiket.BE_PemesananTiket.Model.User;
import BE_PemesananTiket.BE_PemesananTiket.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User registerNewUser(String username, String password, String email, String fullName) {
        if (userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Nama pengguna ' " + username + " ' sudah tedaftar, silahkan gunakan username lain.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(password);
        newUser.setEmail(email);
        newUser.setFullName(fullName);

        return userRepository.save(newUser);
    }

    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()){
            User user = userOptional.get();

            if (user.getPasswordHash().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
