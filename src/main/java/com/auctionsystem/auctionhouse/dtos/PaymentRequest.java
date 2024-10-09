package com.auctionsystem.auctionhouse.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {
    @JsonProperty("continueUrl")
    private String continueUrl;

    @JsonProperty("notifyUrl")
    private String notifyUrl;

    @JsonProperty("customerIp")
    private String customerIp;

    @JsonProperty("merchantPosId")
    private String merchantPosId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("totalAmount")
    private String totalAmount;

    @JsonProperty("products")
    private List<Product> products;

    @JsonProperty("buyer")
    private Buyer buyer;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        @JsonProperty("name")
        private String name;

        @JsonProperty("unitPrice")
        private Integer unitPrice;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("virtual")
        private Boolean virtual;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Buyer {
        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("firstName")
        private String firstName;

        @JsonProperty("lastName")
        private String lastName;
    }
}