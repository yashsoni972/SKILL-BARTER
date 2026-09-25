package com.yashsoni.skillbarter.utils;

import com.yashsoni.skillbarter.data.model.User;
import java.util.List;

public class SkillMatchEngine {

    public static int calculateMatchPercentage(User currentUser, User targetUser) {
        if (currentUser == null || targetUser == null) return 0;

        List<String> myWants = currentUser.getWantedSkills();
        List<String> partnerOffers = targetUser.getOfferedSkills();

        List<String> myOffers = currentUser.getOfferedSkills();
        List<String> partnerWants = targetUser.getWantedSkills();

        int matches = 0;
        if (myWants != null && partnerOffers != null) {
            for (String want : myWants) {
                for (String offer : partnerOffers) {
                    if (want.equalsIgnoreCase(offer)) {
                        matches++;
                        break;
                    }
                }
            }
        }

        if (myOffers != null && partnerWants != null) {
            for (String offer : myOffers) {
                for (String want : partnerWants) {
                    if (offer.equalsIgnoreCase(want)) {
                        matches++;
                        break;
                    }
                }
            }
        }

        if (matches >= 2) return 100;
        if (matches == 1) return 75;
        return 50;
    }

    public static String getMatchBadgeText(int percentage) {
        if (percentage >= 100) return "🎯 100% Mutual Skill Match!";
        if (percentage >= 75) return "🎯 High Compatibility";
        return "✨ Potential Skill Partner";
    }
}
