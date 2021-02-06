package pl.graduation.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Tool {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    @Column(length = 1000)
    private String iconURL;
    private String shortDescription;
    @Column(length = 1500)
    private String fullDescription;
    @ElementCollection
    private List<String> categories;
    private String directLinkURL;
    @ElementCollection
    private List<String> platforms;
    @ElementCollection
    private List<String> keywords;

    public Tool() {
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDirectLinkURL() {
        return directLinkURL;
    }

    public void setDirectLinkURL(String directLinkURL) {
        this.directLinkURL = directLinkURL;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    @Override
    public String toString() {
        return "Tool{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", iconURL='" + iconURL + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", categories=" + categories +
                ", directLinkURL='" + directLinkURL + '\'' +
                ", platforms=" + platforms +
                '}';
    }
}
