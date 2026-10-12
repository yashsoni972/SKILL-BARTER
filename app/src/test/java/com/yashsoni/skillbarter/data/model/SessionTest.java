package com.yashsoni.skillbarter.data.model;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SessionTest {

    private final Gson gson = new Gson();

    @Test
    public void session_parsesUnpopulatedRawUserIds() {
        String json = "{"
                + "\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d1\","
                + "\"requestId\":\"65f1a2b3c4d5e6f7a8b9c0d0\","
                + "\"hostUserId\":\"65f1a2b3c4d5e6f7a8b9c0d2\","
                + "\"partnerUserId\":\"65f1a2b3c4d5e6f7a8b9c0d3\","
                + "\"skill\":\"Android Development\","
                + "\"date\":\"2026-10-12\","
                + "\"time\":\"18:00\","
                + "\"dayOfWeek\":\"Monday\","
                + "\"startTime\":\"18:00\","
                + "\"endTime\":\"19:00\","
                + "\"meetLink\":\"https://meet.google.com/xyz-abc-def\","
                + "\"status\":\"scheduled\""
                + "}";

        Session session = gson.fromJson(json, Session.class);
        assertNotNull(session);
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d1", session.getId());
        assertNotNull(session.getHostUserId());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d2", session.getHostUserId().getId());
        assertNotNull(session.getPartnerUserId());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d3", session.getPartnerUserId().getId());
        assertEquals("Android Development", session.getSkill());
        assertEquals("Monday, 18:00 - 19:00", session.getWhenLabel());
    }

    @Test
    public void session_parsesPopulatedUserObjects() {
        String json = "{"
                + "\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d1\","
                + "\"hostUserId\":{\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d2\",\"name\":\"Alice\"},"
                + "\"partnerUserId\":{\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d3\",\"name\":\"Vraj\"},"
                + "\"skill\":\"Python\""
                + "}";

        Session session = gson.fromJson(json, Session.class);
        assertNotNull(session);
        assertNotNull(session.getHostUserId());
        assertEquals("Alice", session.getHostUserId().getName());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d2", session.getHostUserId().getId());
        assertNotNull(session.getPartnerUserId());
        assertEquals("Vraj", session.getPartnerUserId().getName());
        assertEquals("65f1a2b3c4d5e6f7a8b9c0d3", session.getPartnerUserId().getId());
    }

    @Test
    public void session_nullUserRefs_deserializeToNull() {
        String json = "{\"_id\":\"65f1a2b3c4d5e6f7a8b9c0d1\",\"hostUserId\":null,\"partnerUserId\":null}";

        Session session = gson.fromJson(json, Session.class);
        assertNotNull(session);
        assertNull(session.getHostUserId());
        assertNull(session.getPartnerUserId());
    }
}
