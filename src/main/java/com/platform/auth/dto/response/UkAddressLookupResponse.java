package com.platform.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "UK Postcode & Address Lookup Result")
public class UkAddressLookupResponse implements Serializable {

    private boolean valid;
    private String postcode;
    private String country;
    private String region;
    private String adminDistrict;
    private String parliamentaryConstituency;
    private Double latitude;
    private Double longitude;

    public UkAddressLookupResponse() {
    }

    public UkAddressLookupResponse(boolean valid, String postcode, String country, String region, String adminDistrict, String parliamentaryConstituency, Double latitude, Double longitude) {
        this.valid = valid;
        this.postcode = postcode;
        this.country = country;
        this.region = region;
        this.adminDistrict = adminDistrict;
        this.parliamentaryConstituency = parliamentaryConstituency;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static UkAddressLookupResponse invalid(String postcode) {
        UkAddressLookupResponse res = new UkAddressLookupResponse();
        res.setValid(false);
        res.setPostcode(postcode);
        return res;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAdminDistrict() {
        return adminDistrict;
    }

    public void setAdminDistrict(String adminDistrict) {
        this.adminDistrict = adminDistrict;
    }

    public String getParliamentaryConstituency() {
        return parliamentaryConstituency;
    }

    public void setParliamentaryConstituency(String parliamentaryConstituency) {
        this.parliamentaryConstituency = parliamentaryConstituency;
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
