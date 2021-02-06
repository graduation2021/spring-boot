package pl.graduation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.graduation.model.Likes;
import pl.graduation.model.Tool;
import pl.graduation.model.User;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Integer> {
    List<Likes> findByTool(Tool tool);
    List<Likes> findAllByUser(User user);
}
