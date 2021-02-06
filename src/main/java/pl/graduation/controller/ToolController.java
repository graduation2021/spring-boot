package pl.graduation.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.graduation.model.Tool;
import pl.graduation.repository.CommentRepository;
import pl.graduation.repository.LikesRepository;
import pl.graduation.repository.PostRepository;
import pl.graduation.repository.ToolRepository;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/")
public class ToolController {

    private final ToolRepository toolRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;

    public ToolController(ToolRepository toolRepository, PostRepository postRepository, CommentRepository commentRepository, LikesRepository likesRepository) {
        this.toolRepository = toolRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
    }

    @GetMapping("tools")
    public ResponseEntity<?> getTools(@RequestParam(value = "categories", defaultValue = "") String category,
                                      @RequestParam(value = "platforms", defaultValue = "") String platform,
                                      @RequestParam(value = "keywords", defaultValue = "") String keywords,
                                      @RequestParam(value = "orderBy", defaultValue = "numberOfLikesDesc") String orderBy) {
        List<Tool> listOfTools = new ArrayList<>();
        switch (orderBy) {
            case "numberOfLikesDesc":
                listOfTools = toolRepository.findAllToolsOrderByNumberOfLikesDesc();
                break;
            case "numberOfLikesAsc":
                listOfTools = toolRepository.findAllToolsOrderByNumberOfLikesAsc();
                break;
            case "alphabeticDesc":
                listOfTools = toolRepository.findAllToolsOrderByAlphabeticDesc();
                break;
            case "alphabeticAsc":
                listOfTools = toolRepository.findAllToolsOrderByAlphabeticAsc();
                break;
        }

        if (listOfTools.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        LinkedHashSet<String> categories = new LinkedHashSet<>();
        LinkedHashSet<String> platforms = new LinkedHashSet<>();
        listOfTools.forEach(y -> categories.addAll(y.getCategories()));
        listOfTools.forEach(y -> platforms.addAll(y.getPlatforms()));

        if (!category.equals("") && !platform.equals("")) {
            listOfTools = listOfTools.stream()
                    .filter(y -> y.getCategories().containsAll(Arrays.asList(category.split(","))))
                    .filter(y -> y.getPlatforms().containsAll(Arrays.asList(platform.split(","))))
                    .collect(Collectors.toList());
        } else if (!category.equals("")) {
            listOfTools = listOfTools.stream()
                    .filter(y -> y.getCategories().containsAll(Arrays.asList(category.split(","))))
                    .collect(Collectors.toList());
        } else if (!platform.equals("")) {
            listOfTools = listOfTools.stream()
                    .filter(y -> y.getPlatforms().containsAll(Arrays.asList(platform.split(","))))
                    .collect(Collectors.toList());
        }

        if (!keywords.equals("")){
            listOfTools = listOfTools.stream()
                    .filter(tool -> {  return Arrays.stream(keywords.split(",")).map(String::trim).anyMatch(tool.getName().toLowerCase()::contains) ||
                            Arrays.stream(keywords.split(",")).map(String::trim).anyMatch(keywordParam -> tool.getKeywords().stream().anyMatch(toolKeyword -> toolKeyword.contains(keywordParam)))
                            ;})
                    .collect(Collectors.toList());
        }

        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success")
                .appendField("tools", listOfTools)
                .appendField("categories", categories)
                .appendField("platforms", platforms);
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @PostMapping("tools")
    public ResponseEntity<?> addTool(@RequestBody Tool tool) {
        if (tool == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        toolRepository.save(tool);
        return ResponseEntity.ok().build();
    }

    @GetMapping("tool/{id}")
    public ResponseEntity<?> getTool(@PathVariable("id") int id) {
        Optional<Tool> tool = toolRepository.findById(id);
        if (tool.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is no Tool with such id");
        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success")
                .appendField("tool", tool);
        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }

    @DeleteMapping("tool/{id}")
    public ResponseEntity<?> deleteTool(@PathVariable("id") int id) {
        if (toolRepository.findById(id).isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no tool with such id");
        Optional<Tool> tool = toolRepository.findById(id);
        postRepository.findAllByTool(tool.get()).forEach(post -> {
            commentRepository.findAllByPost(post).forEach(commentRepository::delete);
            postRepository.delete(post);
        });
        likesRepository.findByTool(tool.get()).forEach(likesRepository::delete);
        toolRepository.deleteById(id);
        JSONObject jsonObject = new JSONObject()
                .appendField("status", "success")
                .appendField("messege", "Tool deleted");
        return ResponseEntity.ok(jsonObject);
    }

    @PutMapping("tool/{id}")
    public ResponseEntity<?> updateTool(@PathVariable("id") int id, @RequestBody Tool tool) {
        System.out.println("asd");
        Optional<Tool> toolOptional = toolRepository.findById(id);
        if (toolOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        tool.setId(id);
        toolRepository.save(tool);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }
}
