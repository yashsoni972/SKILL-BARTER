package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CreditTransaction implements Serializable {

    @SerializedName("_id")
    private String id;
    private String userId;
    private int amount;
    private String type;
    private String description;
    private String createdAt;

    public CreditTransaction() {}

    public CreditTransaction(int amount, String type, String description, String createdAt) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
