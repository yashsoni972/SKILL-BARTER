package com.yashsoni.skillbarter.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Handles fields referencing a {@link User} that Mongoose might return either
 * as a populated JSON Object (e.g., {"_id": "...", "name": "..."}) or as an
 * unpopulated raw ObjectId string (e.g., "651234567890abcdef123456").
 */
public class UserRefAdapter implements JsonDeserializer<User>, JsonSerializer<User> {

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        if (json.isJsonPrimitive()) {
            User user = new User();
            user.setId(json.getAsString());
            return user;
        }
        return context.deserialize(json, User.class);
    }

    @Override
    public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(user, User.class);
    }
}
