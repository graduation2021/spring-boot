package pl.graduation.model;

import javax.persistence.*;

@Entity
public class Likes {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private User user;
    private int isLiked = 0;
    @OneToOne
    private Tool tool;

    public Likes() {
    }

    public Likes(User user, int isLiked, Tool tool) {
        this.user = user;
        this.isLiked = isLiked;
        this.tool = tool;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public Tool getTool() {
        return tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }
}
