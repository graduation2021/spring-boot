package pl.graduation.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.graduation.model.Comment;
import pl.graduation.model.Post;
import pl.graduation.model.Tool;
import pl.graduation.model.User;
import pl.graduation.repository.CommentRepository;
import pl.graduation.repository.PostRepository;
import pl.graduation.repository.ToolRepository;
import pl.graduation.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RequestMapping("api")
@RestController
public class PostController {

    private final PostRepository postRepository;

    private final ToolRepository toolRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public PostController(PostRepository postRepository, ToolRepository toolRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.toolRepository = toolRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/post/{tool_id}")
    public ResponseEntity<?> getPosts(@PathVariable("tool_id") int tool_id) {
        Tool tool = toolRepository.findById(tool_id).get();
        List<Post> listOfPosts = postRepository.findAllByTool(tool);
        if (listOfPosts.isEmpty()) {
            JSONObject jsonObject = new JSONObject()
                    .appendField("status", "not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject);
        }

        ArrayList<JSONObject> jsonObject = new ArrayList<>();
        for (Post x : listOfPosts) {
            JSONObject jsonObject1 = new JSONObject()
                    .appendField("id", x.getId())
                    .appendField("username", x.getUser().getUsername())
                    .appendField("content", x.getContent())
                    .appendField("createdDate", x.getCreatedDate());
            jsonObject.add(jsonObject1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @PostMapping("/post/{tool_id}")
    public ResponseEntity<?> addPost(@AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser,
                                  @RequestBody JSONObject request,
                                  @PathVariable("tool_id") int tool_id) {
        Optional<Tool> tool = toolRepository.findById(tool_id);
        User user = userRepository.findByUsername(authUser.getName());
        if(tool.isEmpty())
            return ResponseEntity.notFound().build();
        postRepository.save(new Post(tool.get(), user, request.get("content").toString(), LocalDateTime.now()));
        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success");
        List<Post> listOfPostsOfThisUser = postRepository.findAllByUser(user);
        if (listOfPostsOfThisUser.size() >= 10) {
            List<String> roles = new ArrayList<>(user.getRole());
            if (!roles.contains("ROLE_EDITOR")) {
                roles.add("ROLE_EDITOR");
                user.setRole(roles);
                userRepository.save(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @DeleteMapping("/post/{post_id}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser,
                                        @PathVariable("post_id") int post_id) {
        User user = userRepository.findByUsername(authUser.getName());
        Optional<Post> post = postRepository.findById(post_id);
        if (post.isPresent() && (user.getRole().contains("ROLE_ADMIN") || post.get().getUser().getId() == user.getId())) {
            List<Comment> listOfComments = commentRepository.findAllByPost(post.get());
            listOfComments.forEach(comment -> commentRepository.delete(comment));
            postRepository.delete(post.get());
            JSONObject jsonObject = new JSONObject()
                    .appendField("status", "success")
                    .appendField("messege", "Post deleted");
            return ResponseEntity.ok(jsonObject);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/post/{post_id}")
    public ResponseEntity<?> updatePost(@AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser,
                                        @RequestBody Map<String, Object> updates,
                                        @PathVariable("post_id") int post_id) {
        User user = userRepository.findByUsername(authUser.getName());
        Optional<Post> post = postRepository.findById(post_id);
        if (post.isPresent() && (user.getRole().contains("ROLE_ADMIN") || post.get().getUser().getId() == user.getId())) {
            if(updates.containsKey("content")){
                post.get().setContent(updates.get("content").toString());
                postRepository.save(post.get());
                JSONObject jsonObject = new JSONObject()
                        .appendField("status", "success")
                        .appendField("messege", "Post updated");
                return ResponseEntity.ok(jsonObject);
            }
            JSONObject jsonObject = new JSONObject()
                    .appendField("status", "failed")
                    .appendField("messege", "There is no data to update");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(jsonObject);
        }
        return ResponseEntity.notFound().build();
    }
}









