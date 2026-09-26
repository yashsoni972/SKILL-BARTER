package com.yashsoni.skillbarter.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String email;
    private String location;
    private String bio;
    private double rating;
    private int totalExchanges;
    private String profileImage;

    public UserEntity(@NonNull String id, String name, String email, String location, String bio, double rating, int totalExchanges, String profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.bio = bio;
        this.rating = rating;
        this.totalExchanges = totalExchanges;
        this.profileImage = profileImage;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

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

    public int getTotalExchanges() { return totalExchanges; }
    public void setTotalExchanges(int totalExchanges) { this.totalExchanges = totalExchanges; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
