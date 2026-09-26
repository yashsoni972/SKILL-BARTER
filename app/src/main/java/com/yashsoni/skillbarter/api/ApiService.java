package com.yashsoni.skillbarter.api;

import com.yashsoni.skillbarter.data.model.AuthResponse;
import com.yashsoni.skillbarter.data.model.ExchangeRequest;
import com.yashsoni.skillbarter.data.model.Availability;
import com.yashsoni.skillbarter.data.model.HelpRequest;
import com.yashsoni.skillbarter.data.model.MatchResult;
import com.yashsoni.skillbarter.data.model.Message;
import com.yashsoni.skillbarter.data.model.Notification;
import com.yashsoni.skillbarter.data.model.Review;
import com.yashsoni.skillbarter.data.model.Session;
import com.yashsoni.skillbarter.data.model.SkillListing;
import com.yashsoni.skillbarter.data.model.Stats;
import com.yashsoni.skillbarter.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/register")
    Call<AuthResponse> register(@Body Map<String, String> body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @GET("users/profile")
    Call<Map<String, Object>> getProfile();

    @PUT("users/profile")
    Call<User> updateProfile(@Body Map<String, String> body);

    @GET("users/stats")
    Call<Stats> getStats();

    @GET("users/search")
    Call<List<User>> searchUsers(@Query("query") String query);

    @GET("users/recommended")
    Call<List<User>> getRecommendedPartners();

    @GET("discover/users")
    Call<List<MatchResult>> getDiscoverUsers(
            @Query("skill") String skill,
            @Query("category") String category,
            @Query("location") String location
    );

    @POST("skills/batch")
    Call<Map<String, String>> updateSkillsBatch(@Body Map<String, List<String>> body);

    @POST("requests")
    Call<ExchangeRequest> sendRequest(@Body Map<String, String> body);

    @GET("requests/incoming")
    Call<List<ExchangeRequest>> getIncomingRequests();

    @GET("requests/outgoing")
    Call<List<ExchangeRequest>> getOutgoingRequests();

    @PUT("requests/{id}/accept")
    Call<ExchangeRequest> acceptRequest(@Path("id") String id);

    @PUT("requests/{id}/reject")
    Call<ExchangeRequest> rejectRequest(@Path("id") String id);

    @PUT("requests/{id}/complete")
    Call<ExchangeRequest> completeRequest(@Path("id") String id);

    @POST("messages")
    Call<Message> sendMessage(@Body Map<String, String> body);

    @GET("messages/{userId}")
    Call<List<Message>> getMessages(@Path("userId") String userId);

    @POST("sessions")
    Call<Session> createSession(@Body Map<String, String> body);

    @GET("sessions")
    Call<List<Session>> getSessions();

    @POST("reviews")
    Call<Review> addReview(@Body Map<String, Object> body);

    @GET("notifications")
    Call<List<Notification>> getNotifications();

    @GET("marketplace")
    Call<List<SkillListing>> getMarketplaceListings();

    @POST("marketplace/listings")
    Call<SkillListing> createMarketplaceListing(@Body Map<String, Object> body);

    @GET("help-requests")
    Call<List<HelpRequest>> getHelpRequests();

    @POST("help-requests")
    Call<HelpRequest> createHelpRequest(@Body Map<String, String> body);

    @GET("availability/{userId}")
    Call<List<Availability>> getAvailability(@Path("userId") String userId);

    @POST("availability")
    Call<Availability> addAvailability(@Body Map<String, String> body);
}
