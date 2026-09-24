package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Session implements Serializable {
    @SerializedName("_id")
    private String id;
    private String requestId;
    private User hostUserId;
    private User partnerUserId;
    private String skill;
    private String date;
    private String time;
    private String location;
    private String notes;
    private String status;

    public Session() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public User getHostUserId() { return hostUserId; }
    public void setHostUserId(User hostUserId) { this.hostUserId = hostUserId; }

    public User getPartnerUserId() { return partnerUserId; }
    public void setPartnerUserId(User partnerUserId) { this.partnerUserId = partnerUserId; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
