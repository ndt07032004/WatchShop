package com.watchstore.payment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Simple MoMo integration helper (Test environment).
 * You MUST replace partnerCode/accessKey/secretKey with your MoMo credentials.
 * This implementation uses the MoMo v2 create payment endpoint.
 */
public class MomoPayment {
    // TODO: điền thông tin thật từ MoMo
    private static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String PARTNER_CODE = "YOUR_PARTNER_CODE";
    private static final String ACCESS_KEY = "YOUR_ACCESS_KEY";
    private static final String SECRET_KEY = "YOUR_SECRET_KEY";

    public static String createPayment(String orderId, String amount, String orderInfo, String redirectUrl, String ipnUrl) throws Exception {
        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";
        String extraData = ""; // optional

        // raw signature string - order of params is important for MoMo
        String raw = "accessKey=" + ACCESS_KEY +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + PARTNER_CODE +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = hmacSha256(raw, SECRET_KEY);

        String jsonPayload = "{"
                + "\"partnerCode\":\"" + PARTNER_CODE + "\",
                + "\"accessKey\":\"" + ACCESS_KEY + "\",
                + "\"requestId\":\"" + requestId + "\",
                + "\"amount\":\"" + amount + "\",
                + "\"orderId\":\"" + orderId + "\",
                + "\"orderInfo\":\"" + escapeJson(orderInfo) + "\",
                + "\"redirectUrl\":\"" + redirectUrl + "\",
                + "\"ipnUrl\":\"" + ipnUrl + "\",
                + "\"extraData\":\"" + extraData + "\",
                + "\"requestType\":\"" + requestType + "\",
                + "\"signature\":\"" + signature + "\""
                + "}";

        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.flush();
        }

        int code = conn.getResponseCode();
        BufferedReader br;
        if (code >= 200 && code < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }
        StringBuilder resp = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            resp.append(line);
        }

        // Very basic extraction of "payUrl" from response JSON
        String respStr = resp.toString();
        // expected:...\"payUrl\":\"https://...\"...
        int idx = respStr.indexOf("\"payUrl\"");
        if (idx != -1) {
            int start = respStr.indexOf(':', idx) + 1;
            int firstQuote = respStr.indexOf('"', start);
            int secondQuote = respStr.indexOf('"', firstQuote + 1);
            if (firstQuote != -1 && secondQuote != -1) {
                return respStr.substring(firstQuote + 1, secondQuote);
            }
        }
        throw new RuntimeException("Cannot get payUrl from MoMo response: " + respStr);
    }

    private static String escapeJson(String s) {
        return s.replace("\"", "\\\"");
    }

    private static String hmacSha256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}