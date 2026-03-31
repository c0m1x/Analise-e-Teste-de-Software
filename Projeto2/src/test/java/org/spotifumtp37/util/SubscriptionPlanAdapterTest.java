package org.spotifumtp37.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.spotifumtp37.model.subscription.FreePlan;
import org.spotifumtp37.model.subscription.PremiumBase;
import org.spotifumtp37.model.subscription.PremiumTop;
import org.spotifumtp37.model.subscription.SubscriptionPlan;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionPlanAdapterTest {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SubscriptionPlan.class, new SubscriptionPlanAdapter())
            .create();

    @Test
    void serializeShouldInjectTypeField() {
        String json = gson.toJson(new PremiumBase(), SubscriptionPlan.class);
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        assertTrue(obj.has("type"));
        assertTrue("PremiumBase".equals(obj.get("type").getAsString()));
    }

    @Test
    void deserializeShouldResolveConcretePremiumTop() {
        String json = "{\"type\":\"PremiumTop\"}";

        SubscriptionPlan parsed = gson.fromJson(json, SubscriptionPlan.class);

        assertInstanceOf(PremiumTop.class, parsed);
    }

    @Test
    void deserializeShouldFallbackToFreePlanWhenTypeMissing() {
        String json = "{}";

        SubscriptionPlan parsed = gson.fromJson(json, SubscriptionPlan.class);

        assertInstanceOf(FreePlan.class, parsed);
    }
}
