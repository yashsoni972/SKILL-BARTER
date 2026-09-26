package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class HelpRequest implements Serializable {

    @SerializedName("_id")
    private String id;
    private User userId;
    private String skillName;
    private String title;
    private String description;
    private String level;
    private String mode;
    private String status;
    private String createdAt;

    public HelpRequest() {}

    public HelpRequest(String skillName, String title, String description, String level, String mode) {
        this.skillName = skillName;
        this.title = title;
        this.description = description;
        this.level = level;
        this.mode = mode;
        this.status = "open";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUserId() { return userId; }
    public void setUserId(User userId) { this.userId = userId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
