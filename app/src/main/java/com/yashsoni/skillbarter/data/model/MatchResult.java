package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Serializable {

    @SerializedName("_id")
    private String id;
    private String name;
    private String email;
    private String location;
    private String bio;
    private double rating;
    private String profileImage;

    private List<Skill> offers = new ArrayList<>();
    private List<Skill> wants = new ArrayList<>();

    private int matchPercentage;

    public MatchResult() {}

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

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public List<Skill> getOffers() { return offers; }
    public void setOffers(List<Skill> offers) { this.offers = offers; }

    public List<Skill> getWants() { return wants; }
    public void setWants(List<Skill> wants) { this.wants = wants; }

    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }

    public User toUser() {
        User u = new User(id, name, email, location, bio, rating, 0);
        u.setProfileImage(profileImage);

        List<String> offeredNames = new ArrayList<>();
        if (offers != null) {
            for (Skill s : offers) { offeredNames.add(s.getSkillName()); }
        }
        u.setOfferedSkills(offeredNames);

        List<String> wantedNames = new ArrayList<>();
        if (wants != null) {
            for (Skill s : wants) { wantedNames.add(s.getSkillName()); }
        }
        u.setWantedSkills(wantedNames);

        return u;
    }
}
