package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ExchangeRequest implements Serializable {
    @SerializedName("_id")
    private String id;
    private User senderId;
    private User receiverId;
    private String offeredSkill;
    private String requestedSkill;
    private String message;
    private String status; // "pending", "accepted", "rejected", "completed"
    private String createdAt;

    public ExchangeRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getSenderId() { return senderId; }
    public void setSenderId(User senderId) { this.senderId = senderId; }

    public User getReceiverId() { return receiverId; }
    public void setReceiverId(User receiverId) { this.receiverId = receiverId; }

    public String getOfferedSkill() { return offeredSkill; }
    public void setOfferedSkill(String offeredSkill) { this.offeredSkill = offeredSkill; }

    public String getRequestedSkill() { return requestedSkill; }
    public void setRequestedSkill(String requestedSkill) { this.requestedSkill = requestedSkill; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
