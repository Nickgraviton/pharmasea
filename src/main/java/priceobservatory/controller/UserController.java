package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.UserDTO;
import priceobservatory.exception.BadRequestException;
import priceobservatory.exception.UnauthorizedException;
import priceobservatory.exception.UserNotFoundException;
import priceobservatory.json.UserJson;
import priceobservatory.model.Token;
import priceobservatory.model.User;
import priceobservatory.service.TokenService;
import priceobservatory.service.UserService;

import java.util.*;

@RestController
@RequestMapping(path = "/observatory/api")
public class UserController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    // user login
    @PostMapping(path = "/login", produces = "application/json")
    String login(@RequestParam Map<String, String> body) {
        Optional<User> optUser = userService.findByUsername(body.get("username"));
        if (optUser.isPresent()) {
            if (!BCrypt.checkpw(body.get("password"), optUser.get().getPassword())) {
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

    // user logout
    @PostMapping(path = "/logout", produces = "application/json")
    String message(@RequestHeader("X-OBSERVATORY-AUTH") String token) {
        Optional<Token> t = tokenService.findByToken(token);
        if (t.isEmpty()) {
            throw new BadRequestException("Invalid token");
        }
        tokenService.deleteById(t.get().getId());
        return "{\"message\": \"OK\" }";
    }

    // returns JSON with all users
    @GetMapping(path = "/users", produces = "application/json")
    String allUsers(@RequestParam Map<String, String> params,
                    @RequestHeader("X-OBSERVATORY-AUTH") String token) throws JsonProcessingException {

        Integer start, count;
        String format;
        start  = (params.get("start")  == null) ? 0         : Integer.valueOf(params.get("start"));
        count  = (params.get("count")  == null) ? 20        : Integer.valueOf(params.get("count"));
        format = (params.get("format") == null) ? "json"    : params.get("format");

        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);
        if (!role.equals("admin"))
            throw new UnauthorizedException("Error: Only admins can perform this action");

        List<User> users;
        List<UserDTO> userDTOs = new ArrayList<>();
        boolean validStart = true, validCount = true, validFormat = true;

        if (start < 0)
            validStart = false;
        if (count <= 0)
            validCount = false;
        if (!format.equals("json"))
            validFormat = false;


        if (validStart && validCount && validFormat) {
            users = userService.findAll();
            users.sort(Comparator.comparing(User::getUsername));

            for (int i = start; i <= start+count-1 && i < users.size(); i++) {
                userDTOs.add(users.get(i)._convertToUserDTO());
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            String json = mapper.writeValueAsString(
                    new UserJson(start, count, Long.valueOf(userDTOs.size()), userDTOs));

            return json;
        } else {
            StringBuilder builder = new StringBuilder("Error: ");
            if (!validStart)
                builder.append("Invalid start parameter, ");
            if (!validCount)
                builder.append("Invalid count parameter, ");
            if (!validFormat)
                builder.append("Invalid format, ");
            builder.setLength(builder.length() - 2);
            throw new BadRequestException(builder.toString());
        }
    }

    // creates new user
    @PostMapping(path = "/users", produces = "application/json")
    UserDTO newUser(@ModelAttribute("user") UserDTO newUser,
                    @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        if (newUser.anyNull())
            throw new BadRequestException("Error: Invalid user field(s)");

        String encrypted = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(10));
        newUser.setPassword(encrypted);
        return userService.save(newUser._convertToUser())._convertToUserDTO();
    }

    // deletes user
    @DeleteMapping(path = "/users/{id}", produces = "application/json")
    String deleteUser(@PathVariable Integer id, @RequestHeader("X-OBSERVATORY-AUTH") String token,
                      @RequestParam Map<String, String> params) {

        String format = (params.get("format") == null) ? "json" : params.get("format");
        if (token == null)
            throw new UnauthorizedException("Error: You need to be a logged in user to perform this action");
        String role = tokenService.determineRole(token);

        if (!format.equals("json"))
            throw new BadRequestException("Error: Invalid format");

        if (role.equals("admin")) {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()){
                if (!user.get().getRole().equals("admin"))
                    userService.deleteById(id);
                else
                    throw new UnauthorizedException("Error: Cannot delete admin user");
            } else {
                throw new UserNotFoundException(id);
            }
            return "{\"message\": \"OK\" }";
        } else {
            throw new UnauthorizedException("Error: Only admins can perform this action");
        }
    }
}
