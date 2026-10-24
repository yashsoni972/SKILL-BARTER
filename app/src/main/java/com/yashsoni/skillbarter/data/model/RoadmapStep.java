package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class RoadmapStep implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("completed")
    private boolean completed;

    /** Optional list of resource links: "Label|URL" or just "Label" */
    @SerializedName("resources")
    private List<String> resources;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public List<String> getResources() { return resources; }
    public void setResources(List<String> resources) { this.resources = resources; }
}
