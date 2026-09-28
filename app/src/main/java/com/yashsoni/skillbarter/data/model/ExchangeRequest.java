package com.yashsoni.skillbarter.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.lang.reflect.Type;

public class ExchangeRequest implements Serializable {
    @SerializedName("_id")
    private String id;

    @JsonAdapter(UserRef.class)
    private User senderId;

    @JsonAdapter(UserRef.class)
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

    public static class UserRef implements JsonDeserializer<User>, JsonSerializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }
            if (json.isJsonPrimitive()) {
                User user = new User();
                user.setId(json.getAsString());
                return user;
            }
            return context.deserialize(json, User.class);
        }

        @Override
        public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(user, User.class);
        }
    }
}
