package org.lgry;

import com.google.gson.JsonObject;
import org.lgry.common.Packet;
import org.lgry.common.request.RequestMethod;
import org.lgry.server.MinettyServer;

import java.util.logging.Logger;

public class Main {

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Packet packet = new Packet(RequestMethod.GET).with("Msg", "Hello netty!").setKey("test");
        Packet newPacket = packet.getResponsePacket(new JsonObject());
        MinettyServer server = new MinettyServer();
        server.register("HELLO_WORLD", json -> {
            json.addProperty("Hello", "World!");
            json.addProperty("I am", 1854);
            return json;
        });
        server.start();
        logger.info(packet.toString());
        logger.info(newPacket.toString());

    }
}