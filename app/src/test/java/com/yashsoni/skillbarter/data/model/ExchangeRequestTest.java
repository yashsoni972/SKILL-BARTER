package com.yashsoni.skillbarter.data.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ExchangeRequestTest {

    private final Gson gson = new Gson();
    private final Type listType = new TypeToken<List<ExchangeRequest>>() {}.getType();

    @Test
    public void incomingRequest_parsesPopulatedSenderAndRawReceiverId() {
        String json = "[{"
                + "\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d1\","
                + "\"senderId\":{\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d2\",\"name\":\"Riya Sharma\","
                + "\"email\":\"riya@example.com\",\"location\":\"Ahmedabad\",\"rating\":4.8},"
                + "\"receiverId\":\"65f1a2b3c4d5e6f7a8b9c0d3\","
                + "\"offeredSkill\":\"HTML & CSS\","
                + "\"requestedSkill\":\"Graphic Design\","
                + "\"message\":\"Hi!\","
                + "\"status\":\"pending\","
                + "\"createdAt\":\"2026-09-28T09:00:00.000Z\""
                + "}]";

        List<ExchangeRequest> requests = gson.fromJson(json, listType);
        assertEquals(1, requests.size());

        ExchangeRequest request = requests.get(0);
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d1", request.getId());
        assertNotNull(request.getSenderId());
        assertEquals("Riya Sharma", request.getSenderId().getName());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d2", request.getSenderId().getId());
        assertNotNull(request.getReceiverId());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d3", request.getReceiverId().getId());
        assertEquals("pending", request.getStatus());
    }

    @Test
    public void sendResponse_parsesBothRawUserIds() {
        String json = "{\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d1\","
                + "\"senderId\":\"65f1a2b3c4d5e6f7a8b9c0d2\","
                + "\"receiverId\":\"65f1a2b3c4d5e6f7a8b9c0d3\","
                + "\"offeredSkill\":\"Python\","
                + "\"requestedSkill\":\"SEO\","
                + "\"message\":\"\","
                + "\"status\":\"pending\","
                + "\"createdAt\":\"2026-09-28T09:00:00.000Z\"}";

        ExchangeRequest request = gson.fromJson(json, ExchangeRequest.class);
        assertNotNull(request);
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d2", request.getSenderId().getId());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d3", request.getReceiverId().getId());
    }

    @Test
    public void nullUserRefs_deserializeToNull() {
        String json = "{\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d1\",\"senderId\":null,\"receiverId\":null}";

        ExchangeRequest request = gson.fromJson(json, ExchangeRequest.class);
        assertNull(request.getSenderId());
        assertNull(request.getReceiverId());
    }
}
