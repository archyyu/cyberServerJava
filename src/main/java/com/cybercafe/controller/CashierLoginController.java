package com.cybercafe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cybercafe.model.User;
import com.cybercafe.model.dto.request.LoginUserRequest;
import com.cybercafe.model.exception.SurfException;
import com.cybercafe.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CashierLoginController {

    private final UserService userService;
    
    public ResponseEntity<User> login(@RequestBody LoginUserRequest request) throws SurfException {
        
        User user = this.userService.findUserByAccountAndPassword(request.gid(), request.account(), request.password());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new SurfException("user not found");
        }

    }

}
