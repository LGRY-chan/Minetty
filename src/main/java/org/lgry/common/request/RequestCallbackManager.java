package org.lgry.common.request;

import com.google.gson.JsonObject;
import org.lgry.Main;
import org.lgry.common.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class RequestCallbackManager {
    private final Map<String, Function<JsonObject, JsonObject>> callback = new ConcurrentHashMap<>();

    public void register(String key, Function<JsonObject, JsonObject> lambda) {
        callback.put(key,lambda);
    }

    public Function<JsonObject, JsonObject> get(String key) {
        return callback.get(key);
    }

}
