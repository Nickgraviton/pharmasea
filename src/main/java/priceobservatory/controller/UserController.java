package priceobservatory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priceobservatory.dto.UserDTO;

import priceobservatory.service.UserService;

@RestController
@RequestMapping(path = "/observatory/api")
public class UserController {
    @Autowired
    private UserService userService;

    // User login
    @PostMapping(path = "/login", produces = "application/json")
    String login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        return userService.login(username, password);
    }

    // User logout
    @PostMapping(path = "/logout", produces = "application/json")
    String logout(
            @RequestHeader("X-OBSERVATORY-AUTH") String token
    ) {
        return userService.logout(token);
    }

    // Returns JSON response with all users
    @GetMapping(path = "/users", produces = "application/json")
    String getUsers(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @RequestParam(defaultValue = "0") Integer start,
            @RequestParam(defaultValue = "20") Integer count,
            @RequestParam(defaultValue = "json") String format
    ) throws JsonProcessingException {
        return userService.getUsers(token, start, count, format);
    }

    // Creates new user
    @PostMapping(path = "/users", produces = "application/json")
    UserDTO postUser(
            @ModelAttribute("user") UserDTO newUser,
            @RequestParam(defaultValue = "json") String format
    ) {
        return userService.postUser(newUser, format);
    }

    // Deletes user
    @DeleteMapping(path = "/users/{id}", produces = "application/json")
    String deleteUser(
            @RequestHeader("X-OBSERVATORY-AUTH") String token,
            @PathVariable Integer id,
            @RequestParam(defaultValue = "json") String format
    ) {
        return userService.deleteUser(token, id, format);
    }
}
