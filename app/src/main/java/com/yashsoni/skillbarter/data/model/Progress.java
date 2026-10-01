package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Real learning/teaching numbers from GET /users/progress. */
public class Progress implements Serializable {

    @SerializedName(value = "taughtHours", alternate = { "taughthours" })
    private double taughtHours;

    @SerializedName(value = "learnedHours", alternate = { "learnedhours" })
    private double learnedHours;

    private int sessionsTaught;
    private int sessionsLearned;
    private int completedExchanges;
    private List<SkillProgress> skills = new ArrayList<>();
    private List<String> offeredSkills = new ArrayList<>();
    private List<String> wantedSkills = new ArrayList<>();

    public Progress() {}

    public double getTaughtHours() { return taughtHours; }
    public void setTaughtHours(double taughtHours) { this.taughtHours = taughtHours; }

    public double getLearnedHours() { return learnedHours; }
    public void setLearnedHours(double learnedHours) { this.learnedHours = learnedHours; }

    public int getSessionsTaught() { return sessionsTaught; }
    public void setSessionsTaught(int sessionsTaught) { this.sessionsTaught = sessionsTaught; }

    public int getSessionsLearned() { return sessionsLearned; }
    public void setSessionsLearned(int sessionsLearned) { this.sessionsLearned = sessionsLearned; }

    public int getCompletedExchanges() { return completedExchanges; }
    public void setCompletedExchanges(int completedExchanges) { this.completedExchanges = completedExchanges; }

    public List<SkillProgress> getSkills() { return skills; }
    public void setSkills(List<SkillProgress> skills) { this.skills = skills; }

    public List<String> getOfferedSkills() { return offeredSkills; }
    public void setOfferedSkills(List<String> offeredSkills) { this.offeredSkills = offeredSkills; }

    public List<String> getWantedSkills() { return wantedSkills; }
    public void setWantedSkills(List<String> wantedSkills) { this.wantedSkills = wantedSkills; }

    /** One skill the user is learning and how far along they are. */
    public static class SkillProgress implements Serializable {
        private String name;
        private int completed;
        private int target;
        private int percent;

        public SkillProgress() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getCompleted() { return completed; }
        public void setCompleted(int completed) { this.completed = completed; }

        public int getTarget() { return target; }
        public void setTarget(int target) { this.target = target; }

        public int getPercent() { return percent; }
        public void setPercent(int percent) { this.percent = percent; }
    }
}
