package com.interactivehome.main_service.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interactivehome.main_service.model.common.dto.ResponseDto;
import com.interactivehome.main_service.model.user.dto.UserDto;
import com.interactivehome.main_service.model.user.entity.User;
import com.interactivehome.main_service.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<ResponseDto> RegisterUser(@RequestBody UserDto dto) {
        userService.registerUser(dto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setSuccess(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/user/{userId}")
    public User GetUser(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    @PutMapping("/user/{userId}")
    public User ModifyUser(@PathVariable Integer userId,
                           @RequestBody UserDto dto) {
        return userService.modifyUser(userId, dto);
    }

    @GetMapping("/user")
    public List<User> GetAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/user/{userId}")
    public Boolean DeleteUser(@PathVariable Integer userId) {
        return userService.deleteUser(userId);
    }
}
