package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FavoriteRegisterForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;



@Service
public class FavoriteService {
	private FavoriteRepository favoriteRepository;
	private UserRepository userRepository;
	private RestaurantRepository restaurantRepository;

	public  FavoriteService(FavoriteRepository favoriteRepository,UserRepository userRepository , RestaurantRepository restaurantRepository) {
	this.favoriteRepository = favoriteRepository;
	this.userRepository = userRepository;
	this.restaurantRepository = restaurantRepository;
}

	@Transactional
	public void create(FavoriteRegisterForm favoriteRegisterForm, Restaurant restaurant, User user) {
		Favorite favorite = new Favorite();
		
		favoriteRegisterForm.setRestaurantId(restaurant.getId());
		favoriteRegisterForm.setUserId(user.getId());
		
		favorite.setRestaurant(restaurantRepository.getReferenceById(favoriteRegisterForm.getRestaurantId()));
		favorite.setUser(userRepository.getReferenceById(favoriteRegisterForm.getUserId()));
		
		favoriteRepository.save(favorite);
	}
	
	@Transactional
	public void delete(Integer restaurantId, Integer userId) {
	    favoriteRepository.deleteByRestaurantIdAndUserId(restaurantId, userId);
	}
}
