package pl.graduation.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.graduation.model.Tool;
import pl.graduation.repository.ToolRepository;

import java.util.LinkedHashSet;
import java.util.List;

@RestController
@RequestMapping("api/")
@CrossOrigin(origins = "*")
public class PlatformsController {

    private final ToolRepository toolRepository;

    public PlatformsController(ToolRepository toolRepository){
        this.toolRepository = toolRepository;
    }

    @GetMapping("platforms")
    public ResponseEntity<?> getPlatforms() {
        List<Tool> listOfTools = toolRepository.findAll();
        if (listOfTools.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        LinkedHashSet<String> platforms = new LinkedHashSet<>();
        listOfTools.forEach(tool -> platforms.addAll(tool.getPlatforms()));

        JSONObject jsonObject = new JSONObject()
                .appendField("platforms", platforms);

        return ResponseEntity.status(HttpStatus.OK).body(jsonObject);
    }
}
