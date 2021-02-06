package pl.graduation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.graduation.model.User;
import pl.graduation.repository.CommentRepository;
import pl.graduation.repository.PostRepository;
import pl.graduation.repository.ToolRepository;
import pl.graduation.repository.UserRepository;
import pl.graduation.util.ExcelReader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class GraduationWorkApplication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public static void main(String[] args) {
        SpringApplication.run(GraduationWorkApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*").allowedHeaders("*").allowedOrigins("*").allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }

    @PostConstruct
    void setUp() throws IOException {

        ExcelReader.fillDB(toolRepository, "FUTURE_ Database final contentPL.xlsx");

        userRepository.save(new User("Tomek", new BCryptPasswordEncoder().encode("tomek"), Arrays.asList("ROLE_USER", "ROLE_EDITOR", "ROLE_ADMIN"), "tomek@gmail.com", 23));
        userRepository.save(new User("Marcin", new BCryptPasswordEncoder().encode("marcin"), Arrays.asList("ROLE_USER"), "marcin@gmail.com", 12));
        userRepository.save(new User("Piotr", new BCryptPasswordEncoder().encode("piotr"), Arrays.asList("ROLE_USER"), "piotr@gmail.com", 21));
        userRepository.save(new User("Ziomek", new BCryptPasswordEncoder().encode("ziomek"), Arrays.asList("ROLE_USER", "ROLE_EDITOR"), "ziomek@gmail.com", 87));
    }
}
