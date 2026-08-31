package com.platform.auth.service;

import com.platform.auth.dto.response.UkAddressLookupResponse;

import java.util.List;

public interface UkAddressService {
    boolean isValidPostcode(String postcode);
    UkAddressLookupResponse lookupPostcode(String postcode);
    List<String> autocompletePostcode(String partialPostcode);
}
