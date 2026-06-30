package com.asistenciaqr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

@SpringBootApplication
public class AsistenciaQrApplication {

    private static final String KEYSTORE_FILE = "keystore.p12";
    private static final String KEYSTORE_PASSWORD = "asistenciaqr";
    private static final String KEY_ALIAS = "asistenciaqr";

    public static void main(String[] args) {
        ensureKeystore();
        deleteDefaultKeystore();

        SpringApplication.run(AsistenciaQrApplication.class, args);
    }

    private static void deleteDefaultKeystore() {
        try {
            File defaultKs = new File(System.getProperty("user.home"), ".keystore");
            if (defaultKs.exists()) {
                if (defaultKs.delete()) {
                    System.out.println("Keystore por defecto eliminado: " + defaultKs.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo eliminar el keystore por defecto: " + e.getMessage());
        }
    }

    private static void ensureKeystore() {
        String ip = getLocalIp();
        File keystore = new File(KEYSTORE_FILE);

        if (keystore.exists() && keystoreMatchesIp(ip)) {
            System.out.println("Certificado SSL válido para IP: " + ip);
            return;
        }

        if (keystore.exists()) {
            System.out.println("IP cambiada, regenerando certificado SSL...");
            keystore.delete();
        }

        try {
            String dname = "CN=" + ip + ",OU=SIGAA,O=SIGAA,L=Panama,ST=Panama,C=PA";
            ProcessBuilder pb = new ProcessBuilder(
                    "keytool", "-genkey", "-noprompt",
                    "-alias", KEY_ALIAS,
                    "-keyalg", "RSA",
                    "-keysize", "2048",
                    "-validity", "3650",
                    "-dname", dname,
                    "-ext", "san=ip:" + ip,
                    "-keypass", KEYSTORE_PASSWORD,
                    "-storepass", KEYSTORE_PASSWORD,
                    "-storetype", "PKCS12",
                    "-keystore", KEYSTORE_FILE
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

    private static boolean keystoreMatchesIp(String ip) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
                ks.load(fis, KEYSTORE_PASSWORD.toCharArray());
            }
            X509Certificate cert = (X509Certificate) ks.getCertificate(KEY_ALIAS);
            if (cert == null) return false;
            String subject = cert.getSubjectX500Principal().getName();
            return subject.contains("CN=" + ip);
        } catch (Exception e) {
            return false;
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
