package com.calculator.calculatorapi.config;

import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Optional<User> userInfo = repository.findUserByEmail(email);
        if (userInfo.isPresent()) {
            final User user = userInfo.get();
            if (user.getStatus().equals(UserStatus.INACTIVE)) {
                throw new AuthenticationServiceException("user " + email + " is currently inactive");
            }
            return new UserDetailsInfo(user);
        } else {
            throw new UsernameNotFoundException("user not found " + email);
        }
    }
}
