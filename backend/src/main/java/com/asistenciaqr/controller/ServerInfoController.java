package com.asistenciaqr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequestMapping("/api/server")
public class ServerInfoController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        String ip = getLocalIp();
        String serverUrl = "https://" + ip + ":8443";
        return ResponseEntity.ok(Map.of(
                "localIp", ip,
                "serverUrl", serverUrl
        ));
    }

    private String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String host = addr.getHostAddress();
                    if (host != null && host.contains(".") && !host.startsWith("127.") && !host.startsWith("169.")) {
                        return host;
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
