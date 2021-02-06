package pl.graduation.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.graduation.model.Comment;
import pl.graduation.model.Post;
import pl.graduation.model.User;
import pl.graduation.repository.CommentRepository;
import pl.graduation.repository.PostRepository;
import pl.graduation.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CommentController {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public CommentController(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/comment/{post_id}")
    public ResponseEntity<?> getComments(@PathVariable("post_id") int post_id){
        Post post = postRepository.findById(post_id).get();
        List<Comment> listOfComments = commentRepository.findAllByPost(post);
        if(listOfComments.isEmpty()) {
            JSONObject jsonObject = new JSONObject()
                    .appendField("status", "not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonObject);
        }

        ArrayList<JSONObject> jsonObject = new ArrayList<>();
        for (Comment x: listOfComments) {
            JSONObject jsonObject1 = new JSONObject()
                    .appendField("id", x.getId())
                    .appendField("username", x.getUser().getUsername())
                    .appendField("content", x.getContent())
                    .appendField("createdDate", x.getCreatedDate());
            jsonObject.add(jsonObject1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @PostMapping("/comment/{post_id}")
    public ResponseEntity<?> addComment(@AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser,
                                  @RequestBody JSONObject request,
                                  @PathVariable("post_id") int post_id){
        Optional<Post> post = postRepository.findById(post_id);
        User user = userRepository.findByUsername(authUser.getName());
        if (post.isEmpty())
            return ResponseEntity.notFound().build();
        commentRepository.save(new Comment(user, post.get(), request.get("content").toString(), LocalDateTime.now()));
        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @DeleteMapping("/comment/{comment_id}")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser,
                                        @PathVariable("comment_id") int comment_id) {
        User user = userRepository.findByUsername(authUser.getName());
        Optional<Comment> comment = commentRepository.findById(comment_id);
        if (comment.isPresent() && (user.getRole().contains("ROLE_ADMIN") || comment.get().getUser().getId() == user.getId())) {
            commentRepository.delete(comment.get());
            JSONObject jsonObject = new JSONObject()
                    .appendField("status", "success")
                    .appendField("messege", "Comment deleted");
            return ResponseEntity.ok(jsonObject);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/comment/{comment_id}")
    public ResponseEntity<?> updatePost(@AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser,
                                        @RequestBody Map<String, Object> updates,
                                        @PathVariable("comment_id") int comment_id) {
        User user = userRepository.findByUsername(authUser.getName());
        Optional<Comment> comment = commentRepository.findById(comment_id);
        if (comment.isPresent() && (user.getRole().contains("ROLE_ADMIN") || comment.get().getUser().getId() == user.getId())) {
            if(updates.containsKey("content")){
                comment.get().setContent(updates.get("content").toString());
                commentRepository.save(comment.get());
                JSONObject jsonObject = new JSONObject()
                        .appendField("status", "success")
                        .appendField("messege", "Comment updated");
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
