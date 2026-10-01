package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * The existing exchange between the logged-in member and one other member, from
 * GET /requests/with/:userId. Lets the app replace "Send Request" with "Chat" or
 * "Awaiting reply" instead of offering a form the server will reject.
 */
public class ExchangeRelationship implements Serializable {

    private String status;
    private String direction;

    @SerializedName("requestId")
    private String requestId;

    private boolean canChat;
    private boolean canRequest;

    public ExchangeRelationship() {}

    public String getStatus() { return status; }
    public String getDirection() { return direction; }
    public String getRequestId() { return requestId; }
    public boolean canChat() { return canChat; }
    public boolean canRequest() { return canRequest; }

    public boolean isPending() { return "pending".equals(status); }
    public boolean isNone() { return status == null || "none".equals(status); }
}