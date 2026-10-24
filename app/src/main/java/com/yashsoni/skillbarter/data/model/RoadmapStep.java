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

    /** "1–2 weeks", "3–5 days", etc. */
    @SerializedName("estimatedTime")
    private String estimatedTime;

    /** One small concrete practice task for this step. */
    @SerializedName("miniTask")
    private String miniTask;

    /** YouTube search query string for this step. */
    @SerializedName("youtubeSearch")
    private String youtubeSearch;

    /** "Label — URL" strings shown as bullet links. */
    @SerializedName("resources")
    private List<String> resources;

    public String getTitle()                     { return title; }
    public void   setTitle(String v)             { this.title = v; }

    public String getDescription()               { return description; }
    public void   setDescription(String v)       { this.description = v; }

    public boolean isCompleted()                 { return completed; }
    public void    setCompleted(boolean v)       { this.completed = v; }

    public String getEstimatedTime()             { return estimatedTime; }
    public void   setEstimatedTime(String v)     { this.estimatedTime = v; }

    public String getMiniTask()                  { return miniTask; }
    public void   setMiniTask(String v)          { this.miniTask = v; }

    public String getYoutubeSearch()             { return youtubeSearch; }
    public void   setYoutubeSearch(String v)     { this.youtubeSearch = v; }

    public List<String> getResources()           { return resources; }
    public void         setResources(List<String> v) { this.resources = v; }
}
