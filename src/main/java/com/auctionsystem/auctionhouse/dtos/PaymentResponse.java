package com.auctionsystem.auctionhouse.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("redirectUri")
    private String redirectUri;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("error")
    private String error;
}