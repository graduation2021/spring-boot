package pl.graduation.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.graduation.config.CustomUserDetailsService;
import pl.graduation.model.AuthRequest;
import pl.graduation.model.User;
import pl.graduation.repository.CommentRepository;
import pl.graduation.repository.LikesRepository;
import pl.graduation.repository.PostRepository;
import pl.graduation.repository.UserRepository;
import pl.graduation.util.JwtUtil;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final LikesRepository likesRepository;

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

    public UserController(UserRepository userRepository, AuthenticationManager authenticationManager, PostRepository postRepository, CommentRepository commentRepository, LikesRepository likesRepository, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("admin/users")
    public ResponseEntity<?> getUsers() {
        List<User> listOfUsers = userRepository.findAll();
        if (listOfUsers.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success")
                .appendField("users", listOfUsers);
        return ResponseEntity.ok(jsonObject);
    }

    @GetMapping("admin/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent())
            return ResponseEntity.notFound().build();

        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success")
                .appendField("user", user);
        return ResponseEntity.ok(jsonObject);
    }

    @PatchMapping("admin/user/{id}")
    public ResponseEntity<?> updateRole(@PathVariable("id") int id, @RequestBody Map<String, Object> updates){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent())
            return ResponseEntity.notFound().build();

        if(updates.containsKey("role")){
            Object objectRoles = updates.get("role");
            if(objectRoles != null){
                List<String> roles = new LinkedList<String>(Arrays.asList(objectRoles.toString().substring(1, objectRoles.toString().length() - 1).split(", ")));
                user.get().setRole(roles);
                userRepository.save(user.get());
                return ResponseEntity.ok("User updated");
            }
        }
        return ResponseEntity.badRequest().body("There is no data to update");
    }

    @DeleteMapping("admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent())
            return ResponseEntity.notFound().build();

        postRepository.findAllByUser(user.get()).forEach(post -> {
            commentRepository.findAllByPost(post).forEach(commentRepository::delete);
            postRepository.delete(post);
        });
        commentRepository.findAllByUser(user.get()).forEach(commentRepository::delete);
        likesRepository.findAllByUser(user.get()).forEach(likesRepository::delete);

        userRepository.delete(user.get());
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is user with such username");
        if (userRepository.findByEmail(user.getEmail()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is user with such email");

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        newUser.setEmail(user.getEmail());
        newUser.setAge(user.getAge());
        newUser.setRole(Collections.singletonList("ROLE_USER"));
        userRepository.save(newUser);
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("auth")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), authRequest.getPassword()));
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject()
                    .appendField("status", "failed")
                    .appendField("message", "Wprowadzono złą nazwę użytkownika lub hasło");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject);
        }
        User user = userRepository.findByUsername(authRequest.getUsername());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        JSONObject jsonObject = new JSONObject()
                .appendField("token", jwtUtil.generateToken(userDetails))
                .appendField("username", authRequest.getUsername())
                .appendField("roles", user.getRole())
                .appendField("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @GetMapping("admin/role")
    public ResponseEntity<?> getRoles(){
        List<User> listOfUsers = userRepository.findAll();
        LinkedHashSet<String> listOfRoles = new LinkedHashSet<>();
        listOfUsers.forEach(user -> listOfRoles.addAll(user.getRole()));

        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success")
                .appendField("roles", listOfRoles);

        return ResponseEntity.ok(jsonObject);
    }
}
