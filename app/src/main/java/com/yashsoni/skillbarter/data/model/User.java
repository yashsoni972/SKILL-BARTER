package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private String email;
    private String location;
    private String bio;
    private String profileImage;
    private double rating;
    private int totalExchanges;

    private List<String> offeredSkills = new ArrayList<>();
    private List<String> wantedSkills = new ArrayList<>();

    public User() {}

    public User(String id, String name, String email, String location, String bio, double rating, int totalExchanges) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.bio = bio;
        this.rating = rating;
        this.totalExchanges = totalExchanges;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalExchanges() { return totalExchanges; }
    public void setTotalExchanges(int totalExchanges) { this.totalExchanges = totalExchanges; }

    public List<String> getOfferedSkills() { return offeredSkills; }
    public void setOfferedSkills(List<String> offeredSkills) { this.offeredSkills = offeredSkills; }

    public List<String> getWantedSkills() { return wantedSkills; }
    public void setWantedSkills(List<String> wantedSkills) { this.wantedSkills = wantedSkills; }
}
