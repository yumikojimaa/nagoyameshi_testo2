package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FavoriteRegisterForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FavoriteController {
	private final FavoriteRepository favoriteRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;
	private final FavoriteService favoriteService;
	
	public FavoriteController(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository, UserRepository userRepository, FavoriteService favoriteService) {
		this.favoriteRepository = favoriteRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
		this.favoriteService = favoriteService;
	}
	
	//お気に入り一覧
	@GetMapping("/favorites")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC)Pageable pageable, Model model, HttpSession httpSession) {
		User user = userDetailsImpl.getUser();
		Page<Favorite> favoritesPage = favoriteRepository.findByUser(user, pageable);
		httpSession.setAttribute("favoriteRestaurant", restaurantRepository.findAll());
		
		model.addAttribute("user", user);
		model.addAttribute("favoritesPage", favoritesPage);
		
		return "favorites/index";
	}
	
	//お気に入り登録
	 @PostMapping("/restaurants/{id}/create")
	    public String createFavorite(@PathVariable(name = "id") Integer restaurantId,
	    		                     @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
	    		                     RedirectAttributes redirectAttributes) {
	        Integer userId = userDetailsImpl.getUser().getId();
	        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
	        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found"));
	        
	        
	        FavoriteRegisterForm form = new FavoriteRegisterForm();
	        form.setRestaurantId(restaurant.getId());
	        form.setUserId(user.getId());
	        
	        
	        favoriteService.create(form, restaurant, user);
	        redirectAttributes.addFlashAttribute("success", true); // 成功フラグを追加
	        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました");
	        return "redirect:/restaurants/{id}";
	 }
    

    //お気に入り解除
	@PostMapping("/restaurants/{id}/delete")
	public String deleteFavorite(@PathVariable(name = "id") Integer restaurantId,
			                     @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
			                     RedirectAttributes redirectAttributes) {
	    Integer userId = userDetailsImpl.getUser().getId();
	    favoriteService.delete(restaurantId, userId);
	    redirectAttributes.addFlashAttribute("success", false); // 成功フラグを追加
	    redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました");
	    return "redirect:/restaurants/{id}";
	}
}