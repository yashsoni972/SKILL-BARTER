package com.yashsoni.skillbarter.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Credit implements Serializable {

    @SerializedName("balance")
    private int balance;

    @SerializedName("earnedTotal")
    private int earnedTotal;

    @SerializedName("spentTotal")
    private int spentTotal;

    @SerializedName("history")
    private List<CreditTransaction> history = new ArrayList<>();

    public Credit() {}

    public Credit(int balance, int earnedTotal, int spentTotal) {
        this.balance = balance;
        this.earnedTotal = earnedTotal;
        this.spentTotal = spentTotal;
    }

    public int getBalance()                           { return balance; }
    public void setBalance(int balance)               { this.balance = balance; }

    public int getEarnedTotal()                       { return earnedTotal; }
    public void setEarnedTotal(int earnedTotal)       { this.earnedTotal = earnedTotal; }

    public int getSpentTotal()                        { return spentTotal; }
    public void setSpentTotal(int spentTotal)         { this.spentTotal = spentTotal; }

    public List<CreditTransaction> getHistory()       { return history; }
    public void setHistory(List<CreditTransaction> h) { this.history = h; }
}
