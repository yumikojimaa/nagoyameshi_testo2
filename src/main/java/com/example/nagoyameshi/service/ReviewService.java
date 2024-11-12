package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.EditForm;
import com.example.nagoyameshi.form.RegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.repository.UserRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;

	public ReviewService(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository,
			UserRepository userRepository) {
		this.reviewRepository = reviewRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}
	
	//新規レビューをDBに保存
	@Transactional
	public void create(RegisterForm RegisterForm) {
		Review review = new Review();
		Restaurant restaurant = restaurantRepository.getReferenceById(RegisterForm.getRestaurantId());
		User user = userRepository.getReferenceById(RegisterForm.getUserId());
		review.setRestaurant(restaurant);
		review.setUser(user);
		review.setStar(RegisterForm.getStar());
		review.setReview(RegisterForm.getReview());
		reviewRepository.save(review);
	}

	//レビューの変更を保存
	@Transactional
	public void update(EditForm EditForm) {
		Review review = reviewRepository.getReferenceById(EditForm.getId());
		review.setStar(EditForm.getStar());
		review.setReview(EditForm.getReview());
		reviewRepository.save(review);
	}

}

    