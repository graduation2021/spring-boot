package pl.graduation.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.graduation.model.Likes;
import pl.graduation.model.Tool;
import pl.graduation.model.User;
import pl.graduation.repository.LikesRepository;
import pl.graduation.repository.ToolRepository;
import pl.graduation.repository.UserRepository;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RequestMapping("api")
@RestController
public class LikeController {

    private final LikesRepository likesRepository;

    private final UserRepository userRepository;

    private final ToolRepository toolRepository;

    public LikeController(LikesRepository likesRepository, UserRepository userRepository, ToolRepository toolRepository) {
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.toolRepository = toolRepository;
    }

    @GetMapping("/like/{tool_id}")
    public ResponseEntity<?> getLikesOfTool(@PathVariable("tool_id") int tool_id, @AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser){
        Optional<Tool> tool = toolRepository.findById(tool_id);
        if(!tool.isPresent()){
            return ResponseEntity.notFound().build();
        }
        long numberOfLikes = likesRepository.findByTool(tool.get()).stream().filter(like -> like.getIsLiked() == 1).count();
        boolean isLikedByUser = false;
        if(authUser != null)
            isLikedByUser = likesRepository.findByTool(tool.get()).stream().anyMatch(like -> like.getUser().getUsername().equals(authUser.getName()));
        JSONObject jsonObject = new JSONObject()
                .appendField("numberOfLikes", numberOfLikes)
                .appendField("likedByUser", isLikedByUser);
        return ResponseEntity.ok(jsonObject);
    }

    @PostMapping("/like/{tool_id}")
    public ResponseEntity<?> addLikeToTool(@PathVariable("tool_id") int tool_id, @AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser){
        Optional<Tool> tool = toolRepository.findById(tool_id);
        User user = userRepository.findByUsername(authUser.getName());
        if(!tool.isPresent() || user == null){
            return ResponseEntity.notFound().build();
        }
        if (likesRepository.findByTool(tool.get()).stream().anyMatch(likes -> likes.getUser().getUsername().equals(authUser.getName())))
            return ResponseEntity.badRequest().body("This tool is already liked by this user");
        Likes likes = new Likes(user, 1, tool.get());
        likesRepository.save(likes);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/like/{tool_id}")
    public ResponseEntity<?> deleteLike(@PathVariable("tool_id") int tool_id, @AuthenticationPrincipal UsernamePasswordAuthenticationToken authUser){
        Optional<Tool> tool = toolRepository.findById(tool_id);
        User user = userRepository.findByUsername(authUser.getName());
        if(!tool.isPresent() || user == null){
            return ResponseEntity.notFound().build();
        }
        Optional<Likes> like = likesRepository.findByTool(tool.get()).stream().filter(x -> x.getUser().getUsername().equals(authUser.getName())).findAny();
        likesRepository.delete(like.get());
        return ResponseEntity.ok().build();
    }
}
