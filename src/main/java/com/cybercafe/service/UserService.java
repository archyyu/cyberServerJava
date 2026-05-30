package com.cybercafe.service;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.cybercafe.model.User;
import com.cybercafe.repository.UserRepository;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User findUserByAccountAndPassword(long gid, String acount, String password) {
        
        Optional<User> userOptional = this.userRepository.findOne(Example.of(User.builder().gid(gid).account(acount).build()));
        if (userOptional.isEmpty()) {
            return null;
        }

        String md5Str = DigestUtils.md5Hex(password + userOptional.get().getSalt());
        if (md5Str.equals(userOptional.get().getPassword())) {
            User user = userOptional.get();
            user.setToken(this.jwtService.generateToken(user.getAccount()));
            return user;
        }

        return null;

    }

}
