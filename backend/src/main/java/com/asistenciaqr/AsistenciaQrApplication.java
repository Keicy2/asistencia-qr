package com.asistenciaqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@SpringBootApplication
public class AsistenciaQrApplication {

    public static void main(String[] args) {
        ensureKeystore();
        SpringApplication.run(AsistenciaQrApplication.class, args);
    }

    private static void ensureKeystore() {
        File keystore = new File("keystore.p12");
        if (keystore.exists()) return;

        String ip = getLocalIp();
        String password = "asistenciaqr";

        try {
            String dname = "CN=" + ip + ",OU=SIGAA,O=SIGAA,L=Panama,ST=Panama,C=PA";
            ProcessBuilder pb = new ProcessBuilder(
                    "keytool", "-genkey", "-noprompt",
                    "-alias", "asistenciaqr",
                    "-keyalg", "RSA",
                    "-keysize", "2048",
                    "-validity", "3650",
                    "-dname", dname,
                    "-keypass", password,
                    "-storepass", password,
                    "-storetype", "PKCS12",
                    "-keystore", "keystore.p12"
            );
            pb.inheritIO();
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                System.out.println("Certificado SSL generado para IP: " + ip);
            } else {
                System.err.println("Error generando certificado SSL (exit code: " + exitCode + ")");
            }
        } catch (Exception e) {
            System.err.println("No se pudo generar el certificado SSL: " + e.getMessage());
        }
    }

    private static String getLocalIp() {
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
