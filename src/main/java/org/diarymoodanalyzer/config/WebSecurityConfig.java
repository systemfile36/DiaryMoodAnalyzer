package org.diarymoodanalyzer.config;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

/*
스프링 시큐리티에 필요한 설정들을 람다식 빈 등록으로 구현
@Bean으로 선언된 메소드들의 반환값은 빈으로 등록됨. 사용 가능
 */
@RequiredArgsConstructor
@Configuration //설정 파일임을 선언
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailService userDetailService;

    /*
    보안 인증이 불필요한 정적 리소스에 대해 필터 적용을 해제하는 부분
    H2Console에 대해서도 인증 해제 (원활한 테스트를 위함)
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**")
                .requestMatchers("/resources/**");
    }

    /*
    필터 기반의 스프링 시큐리티의 주요 보안 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable() //CSRF 보호 비활성화. JWT 기반 인증에는 필요 X
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않음(Stateless)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll() //로그인/회원가입 엔드 포인트는 인증 불필요. 누구나 접근 O
                .anyRequest().authenticated() //다른 모든 요청은 인증이 필요
                .and()
                //.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //아직 미구현
                .build();
    }

    /*
    AuthenticationManager에 대한 설정.
    UserDetailsService와 PasswordEncoder를 설정
     */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserDetailService userDetailService
    ) throws Exception  {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(this.userDetailService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    //패스워드 인코더 빈으로 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
