package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * One row of the "My Exchanges" list, from GET /requests/exchanges. Holds
 * everything needed to answer "who did I exchange skills with, what did we do,
 * and have I rated them yet" without extra requests.
 */
public class ExchangeSummary implements Serializable {

    @SerializedName("_id")
    private String requestId;

    private String status;
    private String direction;
    private User partner;

    private String taughtSkill;
    private String learnedSkill;

    private String sessionId;
    private String sessionDate;
    private String sessionTime;
    private String sessionLocation;
    private Double durationHours;

    private String createdAt;

    public ExchangeSummary() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public User getPartner() { return partner; }
    public void setPartner(User partner) { this.partner = partner; }

    public String getTaughtSkill() { return taughtSkill; }
    public void setTaughtSkill(String taughtSkill) { this.taughtSkill = taughtSkill; }

    public String getLearnedSkill() { return learnedSkill; }
    public void setLearnedSkill(String learnedSkill) { this.learnedSkill = learnedSkill; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }

    public String getSessionTime() { return sessionTime; }
    public void setSessionTime(String sessionTime) { this.sessionTime = sessionTime; }

    public String getSessionLocation() { return sessionLocation; }
    public void setSessionLocation(String sessionLocation) { this.sessionLocation = sessionLocation; }

    public Double getDurationHours() { return durationHours; }
    public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    /**
     * What the caller offered, in the wording the request screens use. The
     * summary stores this from the caller's point of view, so "taught" is what
     * they offered and "learned" is what they asked for.
     */
    public String getOfferedSkill() {
        return taughtSkill;
    }

    public String getRequestedSkill() {
        return learnedSkill;
    }

    /**
     * "HTML & CSS → Graphic Design" is only useful when both sides are known,
     * so unknown skills are dropped instead of rendering as empty arrows.
     */
    public String getSkillLine() {
        boolean taught = taughtSkill != null && !taughtSkill.trim().isEmpty();
        boolean learned = learnedSkill != null && !learnedSkill.trim().isEmpty();
        if (taught && learned) return taughtSkill + " → " + learnedSkill;
        if (taught) return "Taught: " + taughtSkill;
        if (learned) return "Learned: " + learnedSkill;
        return "Skill exchange";
    }
}