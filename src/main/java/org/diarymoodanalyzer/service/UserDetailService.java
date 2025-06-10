package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.exception.NotFoundException;
import org.diarymoodanalyzer.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implement {@link UserDetailsService} for using Spring Security.
 * Load {@link org.diarymoodanalyzer.domain.User} entity by <code>username</code>.
 * <br/>
 * Used for JWT token based authentication system that I implemented.
 * @see org.diarymoodanalyzer.domain.User
 * @see org.diarymoodanalyzer.config.jwt.TokenProvider
 * @see org.diarymoodanalyzer.config.jwt.JwtAuthenticationToken
 * @see org.diarymoodanalyzer.config.TokenAuthenticationFilter
 */
@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load {@link UserDetails} object by <code>username</code>.
     * <br/>
     * In this project, <code>username</code> is same as <code>email</code> fields of {@link org.diarymoodanalyzer.domain.User}
     * @param username the username identifying the user whose data is required.
     * @return {@link UserDetails} object specified by <code>email</code>
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found User: " + username));
    }
}
