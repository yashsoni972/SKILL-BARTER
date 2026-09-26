package com.yashsoni.skillbarter.repository;

import android.content.Context;
import com.yashsoni.skillbarter.api.ApiClient;
import com.yashsoni.skillbarter.api.ApiService;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.MatchResult;
import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.Skill;
import com.yashsoni.skillbarter.data.model.Stats;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.utils.SessionManager;
import com.yashsoni.skillbarter.utils.SkillMatchEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkillBarterRepository {
    private final ApiService apiService;
    private final SessionManager sessionManager;

    private static SkillBarterRepository instance;
    private final List<User> mockUsers = new ArrayList<>();
    private final List<ExchangeRequest> mockIncomingRequests = new ArrayList<>();
    private final List<ExchangeRequest> mockOutgoingRequests = new ArrayList<>();
    private final List<Message> mockMessages = new ArrayList<>();

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    private SkillBarterRepository(Context context) {
        this.apiService = ApiClient.getService(context);
        this.sessionManager = new SessionManager(context);
        initCommunityMembers();
    }

    public static synchronized SkillBarterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SkillBarterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private void initCommunityMembers() {
        // Active Community Members
        User u1 = new User("u1", "Riya Sharma", "riya@example.com", "Ahmedabad",
                "UI/UX Designer & Graphic Artist passionate about creating intuitive user interfaces.", 4.8, 5);
        u1.setOfferedSkills(Arrays.asList("Graphic Design", "UI/UX"));
        u1.setWantedSkills(Arrays.asList("HTML & CSS", "JavaScript"));
        u1.setProfileImage("avatar_4");

        User u2 = new User("u2", "Aman Patel", "aman@example.com", "Vadodara",
                "Python Developer with experience in data processing and backend architecture.", 4.5, 2);
        u2.setOfferedSkills(Arrays.asList("Python", "Data Analysis"));
        u2.setWantedSkills(Arrays.asList("Web Development", "HTML & CSS"));
        u2.setProfileImage("avatar_1");

        User u3 = new User("u3", "Neha Patel", "neha@example.com", "Vadodara",
                "Creative illustrator and brand visual designer.", 4.9, 7);
        u3.setOfferedSkills(Arrays.asList("Graphic Design", "Illustration"));
        u3.setWantedSkills(Arrays.asList("Python", "SEO"));
        u3.setProfileImage("avatar_2");

        User u4 = new User("u4", "Karan Mehta", "karan@example.com", "Surat",
                "Photographer & Video Editor specializing in Premiere Pro & After Effects.", 4.6, 4);
        u4.setOfferedSkills(Arrays.asList("Photoshop", "Video Editing"));
        u4.setWantedSkills(Arrays.asList("JavaScript", "React"));
        u4.setProfileImage("avatar_3");

        User u5 = new User("u5", "Isha Desai", "isha@example.com", "Anand",
                "Branding expert & UI Designer.", 4.7, 3);
        u5.setOfferedSkills(Arrays.asList("Graphic Design", "Branding"));
        u5.setWantedSkills(Arrays.asList("English Speaking", "Public Speaking"));
        u5.setProfileImage("avatar_5");

        mockUsers.add(u1);
        mockUsers.add(u2);
        mockUsers.add(u3);
        mockUsers.add(u4);
        mockUsers.add(u5);

        // Messages with Riya
        mockMessages.add(new Message("u1", "curr_user", "Hi! I'm interested in exchanging skills.", "10:24 AM"));
        mockMessages.add(new Message("curr_user", "u1", "Great! I can teach you HTML & CSS. What do you want to learn?", "10:26 AM"));
        mockMessages.add(new Message("u1", "curr_user", "I want to learn Graphic Design. Can we schedule a session?", "10:28 AM"));
    }

    public void registerUser(User newUser) {
        if (newUser != null) {
            boolean exists = false;
            for (User u : mockUsers) {
                if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                mockUsers.add(0, newUser);
            }
        }
    }

    public void fetchLiveStats(DataCallback<Stats> callback) {
        apiService.getStats().enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(getStats());
                }
            }

            @Override
            public void onFailure(Call<Stats> call, Throwable t) {
                callback.onSuccess(getStats());
            }
        });
    }

    public Stats getStats() {
        int total = Math.max(128, mockUsers.size() + 120);
        int active = mockOutgoingRequests.size() + mockIncomingRequests.size() + 46;
        return new Stats(total, active, 31);
    }

    public void fetchRecommendedUsers(DataCallback<List<User>> callback) {
        apiService.getRecommendedPartners().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(getRecommendedUsers());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onSuccess(getRecommendedUsers());
            }
        });
    }

    public void fetchDiscoverMatches(String skill, String category, String location, DataCallback<List<MatchResult>> callback) {
        apiService.getDiscoverUsers(skill, category, location).enqueue(new Callback<List<MatchResult>>() {
            @Override
            public void onResponse(Call<List<MatchResult>> call, Response<List<MatchResult>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(getMatchResults(skill));
                }
            }

            @Override
            public void onFailure(Call<List<MatchResult>> call, Throwable t) {
                callback.onSuccess(getMatchResults(skill));
            }
        });
    }

    public List<MatchResult> getMatchResults(String query) {
        User currentUser = sessionManager.getUser();
        List<User> searchList = searchUsers(query);
        List<MatchResult> results = new ArrayList<>();

        for (User u : searchList) {
            MatchResult res = new MatchResult();
            res.setId(u.getId());
            res.setName(u.getName());
            res.setEmail(u.getEmail());
            res.setLocation(u.getLocation());
            res.setBio(u.getBio());
            res.setRating(u.getRating());
            res.setProfileImage(u.getProfileImage());

            List<Skill> offerSkills = new ArrayList<>();
            if (u.getOfferedSkills() != null) {
                for (String s : u.getOfferedSkills()) { offerSkills.add(new Skill(s, "General", "offer", "Intermediate")); }
            }
            res.setOffers(offerSkills);

            List<Skill> wantSkills = new ArrayList<>();
            if (u.getWantedSkills() != null) {
                for (String s : u.getWantedSkills()) { wantSkills.add(new Skill(s, "General", "want", "Beginner")); }
            }
            res.setWants(wantSkills);

            int matchPct = SkillMatchEngine.calculateMatchPercentage(currentUser, u);
            res.setMatchPercentage(matchPct);

            results.add(res);
        }

        results.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return results;
    }

    public List<User> getRecommendedUsers() {
        User curr = sessionManager.getUser();
        List<User> list = new ArrayList<>();
        for (User u : mockUsers) {
            if (curr != null && curr.getEmail() != null && curr.getEmail().equalsIgnoreCase(u.getEmail())) {
                continue;
            }
            list.add(u);
        }
        return list;
    }

    public List<User> searchUsers(String query) {
        List<User> base = getRecommendedUsers();
        if (query == null || query.trim().isEmpty()) {
            return base;
        }
        String q = query.toLowerCase().trim();
        List<User> results = new ArrayList<>();
        for (User u : base) {
            boolean matchesSkill = false;
            if (u.getOfferedSkills() != null) {
                for (String s : u.getOfferedSkills()) {
                    if (s.toLowerCase().contains(q)) { matchesSkill = true; break; }
                }
            }
            if (u.getWantedSkills() != null) {
                for (String s : u.getWantedSkills()) {
                    if (s.toLowerCase().contains(q)) { matchesSkill = true; break; }
                }
            }
            if (u.getName().toLowerCase().contains(q) || (u.getLocation() != null && u.getLocation().toLowerCase().contains(q)) || matchesSkill) {
                results.add(u);
            }
        }
        return results;
    }

    public List<ExchangeRequest> getIncomingRequests() {
        return new ArrayList<>(mockIncomingRequests);
    }

    public List<ExchangeRequest> getOutgoingRequests() {
        return new ArrayList<>(mockOutgoingRequests);
    }

    public void addOutgoingRequest(User partner, String offeredSkill, String requestedSkill, String msg) {
        ExchangeRequest req = new ExchangeRequest();
        req.setId("req_" + System.currentTimeMillis());
        req.setSenderId(sessionManager.getUser());
        req.setReceiverId(partner);
        req.setOfferedSkill(offeredSkill);
        req.setRequestedSkill(requestedSkill);
        req.setMessage(msg);
        req.setStatus("pending");
        req.setCreatedAt("Just now");
        mockOutgoingRequests.add(req);
    }

    public void acceptRequest(String requestId) {
        for (ExchangeRequest r : mockIncomingRequests) {
            if (r.getId().equals(requestId)) {
                r.setStatus("accepted");
                break;
            }
        }
    }

    public void rejectRequest(String requestId) {
        mockIncomingRequests.removeIf(r -> r.getId().equals(requestId));
    }

    public List<Message> getMessagesForPartner(String partnerId) {
        return new ArrayList<>(mockMessages);
    }

    public void sendMessage(String partnerId, String text) {
        mockMessages.add(new Message("curr_user", partnerId, text, "Just now"));
    }
}
