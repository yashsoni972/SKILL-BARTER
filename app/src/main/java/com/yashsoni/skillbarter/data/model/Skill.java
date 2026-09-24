package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Skill implements Serializable {
    @SerializedName("_id")
    private String id;
    private String userId;
    private String skillName;
    private String category;
    private String type; // "offer" or "want"
    private String level; // "Beginner", "Intermediate", "Advanced"
    private String description;

    public Skill() {}

    public Skill(String skillName, String category, String type, String level) {
        this.skillName = skillName;
        this.category = category;
        this.type = type;
        this.level = level;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
