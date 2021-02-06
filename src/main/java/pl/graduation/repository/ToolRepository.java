package pl.graduation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.graduation.model.Tool;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Integer> {
    @Query("SELECT tool FROM Tool tool LEFT OUTER JOIN Likes likes " +
            "ON tool.id=likes.tool.id " +
            "AND tool.id=likes.tool.id " +
            "GROUP BY tool.id " +
            "ORDER BY COUNT(likes.isLiked) DESC")
    List<Tool> findAllToolsOrderByNumberOfLikesDesc();

    @Query("SELECT tool FROM Tool tool LEFT OUTER JOIN Likes likes " +
            "ON tool.id=likes.tool.id " +
            "AND tool.id=likes.tool.id " +
            "GROUP BY tool.id " +
            "ORDER BY COUNT(likes.isLiked) ASC")
    List<Tool> findAllToolsOrderByNumberOfLikesAsc();

    @Query("SELECT tool FROM Tool tool ORDER BY tool.name DESC")
    List<Tool> findAllToolsOrderByAlphabeticDesc();

    @Query("SELECT tool FROM Tool tool ORDER BY tool.name ASC")
    List<Tool> findAllToolsOrderByAlphabeticAsc();
}
