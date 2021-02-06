package pl.graduation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.graduation.util.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/h2/**").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/tools").hasAnyRole("EDITOR", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/tool/**").hasAnyRole("EDITOR", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/tool/**").hasAnyRole("EDITOR", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/post/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/post/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/post/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/comment/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/comment/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/comment/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/like/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/like/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                .anyRequest().permitAll()
                .and().exceptionHandling()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers().frameOptions().disable();
    }
}
