package com.yashsoni.skillbarter.repository;

import android.content.Context;
import com.yashsoni.skillbarter.api.ApiClient;
import com.yashsoni.skillbarter.api.ApiService;
import com.yashsoni.skillbarter.data.model.Availability;
import com.yashsoni.skillbarter.data.model.Badge;
import com.yashsoni.skillbarter.data.model.Conversation;
import com.yashsoni.skillbarter.data.model.Credit;
import com.yashsoni.skillbarter.data.model.ExchangeRelationship;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.ExchangeSummary;
import com.yashsoni.skillbarter.data.model.HelpRequest;
import com.yashsoni.skillbarter.data.model.MatchResult;
import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.Notification;
import com.yashsoni.skillbarter.data.model.Progress;
import com.yashsoni.skillbarter.data.model.Review;
import com.yashsoni.skillbarter.data.model.Session;
import com.yashsoni.skillbarter.data.model.SkillListing;
import com.yashsoni.skillbarter.data.model.Stats;
import com.yashsoni.skillbarter.data.model.User;
import com.yashsoni.skillbarter.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkillBarterRepository {
    private final ApiService apiService;
    private final SessionManager sessionManager;

    private static SkillBarterRepository instance;

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

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

    public void fetchLiveStats(DataCallback<Stats> callback) {
        apiService.getStats().enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Stats> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchRecommendedUsers(DataCallback<List<User>> callback) {
        apiService.getRecommendedPartners().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchDiscoverMatches(String skill, String category, String location, DataCallback<List<MatchResult>> callback) {
        apiService.getDiscoverUsers(skill, category, location).enqueue(new Callback<List<MatchResult>>() {
            @Override
            public void onResponse(Call<List<MatchResult>> call, Response<List<MatchResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Server error (HTTP " + response.code() + "). Is the backend running?");
                }
            }

            @Override
            public void onFailure(Call<List<MatchResult>> call, Throwable t) {
                String detail = t.getMessage();
                callback.onError("Cannot reach backend: " + (detail != null ? detail : "unknown network error"));
            }
        });
    }

    public void fetchIncomingRequests(DataCallback<List<ExchangeRequest>> callback) {
        apiService.getIncomingRequests().enqueue(new Callback<List<ExchangeRequest>>() {
            @Override
            public void onResponse(Call<List<ExchangeRequest>> call, Response<List<ExchangeRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<ExchangeRequest>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchOutgoingRequests(DataCallback<List<ExchangeRequest>> callback) {
        apiService.getOutgoingRequests().enqueue(new Callback<List<ExchangeRequest>>() {
            @Override
            public void onResponse(Call<List<ExchangeRequest>> call, Response<List<ExchangeRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<ExchangeRequest>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void sendExchangeRequest(String receiverId, String offeredSkill, String requestedSkill, String message, DataCallback<ExchangeRequest> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("receiverId", receiverId);
        body.put("offeredSkill", offeredSkill);
        body.put("requestedSkill", requestedSkill);
        body.put("message", message);

        apiService.sendRequest(body).enqueue(new Callback<ExchangeRequest>() {
            @Override
            public void onResponse(Call<ExchangeRequest> call, Response<ExchangeRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ExchangeRequest> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void acceptRequestApi(String requestId, DataCallback<ExchangeRequest> callback) {
        apiService.acceptRequest(requestId).enqueue(new Callback<ExchangeRequest>() {
            @Override
            public void onResponse(Call<ExchangeRequest> call, Response<ExchangeRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ExchangeRequest> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void rejectRequestApi(String requestId, DataCallback<ExchangeRequest> callback) {
        apiService.rejectRequest(requestId).enqueue(new Callback<ExchangeRequest>() {
            @Override
            public void onResponse(Call<ExchangeRequest> call, Response<ExchangeRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ExchangeRequest> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    private String httpError(int code) {
        if (code == 401) {
            return "Session expired. Please log in again.";
        }
        return "Server error (HTTP " + code + "). Please try again.";
    }

    /**
     * Prefers the server's own `message` over the generic HTTP text. The backend
     * explains *why* a call was refused (duplicate request, not enough credits,
     * exchange already completed) and that text is what the user needs to see.
     */
    private String errorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                com.google.gson.JsonObject body = new com.google.gson.Gson()
                        .fromJson(response.errorBody().string(), com.google.gson.JsonObject.class);
                if (body != null && body.has("message") && !body.get("message").isJsonNull()) {
                    return body.get("message").getAsString();
                }
            }
        } catch (Exception ignored) {
            // Not a JSON error body; fall through to the generic message.
        }
        return httpError(response.code());
    }

    private String networkError(Throwable t) {
        String detail = t.getMessage();
        return "Cannot reach backend: " + (detail != null ? detail : "unknown network error");
    }

    public void fetchMarketplaceListings(DataCallback<List<SkillListing>> callback) {
        apiService.getMarketplaceListings().enqueue(new Callback<List<SkillListing>>() {
            @Override
            public void onResponse(Call<List<SkillListing>> call, Response<List<SkillListing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<SkillListing>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchHelpRequests(DataCallback<List<HelpRequest>> callback) {
        apiService.getHelpRequests().enqueue(new Callback<List<HelpRequest>>() {
            @Override
            public void onResponse(Call<List<HelpRequest>> call, Response<List<HelpRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<HelpRequest>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchBadges(DataCallback<List<Badge>> callback) {
        apiService.getBadges().enqueue(new Callback<List<Badge>>() {
            @Override
            public void onResponse(Call<List<Badge>> call, Response<List<Badge>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Badge>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchMessages(String partnerId, DataCallback<List<Message>> callback) {
        apiService.getMessages(partnerId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void sendMessageApi(String partnerId, String text, DataCallback<Message> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("receiverId", partnerId);
        body.put("message", text);

        apiService.sendMessage(body).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    /**
     * Records a finished session with a real duration so the progress dashboard
     * shows actual hours instead of placeholders.
     */
    /**
     * Records a finished session with a real duration. The backend also moves the
     * credit balance for both sides, so the response carries the new balance and
     * the change that was applied.
     */
    public void completeSession(String requestId, double durationHours, String skill, boolean iTaught, SessionCallback callback) {
        Map<String, Object> body = new HashMap<>();
        if (requestId != null) body.put("requestId", requestId);
        body.put("durationHours", durationHours);
        body.put("skill", skill);
        body.put("iTaught", iTaught);

        apiService.completeSession(body).enqueue(new Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError(errorMessage(response));
                    return;
                }
                com.google.gson.JsonObject body = response.body();
                Session session = new com.google.gson.Gson().fromJson(body.get("session"), Session.class);
                callback.onSuccess(session, readInt(body, "balance"), readInt(body, "change"));
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    private int readInt(com.google.gson.JsonObject body, String key) {
        try {
            return body.has(key) && !body.get(key).isJsonNull() ? body.get(key).getAsInt() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Session completion also reports the credit movement it caused. */
    public interface SessionCallback {
        void onSuccess(Session session, int creditBalance, int creditChange);
        void onError(String message);
    }

    public void fetchConversations(DataCallback<List<Conversation>> callback) {        apiService.getConversations().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchProgress(DataCallback<Progress> callback) {
        apiService.getProgress().enqueue(new Callback<Progress>() {
            @Override
            public void onResponse(Call<Progress> call, Response<Progress> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Progress> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchMyAvailability(DataCallback<List<Availability>> callback) {
        apiService.getMyAvailability().enqueue(new Callback<List<Availability>>() {
            @Override
            public void onResponse(Call<List<Availability>> call, Response<List<Availability>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Availability>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void addAvailability(String day, String startTime, String endTime, String mode, DataCallback<Availability> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("day", day);
        body.put("startTime", startTime);
        body.put("endTime", endTime);
        body.put("mode", mode);

        apiService.addAvailability(body).enqueue(new Callback<Availability>() {
            @Override
            public void onResponse(Call<Availability> call, Response<Availability> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Availability> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void deleteAvailability(String slotId, DataCallback<com.google.gson.JsonObject> callback) {
        apiService.deleteAvailability(slotId).enqueue(new Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchCredits(DataCallback<Credit> callback) {
        apiService.getCredits().enqueue(new Callback<Credit>() {
            @Override
            public void onResponse(Call<Credit> call, Response<Credit> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Credit> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchNotifications(DataCallback<List<Notification>> callback) {
        apiService.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    /** Unread count for the red dot on the bell. */
    public void fetchUnreadCount(DataCallback<Integer> callback) {
        apiService.getUnreadNotificationCount().enqueue(new Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(readInt(response.body(), "count"));
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void markNotificationsRead(DataCallback<com.google.gson.JsonObject> callback) {
        apiService.markNotificationsRead().enqueue(new Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    /**
     * Everyone the user has an accepted or completed exchange with, used by the
     * "My Exchanges" screen to show who they traded skills with.
     */
    public void fetchMyExchanges(DataCallback<List<ExchangeSummary>> callback) {
        apiService.getMyExchanges().enqueue(new Callback<List<ExchangeSummary>>() {
            @Override
            public void onResponse(Call<List<ExchangeSummary>> call, Response<List<ExchangeSummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<ExchangeSummary>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    /**
     * Sends a review. The backend only accepts it once the exchange is complete,
     * so the failure text explains exactly that if it is too early.
     */
    public void submitReview(String userId, int rating, String comment, DataCallback<Review> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("reviewedUserId", userId);
        body.put("rating", rating);
        body.put("comment", comment);

        apiService.addReview(body).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    /** Partner ids the user has already reviewed, so the UI can show Rated. */
    public void fetchSessions(DataCallback<List<Session>> callback) {
        apiService.getSessions().enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    /**
     * Checks whether a live exchange already exists with one member. Used before
     * showing the send-request form so a second request is never offered for a
     * pair that is already chatting.
     */
    public void fetchRelationship(String userId, DataCallback<ExchangeRelationship> callback) {
        apiService.getRelationshipWith(userId).enqueue(new Callback<com.google.gson.JsonObject>() {
            @Override
            public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRelationship relationship = new com.google.gson.Gson()
                            .fromJson(response.body(), ExchangeRelationship.class);
                    callback.onSuccess(relationship);
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<com.google.gson.JsonObject> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }

    public void fetchMyReviews(DataCallback<List<Review>> callback) {
        apiService.getMyReviews().enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                callback.onError(networkError(t));
            }
        });
    }
}
