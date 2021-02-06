package pl.graduation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.graduation.model.Comment;
import pl.graduation.model.Post;
import pl.graduation.model.User;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPost(Post post);
    List<Comment> findAllByUser(User user);
}
