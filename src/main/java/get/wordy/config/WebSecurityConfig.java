package get.wordy.config;

import get.wordy.users.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public CustomUserDetailsService userDetailsService(@Autowired DataSource dataSource) {
        return new CustomUserDetailsService(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/users/registration",
                                "/reset_password"
                        )
                        .permitAll()
                        .requestMatchers("/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/welcome", true)
                        .failureUrl("/login?error=true")
//                        .successHandler(successHandler())  // Custom success handler
//                        .failureHandler(failureHandler())   // Custom failure handler
                        .permitAll()
                )
                .logout((exit) -> exit
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }

    // Custom success handler
    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            // Return a success response, such as user details or a simple message
            response.setStatus(200);
            response.getWriter().write("Login Successful");
            response.getWriter().flush();
        };
    }

    // Custom failure handler
    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            // Return a 403 Forbidden status
            response.sendError(403, "Authentication Failed");
        };
    }

}
