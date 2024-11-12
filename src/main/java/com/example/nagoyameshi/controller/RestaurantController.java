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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.form.FavoriteRegisterForm;
import com.example.nagoyameshi.form.ReservationInputForm;
import com.example.nagoyameshi.repository.FavoriteRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	
	public RestaurantController(RestaurantRepository restaurantRepository,
			ReviewRepository reviewRepository, FavoriteRepository favoriteRepository) {
		this.restaurantRepository = restaurantRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteRepository = favoriteRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			//検索部分のパラメーター
			
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<Restaurant> restaurantPage;
		//キーワードによる検索
		if (keyword != null && !keyword.isEmpty()) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByLowestPriceAsc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			} else {
				restaurantPage = restaurantRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%",
						"%" + keyword + "%", pageable);
			}
		
		//価格による検索
		} else if (price != null) {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findByLowestPriceLessThanEqualOrderByLowestPriceAsc(price, pageable);
			} else {
				restaurantPage = restaurantRepository.findByLowestPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
			}
		//その他
		} else {
			if (order != null && order.equals("priceAsc")) {
				restaurantPage = restaurantRepository.findAllByOrderByLowestPriceAsc(pageable);
			} else {
				restaurantPage = restaurantRepository.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("price", price);
		model.addAttribute("order", order);

		return "restaurants/index";
	}
	


	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id,
	                   FavoriteRegisterForm favoriteRegisterForm,
	                   FavoriteRepository favoriteRepository,
	                   Model model,
	                   @PageableDefault(page = 0, size = 6, sort = "id", direction = Direction.ASC) Pageable pageable,
	                   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
	    
	    // Restaurantを取得
	    Restaurant restaurant = restaurantRepository.getReferenceById(id);
	    
	    // restaurantに基づいてレビューを取得
	    Page<Review> reviewPage = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);
        
	    // レビューが見つからなかった場合の処理
	    if (reviewPage.isEmpty()) {
	        System.out.println("No reviews found for restaurant id: " + id);
	    }

	    ReservationInputForm reservationInputForm = new ReservationInputForm();

	    // モデルにデータを追加
	    model.addAttribute("reviewPage", reviewPage); 
	    model.addAttribute("restaurant", restaurant);
	    model.addAttribute("reservationInputForm", reservationInputForm);
	  
	 /// ユーザー情報の追加
	    if (userDetailsImpl != null) {
	        // ログインユーザ情報の取得
	        var user = userDetailsImpl.getUser();
	        model.addAttribute("user", user);
	        
	        // ログインユーザが登録したレビューが存在しないことをチェック
	        var hasNotMyReview = reviewPage.filter(review -> review.getUser().getId().equals(userDetailsImpl.getUser().getId())).isEmpty();
	        // 上記のチェック結果をviwにわたす。
	        model.addAttribute("hasNotMyReview", hasNotMyReview);
	        
	        var favorite = this.favoriteRepository.findByRestaurantAndUser(restaurant, user);
	        if (favorite == null) {
	            model.addAttribute("favoriteId", null);
	        } else {
	            model.addAttribute("favoriteId", favorite.getId());
	        }

        

	        // ログインしていないときは、そもそもボタンの表示がされないが、念の為trueを設定しておく
	        model.addAttribute("hasNotMyReview", true);
	    }

	    return "restaurants/show";
	}
	
}