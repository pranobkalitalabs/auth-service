package com.platform.auth.service;

import com.platform.auth.dto.response.UkAddressLookupResponse;
import com.platform.auth.service.impl.UkAddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class UkAddressServiceTest {

    private UkAddressService ukAddressService;

    @BeforeEach
    void setUp() {
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
        ukAddressService = new UkAddressServiceImpl(restClient, new ObjectMapper());
    }

    @Test
    void shouldHandleEmptyOrNullPostcodeGracefully() {
        assertFalse(ukAddressService.isValidPostcode(null));
        assertFalse(ukAddressService.isValidPostcode(""));

        UkAddressLookupResponse response = ukAddressService.lookupPostcode(null);
        assertFalse(response.isValid());
    }

    @Test
    void shouldLookupStandardUkPostcodeFallback() {
        UkAddressLookupResponse response = ukAddressService.lookupPostcode("SW1A 1AA");
        assertNotNull(response);
        assertTrue(response.isValid());
        assertNotNull(response.getPostcode());
    }
}
