package com.app.ws.mobileappws.service;

import java.util.List;

import com.app.ws.mobileappws.shared.dto.AddressDTO;

public interface AddressService {
	List<AddressDTO> getAddresses(String userId);

	AddressDTO getAddress(String addressId);
}
