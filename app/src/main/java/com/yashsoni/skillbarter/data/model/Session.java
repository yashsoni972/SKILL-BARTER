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
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String meetLink;
    private String location;
    private String notes;
    private String status;

    public Session() {}

    /**
     * "Wednesday, 10:00 - 11:00", falling back to whatever the backend actually
     * stored so an older session without the new fields still renders sensibly.
     */
    public String getWhenLabel() {
        String day = dayOfWeek != null && !dayOfWeek.isEmpty() ? dayOfWeek : "";
        String start = startTime != null && !startTime.isEmpty() ? startTime : time;
        String end = endTime != null && !endTime.isEmpty() ? endTime : "";

        if (start.isEmpty() && end.isEmpty()) return date;

        String range = start + (end.isEmpty() ? "" : " - " + end);
        return day.isEmpty() ? range : day + ", " + range;
    }

    /** Never returns an empty string, so the join button always opens something real. */
    public String getMeetLink() {
        return meetLink != null && !meetLink.trim().isEmpty()
                ? meetLink
                : "https://meet.google.com/new";
    }

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

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
