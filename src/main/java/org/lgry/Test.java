package org.lgry;

import org.lgry.client.MinettyClient;
import org.lgry.common.AbstractMinettyClient;
import org.lgry.common.Packet;
import org.lgry.common.request.RequestMethod;
import org.lgry.server.MinettyServer;

public class Test {
    public static void main(String[] args) {
        AbstractMinettyClient client = new AbstractMinettyClient() {
            @Override
            public int getPort() {
                return 1234;
            }

            public int getServerPort() {
                return 8080;
            }

            public String getSecret() {
                return "dddd";
            }
        };

        client.startAsync();
        client.send(new Packet("HELLO_WORLD").with("you", "me"));
        //client.stop();

    }
}
