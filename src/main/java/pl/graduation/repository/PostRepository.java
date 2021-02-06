package pl.graduation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.graduation.model.Post;
import pl.graduation.model.Tool;
import pl.graduation.model.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByTool(Tool tool);
    List<Post> findAllByUser(User user);
}
