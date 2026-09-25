package com.yashsoni.skillbarter.repository;

import android.content.Context;
import com.yashsoni.skillbarter.api.ApiClient;
import com.yashsoni.skillbarter.api.ApiService;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.Stats;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillBarterRepository {
    private final ApiService apiService;
    private final SessionManager sessionManager;

    private static SkillBarterRepository instance;
    private final List<User> mockUsers = new ArrayList<>();
    private final List<ExchangeRequest> mockIncomingRequests = new ArrayList<>();
    private final List<ExchangeRequest> mockOutgoingRequests = new ArrayList<>();
    private final List<Message> mockMessages = new ArrayList<>();
    private int totalUsersCount = 0;

    private SkillBarterRepository(Context context) {
        this.apiService = ApiClient.getService(context);
        this.sessionManager = new SessionManager(context);
    }

    public static synchronized SkillBarterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SkillBarterRepository(context.getApplicationContext());
        }
        return instance;
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
                mockUsers.add(newUser);
                totalUsersCount = mockUsers.size();
            }
        }
    }

    public Stats getStats() {
        int active = mockOutgoingRequests.size() + mockIncomingRequests.size();
        return new Stats(totalUsersCount > 0 ? totalUsersCount : mockUsers.size(), active, 0);
    }

    public List<User> getRecommendedUsers() {
        return new ArrayList<>(mockUsers);
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(mockUsers);
        }
        String q = query.toLowerCase().trim();
        List<User> results = new ArrayList<>();
        for (User u : mockUsers) {
            boolean matchesSkill = false;
            for (String s : u.getOfferedSkills()) {
                if (s.toLowerCase().contains(q)) { matchesSkill = true; break; }
            }
            for (String s : u.getWantedSkills()) {
                if (s.toLowerCase().contains(q)) { matchesSkill = true; break; }
            }
            if (u.getName().toLowerCase().contains(q) || u.getLocation().toLowerCase().contains(q) || matchesSkill) {
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
