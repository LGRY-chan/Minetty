package org.lgry.common.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class IPUtil {

    // External servers used for getting public IP
    private static final List<String> IP_SERVICES = List.of(
            "https://api.ipify.org",
            "https://checkip.amazonaws.com",
            "https://ifconfig.me/ip"
    );

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static String PUBLIC_IP = null;

    public static String getPublicIP() {
        if (PUBLIC_IP != null) return PUBLIC_IP;

        for (String service : IP_SERVICES) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(service))
                        .timeout(Duration.ofSeconds(3))
                        .GET()
                        .build();

                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String ip = response.body().trim();
                    if (!ip.isEmpty()) {
                        PUBLIC_IP = ip;
                        return ip;
                    }
                }
            } catch (Exception e) {
                // Do nothing, just try another service
            }
        }

        return "127.0.0.1";

    }

    public static boolean isLocal(String externalIP) {
        String myIP = getPublicIP().split(":")[0];
        String exIP = externalIP.split(":")[0];
        return myIP.equals(exIP);
    }

    public static int getPortOf(String IP) {
        return Integer.parseInt(IP.split(":")[1]);
    }

    public static int getAddressOf(String IP) {
        return Integer.parseInt(IP.split(":")[0]);
    }

}
