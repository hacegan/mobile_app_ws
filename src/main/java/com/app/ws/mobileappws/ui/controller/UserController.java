package com.app.ws.mobileappws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.ws.mobileappws.exceptions.UserServiceException;
import com.app.ws.mobileappws.service.AddressService;
import com.app.ws.mobileappws.service.UserService;
import com.app.ws.mobileappws.shared.dto.AddressDTO;
import com.app.ws.mobileappws.shared.dto.UserDto;
import com.app.ws.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.app.ws.mobileappws.ui.model.response.AddressesRest;
import com.app.ws.mobileappws.ui.model.response.ErrorMessages;
import com.app.ws.mobileappws.ui.model.response.OperationStatusModel;
import com.app.ws.mobileappws.ui.model.response.RequestOperationName;
import com.app.ws.mobileappws.ui.model.response.RequestOperationStatus;
import com.app.ws.mobileappws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users")
//@CrossOrigin(origins= {"http://localhost:8083", "http://localhost:8084"}) // cross origin isteklerini karsılamak icin
public class UserController {

 @Autowired
 UserService userService;

 @Autowired
 AddressService addressesService;

 @GetMapping(path = "/{id}", produces = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 })
 public UserRest getUser(@PathVariable String id) {
  UserRest returnValue = new UserRest();

  UserDto userDto = userService.getUserByUserId(id);
  BeanUtils.copyProperties(userDto, returnValue);

  return returnValue;
 }

 @GetMapping(produces = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 })
 public List < UserRest > getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
  @RequestParam(value = "limit", defaultValue = "25") int limit) {
  List < UserRest > returnValue = new ArrayList < UserRest > ();

  List < UserDto > users = userService.getUsers(page, limit);

  for (UserDto user: users) {
   UserRest userModel = new UserRest();
   BeanUtils.copyProperties(user, userModel);
   returnValue.add(userModel);
  }

  return returnValue;
 }

 // http://localhost:8080/mobile-app-ws/users/jfhdjeufhdhdj/addressses
 @GetMapping(path = "/{id}/addresses", produces = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE,
  "application/hal+json"
 })
 public Resources < AddressesRest > getUserAddresses(@PathVariable String id) {
  List < AddressesRest > addressesListModel = new ArrayList < AddressesRest > ();

  List < AddressDTO > addressesDTO = addressesService.getAddresses(id);

  if (addressesDTO != null && !addressesDTO.isEmpty()) {
   Type listType = new TypeToken < List < AddressesRest >> () { // maplenecek tip
   }.getType();
   addressesListModel = new ModelMapper().map(addressesDTO, listType);

   for (AddressesRest addressRest: addressesListModel) {
    Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
     .withSelfRel();
    addressRest.add(addressLink);

    Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
    addressRest.add(userLink);
   }

  }

  return new Resources < > (addressesListModel); // hal döndürecegimiz için resources objesi döndürüyoruz.
 }

 @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {
  MediaType.APPLICATION_JSON_VALUE,
  MediaType.APPLICATION_XML_VALUE,
  "application/hal+json"
 })
 public Resource < AddressesRest > getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

  AddressDTO addressesDto = addressesService.getAddress(addressId);

  ModelMapper modelMapper = new ModelMapper();

  Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel(); // methodon
  // getmappingdeki
  // pathi
  // alıyor.

  Link userLink = linkTo(UserController.class).slash(userId).withRel("user"); // user altında root yolu/userid

  Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

  AddressesRest addressesRestModel = modelMapper.map(addressesDto, AddressesRest.class);

  addressesRestModel.add(addressLink);
  addressesRestModel.add(userLink);
  addressesRestModel.add(addressesLink);

  return new Resource < > (addressesRestModel);
 }

 @PostMapping(consumes = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 }, produces = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 }) // consume xml yada json alır producede
 // geri döndürür
 public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
  UserRest returnValue = new UserRest();

  if (userDetails.getFirstName().isEmpty())
   throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage()); // kendi
  // özelleştirilebilir
  // exceptionumuzu
  // kullandık.

  // UserDto userDto = new UserDto();
  // BeanUtils.copyProperties(userDetails, userDto);

  ModelMapper modelMapper = new ModelMapper(); // bean utils ile uygun kopyalama yapamıyacağımız için kullandık.
  UserDto userDto = modelMapper.map(userDetails, UserDto.class);

  UserDto createdUser = userService.createUser(userDto);
  returnValue = modelMapper.map(createdUser, UserRest.class);
  // BeanUtils.copyProperties(createdUser, returnValue);

  return returnValue;
 }

 @PutMapping(path = "/{id}", consumes = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 }, produces = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 })
 public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

  UserRest returnValue = new UserRest();

  UserDto userDto = new UserDto();
  BeanUtils.copyProperties(userDetails, userDto);

  UserDto updateUser = userService.updateUser(id, userDto);
  BeanUtils.copyProperties(updateUser, returnValue);

  return returnValue;
 }

 @DeleteMapping(path = "/{id}", produces = {
  MediaType.APPLICATION_XML_VALUE,
  MediaType.APPLICATION_JSON_VALUE
 })
 public OperationStatusModel deleteUser(@PathVariable String id) {
  OperationStatusModel returnValue = new OperationStatusModel();
  returnValue.setOperationName(RequestOperationName.DELETE.name());

  userService.deleteUser(id);

  returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
  return returnValue;
 }

}
