package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Badge implements Serializable {

    @SerializedName("_id")
    private String id;
    private String title;
    private String icon;
    private String description;
    private String category;

    public Badge() {}

    public Badge(String id, String title, String icon, String description) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
