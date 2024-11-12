package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;


 public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	 public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	    public Page<Restaurant> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);

	    public Page<Restaurant> findByNameLikeOrAddressLikeOrderByLowestPriceAsc(String nameKeyword, String addressKeyword, Pageable pageable);

	    public Page<Restaurant> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);

	    public Page<Restaurant> findByAddressLikeOrderByLowestPriceAsc(String area, Pageable pageable);

	    public Page<Restaurant> findByLowestPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);

	    public Page<Restaurant> findByLowestPriceLessThanEqualOrderByLowestPriceAsc(Integer price, Pageable pageable);

	    public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);

	    public Page<Restaurant> findAllByOrderByLowestPriceAsc(Pageable pageable);

	    public List<Restaurant> findTop10ByOrderByCreatedAtDesc();
	}