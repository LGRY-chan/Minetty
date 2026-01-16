package org.lgry.common.request;

import com.google.gson.JsonObject;
import org.lgry.common.Packet;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PendingRequestManager {
    private final Map<String, CompletableFuture<JsonObject>> PENDING =
            new ConcurrentHashMap<>();

    public CompletableFuture<JsonObject> register(Packet packet) {
        CompletableFuture<JsonObject> requestFuture = new CompletableFuture<>();
        PENDING.put(packet.getUUID(), requestFuture);
        return requestFuture;
    }

    public void complete(String UUID, JsonObject payload) {
        CompletableFuture<JsonObject> requestFuture = PENDING.remove(UUID);
        if (!(requestFuture == null)) requestFuture.complete(payload);
    }
}
