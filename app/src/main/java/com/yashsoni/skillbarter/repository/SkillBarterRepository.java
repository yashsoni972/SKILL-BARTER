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
    private int totalUsersCount = 128;

    private SkillBarterRepository(Context context) {
        this.apiService = ApiClient.getService(context);
        this.sessionManager = new SessionManager(context);
        initMockData();
    }

    public static synchronized SkillBarterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SkillBarterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private void initMockData() {
        // User 1: Riya Sharma
        User u1 = new User("u1", "Riya Sharma", "riya@example.com", "Ahmedabad",
                "UI/UX Designer & Graphic Artist passionate about creating intuitive user interfaces.", 4.8, 5);
        u1.setOfferedSkills(Arrays.asList("Graphic Design", "UI/UX"));
        u1.setWantedSkills(Arrays.asList("HTML & CSS", "JavaScript"));

        // User 2: Aman Patel
        User u2 = new User("u2", "Aman Patel", "aman@example.com", "Vadodara",
                "Python Developer with experience in data processing and backend architecture.", 4.5, 2);
        u2.setOfferedSkills(Arrays.asList("Python", "Data Analysis"));
        u2.setWantedSkills(Arrays.asList("Web Development", "HTML & CSS"));

        // User 3: Neha Patel
        User u3 = new User("u3", "Neha Patel", "neha@example.com", "Vadodara",
                "Creative illustrator and brand visual designer.", 4.9, 7);
        u3.setOfferedSkills(Arrays.asList("Graphic Design", "Illustration"));
        u3.setWantedSkills(Arrays.asList("Python", "SEO"));

        // User 4: Karan Mehta
        User u4 = new User("u4", "Karan Mehta", "karan@example.com", "Surat",
                "Photographer & Video Editor specializing in Premiere Pro & After Effects.", 4.6, 4);
        u4.setOfferedSkills(Arrays.asList("Photoshop", "Video Editing"));
        u4.setWantedSkills(Arrays.asList("JavaScript", "React"));

        // User 5: Isha Desai
        User u5 = new User("u5", "Isha Desai", "isha@example.com", "Anand",
                "Branding expert & UI Designer.", 4.7, 3);
        u5.setOfferedSkills(Arrays.asList("Graphic Design", "Branding"));
        u5.setWantedSkills(Arrays.asList("English Speaking", "Public Speaking"));

        mockUsers.add(u1);
        mockUsers.add(u2);
        mockUsers.add(u3);
        mockUsers.add(u4);
        mockUsers.add(u5);

        // Incoming Request 1
        ExchangeRequest req1 = new ExchangeRequest();
        req1.setId("req_1");
        req1.setSenderId(u1);
        req1.setReceiverId(sessionManager.getUser());
        req1.setOfferedSkill("Graphic Design");
        req1.setRequestedSkill("JavaScript");
        req1.setMessage("Hi Yash! I'm interested in exchanging skills. I can teach you Graphic Design in exchange for JavaScript.");
        req1.setStatus("pending");
        req1.setCreatedAt("Today, 10:24 AM");

        // Incoming Request 2
        ExchangeRequest req2 = new ExchangeRequest();
        req2.setId("req_2");
        req2.setSenderId(u2);
        req2.setReceiverId(sessionManager.getUser());
        req2.setOfferedSkill("Python");
        req2.setRequestedSkill("HTML & CSS");
        req2.setMessage("Hello! Would love to learn web basics from you while teaching Python.");
        req2.setStatus("pending");
        req2.setCreatedAt("Yesterday");

        mockIncomingRequests.add(req1);
        mockIncomingRequests.add(req2);

        // Messages with Riya
        mockMessages.add(new Message("u1", "curr_user", "Hi Yash! I'm interested in exchanging skills.", "10:24 AM"));
        mockMessages.add(new Message("curr_user", "u1", "Great! I can teach you HTML & CSS. What do you want to learn?", "10:26 AM"));
        mockMessages.add(new Message("u1", "curr_user", "I want to learn Graphic Design. Can we schedule a session?", "10:28 AM"));
        mockMessages.add(new Message("curr_user", "u1", "Sure! Let's plan for this weekend.", "10:30 AM"));
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

    public void registerUser(User newUser) {
        if (newUser != null) {
            mockUsers.add(newUser);
            totalUsersCount++;
        }
    }

    public Stats getStats() {
        return new Stats(totalUsersCount, 46, 31);
    }
}
