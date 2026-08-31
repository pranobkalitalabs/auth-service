package com.platform.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Address Details (UK based)")
public class AddressDto {

    @NotBlank(message = "Address line 1 is required")
    @Schema(example = "10 Downing Street")
    private String addressLine1;

    @Schema(example = "Westminster")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Schema(example = "London")
    private String city;

    @Schema(example = "Greater London")
    private String county;

    @NotBlank(message = "Postcode is required")
    @Pattern(
        regexp = "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})$",
        message = "Invalid UK Postcode format"
    )
    @Schema(example = "SW1A 2AA")
    private String postcode;

    @Schema(example = "United Kingdom", defaultValue = "United Kingdom")
    private String country = "United Kingdom";

    private Double latitude;
    private Double longitude;

    public AddressDto() {
    }

    public AddressDto(String addressLine1, String addressLine2, String city, String county, String postcode, String country, Double latitude, Double longitude) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.county = county;
        this.postcode = postcode;
        this.country = country != null ? country : "United Kingdom";
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
