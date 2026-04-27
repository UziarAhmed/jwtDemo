package com.example.jwtDemo.service;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.http.MediaType;

import com.example.jwtDemo.config.RazorpayProperties;

@Service
public class RazorpayService {

    private final RestClient restClient;
    private final RazorpayProperties razorpayProperties;

    public RazorpayService(RazorpayProperties razorpayProperties) {
        this.razorpayProperties = razorpayProperties;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.razorpay.com/v1")
                .defaultHeaders(headers -> headers.setBasicAuth(
                        razorpayProperties.getKeyId(), 
                        razorpayProperties.getKeySecret()))
                .build();
    }

    public RazorpayOrderResponse createOrder(long amount, String receipt) {
        return restClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "amount", amount,
                        "currency", razorpayProperties.getCurrency(),
                        "receipt", receipt
                ))
                .retrieve()
                .body(RazorpayOrderResponse.class);
    }

    public boolean verifySignature(String serverOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            String payload = serverOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(razorpayProperties.getKeySecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String generatedSignature = HexFormat.of().formatHex(digest);

            return generatedSignature.equals(razorpaySignature);
        } catch (Exception e) {
            throw new RuntimeException("Error while verifying Razorpay signature", e);
        }
    }

    public String getKeyId() {
        return razorpayProperties.getKeyId();
    }

    public String getCurrency() {
        return razorpayProperties.getCurrency();
    }

    public record RazorpayOrderResponse(
            String id,
            String entity,
            Integer amount,
            Integer amount_paid,
            Integer amount_due,
            String currency,
            String receipt,
            String status
    ) {
    }
}