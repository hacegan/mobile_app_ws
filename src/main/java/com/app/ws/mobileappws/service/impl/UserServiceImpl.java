package com.app.ws.mobileappws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.ws.mobileappws.exceptions.UserServiceException;
import com.app.ws.mobileappws.io.entity.UserEntity;
import com.app.ws.mobileappws.io.repositories.UserRepository;
import com.app.ws.mobileappws.service.UserService;
import com.app.ws.mobileappws.shared.Utils;
import com.app.ws.mobileappws.shared.dto.AddressDTO;
import com.app.ws.mobileappws.shared.dto.UserDto;
import com.app.ws.mobileappws.ui.model.response.ErrorMessages;
import com.app.ws.mobileappws.ui.model.response.UserRest;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {
		// TODO Auto-generated method stub

		if (userRepository.findByEmail(user.getEmail()) != null)// mail unique kabul edip kayıt var mı yok mu kontrol
			throw new RuntimeException("Record already exists");

		for (int i = 0; i < user.getAddresses().size(); i++) {// Her adres icin ayrı bir id üretiyoruz.
			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}

		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		// BeanUtils.copyProperties(user, userEntity);

		userEntity.setUserId(utils.generateUserId(30));// 30 karakterlik üret
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));// kullanıcının girdiği
																							// sifreyi şifrele

		UserEntity storedUserDetails = userRepository.save(userEntity);

		UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);
		// BeanUtils.copyProperties(storedUserDetails, returnValue);

		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}

		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());

		UserEntity updatedUserEntity = userRepository.save(userEntity);

		BeanUtils.copyProperties(updatedUserEntity, returnValue);

		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		userRepository.delete(userEntity);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {// kullanıcı adı aslında email
																							// adresi
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {// email kaydı var mı yok mu kontrol
			throw new UsernameNotFoundException(email);
		}

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {// email kaydı var mı yok mu kontrol
			throw new UsernameNotFoundException(email);
		}

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) {// userid kontrol
			throw new UsernameNotFoundException("User with ID : " + userId + " Not Found");
		}

		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<UserDto>();

		Pageable pageable = PageRequest.of(page, limit);

		Page<UserEntity> usersPage = userRepository.findAll(pageable);
		List<UserEntity> users = usersPage.getContent();

		for (UserEntity user : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(user, userDto);
			returnValue.add(userDto);
		}

		return returnValue;
	}

}
