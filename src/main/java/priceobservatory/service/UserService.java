package priceobservatory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import priceobservatory.dto.UserDTO;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.UnauthorizedException;
import priceobservatory.exception.UserNotFoundException;
import priceobservatory.json.UserJson;
import priceobservatory.model.Token;
import priceobservatory.model.User;
import priceobservatory.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final ValidationService validationService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    UserService(
            @Autowired ValidationService validationService,
            @Autowired TokenService tokenService,
            @Autowired UserRepository userRepository
    ) {
        this.validationService = validationService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        Optional<User> optUser = findByUsername(username);
        if (optUser.isPresent()) {
            if (!BCrypt.checkpw(password, optUser.get().getPassword())) {
                throw new BadRequestException("Error: Wrong username or password");
            }
            int length = 60;
            boolean useLetters = true;
            boolean useNumbers = true;
            String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
            String hash = BCrypt.hashpw(generatedString, BCrypt.gensalt(10));
            tokenService.save(new Token(optUser.get().getRole(), hash));
            return "{ \"token\" : \"" + hash + "\"}";
        } else {
            throw new BadRequestException("Error: Wrong username or password");
        }
    }

    public String logout(String token) {
        validationService.validateToken(token);

        Optional<Token> t = tokenService.findByToken(token);
        tokenService.deleteById(t.get().getId());
        return "{\"message\": \"OK\" }";
    }

    public String getUsers(String token, Integer start, Integer count, String format) throws JsonProcessingException {
        validationService.checkAdminRole(token);
        validationService.validateGetParameters(start, count, format);

        List<User> users;
        users = findAll();
        users.sort(Comparator.comparing(User::getUsername));

        List<UserDTO> userDTOs = new ArrayList<>();
        for (int i = start; i <= start+count-1 && i < users.size(); i++) {
            userDTOs.add(users.get(i)._convertToUserDTO());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(
                new UserJson(start, count, Long.valueOf(userDTOs.size()), userDTOs)
        );
        return json;
    }

    public UserDTO postUser(UserDTO newUser, String format) {
        validationService.validatePostParameters(newUser, format);

        String encrypted = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(10));
        newUser.setPassword(encrypted);
        return save(newUser._convertToUser())._convertToUserDTO();
    }

    public String deleteUser(String token, Integer id, String format) {
        validationService.checkAdminRole(token);
        validationService.validateDeleteParameters(format);

        Optional<User> user = findById(id);
        if (user.isPresent()){
            if (!user.get().getRole().equals("admin"))
                deleteById(id);
            else
                throw new UnauthorizedException("Error: Cannot delete admin user");
        } else {
            throw new UserNotFoundException(id);
        }
        return "{\"message\": \"OK\" }";
    }
}
