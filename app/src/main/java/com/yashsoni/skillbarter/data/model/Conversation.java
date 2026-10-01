package com.yashsoni.skillbarter.data.model;

import java.io.Serializable;

/** One row in the Chats tab: a partner you have an accepted exchange with. */
public class Conversation implements Serializable {

    private User partner;
    private String lastMessage;
    private String lastMessageAt;
    private boolean lastMessageMine;
    private long unreadCount;
    private String requestId;
    private String status;

    public Conversation() {}

    public User getPartner() { return partner; }
    public void setPartner(User partner) { this.partner = partner; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(String lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public boolean isLastMessageMine() { return lastMessageMine; }
    public void setLastMessageMine(boolean lastMessageMine) { this.lastMessageMine = lastMessageMine; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
