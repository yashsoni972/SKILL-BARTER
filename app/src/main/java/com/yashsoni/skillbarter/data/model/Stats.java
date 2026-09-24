package com.yashsoni.skillbarter.data.model;

import java.io.Serializable;

public class Stats implements Serializable {
    private int totalUsers;
    private int activeExchanges;
    private int completedExchanges;

    public Stats() {}

    public Stats(int totalUsers, int activeExchanges, int completedExchanges) {
        this.totalUsers = totalUsers;
        this.activeExchanges = activeExchanges;
        this.completedExchanges = completedExchanges;
    }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getActiveExchanges() { return activeExchanges; }
    public void setActiveExchanges(int activeExchanges) { this.activeExchanges = activeExchanges; }

    public int getCompletedExchanges() { return completedExchanges; }
    public void setCompletedExchanges(int completedExchanges) { this.completedExchanges = completedExchanges; }
}
