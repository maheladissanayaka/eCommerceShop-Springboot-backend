package com.shop.eCommerceShop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.eCommerceShop.exception.HandleException;
import com.shop.eCommerceShop.model.Address;
import com.shop.eCommerceShop.model.User;
import com.shop.eCommerceShop.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/profile")
	public ResponseEntity<User>getUserProfileHandler(@RequestHeader("Authorization")String jwt)throws HandleException{
		
		User user = userService.findUserProfileByJwt(jwt);
		
		return new ResponseEntity<User>(user,HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/address")
	public ResponseEntity<Address>addAddressHandler(@RequestHeader("Authorization")String jwt,
			@RequestBody Address address)throws HandleException{
		
		Address savedAddress = userService.addAddress(jwt, address);
		
		return new ResponseEntity<Address>(savedAddress,HttpStatus.CREATED);
	}
	
	@PutMapping("/address/{addressId}")
	public ResponseEntity<Address>updateAddressHandler(@RequestHeader("Authorization")String jwt,
			@PathVariable("addressId")int addressId,
			@RequestBody Address address)throws HandleException{
		
		Address updatedAddress = userService.updateAddress(jwt, addressId, address);
		
		return new ResponseEntity<Address>(updatedAddress,HttpStatus.OK);
	}
	
	@DeleteMapping("/address/{addressId}")
	public ResponseEntity<String>deleteAddressHandler(@RequestHeader("Authorization")String jwt,
			@PathVariable("addressId")int addressId)throws HandleException{
		
		userService.deleteAddress(jwt, addressId);
		
		return new ResponseEntity<String>("Address deleted successfully",HttpStatus.OK);
	}
}
