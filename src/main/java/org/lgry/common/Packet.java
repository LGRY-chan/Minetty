package org.lgry.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.lgry.common.request.RequestMethod;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

public class Packet {
    public final String uuid;
    private final Gson GSON = new Gson();
    public final RequestMethod requestMethod;
    private String KEY = "";
    public JsonObject content;
    private static final Pattern keyPattern = Pattern.compile("^[A-Za-z_]+$");

    public Packet(RequestMethod requestMethod) {
        this.uuid = UUID.randomUUID().toString();
        this.requestMethod = requestMethod;
        this.content = new JsonObject();
    }

    // Only used for response packet generation.
    private Packet(Packet packet) {
        this.uuid = packet.uuid;
        this.requestMethod = RequestMethod.RESPONSE;
        this.content = new JsonObject();
    }

    // Only used for GET packet generation.
    public Packet(String key) {
        this.uuid = UUID.randomUUID().toString();
        this.requestMethod = RequestMethod.GET;
        this.content = new JsonObject();
        setKey(key);
    }

    // ByteBuf(received data) -> Packet
    public Packet(ByteBuf buf) {

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        JsonObject metaJson = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
        this.uuid = metaJson.get("ID").getAsString();
        this.requestMethod = RequestMethod.valueOf(metaJson.get("METHOD").getAsString());
        this.KEY = metaJson.get("KEY").getAsString();
        this.content = metaJson.get("CONTENT").getAsJsonObject();

    }

    // Packet -> ByteBuf(data to send)
    public ByteBuf encode() {
        byte[] bytes = this.toString().getBytes(StandardCharsets.UTF_8);
        return Unpooled.wrappedBuffer(bytes);
    }

    public Packet getResponsePacket(JsonObject content) {
        Packet responsePacket = new Packet(this);
        responsePacket.content = content;
        return responsePacket;
    }

    public String getUUID() {
        return this.uuid;
    }

    public Packet setKey(String key) {
        if (!keyPattern.matcher(key).matches()) return this;
        this.KEY = key.toUpperCase();
        return this;
    }

    public String getKey() {
        return this.KEY;
    }

    public Packet with(String key, String value) {
        this.content.addProperty(key, value);
        return this;
    }

    public Packet with(String key, int value) {
        this.content.addProperty(key, value);
        return this;
    }

    @Override
    public String toString() {
        JsonObject metaJson = new JsonObject();
        metaJson.addProperty("ID", this.uuid);
        metaJson.addProperty("METHOD", this.requestMethod.name());
        metaJson.addProperty("KEY", this.KEY);
        metaJson.add("CONTENT", this.content);
        return GSON.toJson(metaJson);
    }


}
