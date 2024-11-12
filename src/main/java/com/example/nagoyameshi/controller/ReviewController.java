package com.example.nagoyameshi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.EditForm;
import com.example.nagoyameshi.form.RegisterForm;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.repository.ReviewRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants/{restaurantId}/review")
public class ReviewController {
	private final ReviewRepository reviewRepository;
	private final ReviewService reviewService;
	private final RestaurantRepository restaurantRepository;
	

	 public ReviewController(ReviewRepository reviewRepository,RestaurantRepository restaurantRepository,ReviewService reviewService) {
		    this.reviewRepository = reviewRepository;
		    this.reviewService = reviewService;
		    this.restaurantRepository = restaurantRepository;
		  }
	 
	
	 
	 //家の詳細ページ
	 @GetMapping
	  public String index(Model model,@PageableDefault(page = 0, size = 6, sort = "id", direction = Direction.ASC)Pageable pageable) {
		   Page<Review> reviewPage;   
			   reviewPage = reviewRepository.findAll(pageable);
		   model.addAttribute("reviewPage", reviewPage);
		   return "restaurants/show";
	  }
	// レビュー一覧の表示
	    @GetMapping("/table")
	    public String table(@PathVariable(name = "restaurantId") Integer restaurantId,
	                        Model model,
	                        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
	    	// restaurantIdに基づいてレビューを取得
	        Restaurant restaurant = restaurantRepository.findById(restaurantId)
	            .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + restaurantId));
	        
	        Page<Review> reviewPage = reviewRepository.findByRestaurantOrderByCreatedAtDesc(restaurant, pageable);
	        
	        
	        model.addAttribute("restaurant", restaurant);
	        model.addAttribute("reviewPage", reviewPage);

	        if (userDetailsImpl != null) {
	            model.addAttribute("user", userDetailsImpl.getUser());
	        }

	        return "review/table";
	    }
	    //レビューの投稿
		  @GetMapping("/register")
		  public String register(@PathVariable(name = "restaurantId") Integer restaurantId, Model model) {
		      Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		      model.addAttribute("restaurant", restaurant);
		      model.addAttribute("RegisterForm", new RegisterForm());
		      return "review/register";
		  }

	  //レビューの作成
	  @PostMapping("/create")
	  public String create(@ModelAttribute @Validated RegisterForm RegisterForm, BindingResult bindingResult,
			  RedirectAttributes redirectAttributes,
			  @AuthenticationPrincipal UserDetailsImpl userDetailsImpl
			  ) {
	      if (bindingResult.hasErrors()) {
	          return "restaurants/show/review/register";
	      }
	      User user = userDetailsImpl.getUser();
	      RegisterForm.setUserId(user.getId());
	      reviewService.create(RegisterForm);
	 
	      redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");
	      return "redirect:/restaurants/{restaurantId}";
	  }

	  //レビューの編集
	  @GetMapping("/{id}/edit")
	  public String edit(@PathVariable(name = "id") Integer id,
			             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			             @PathVariable(name = "restaurantId") Integer restaurantId,		             
			             Model model) {
		  Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
	      Review review = reviewRepository.getReferenceById(id);
	      EditForm EditForm = new EditForm(review.getId(),restaurant.getId(),review.getStar(), review.getReview());
	      model.addAttribute("restaurant", restaurant);
	      model.addAttribute("EditForm", EditForm);
	      return "review/edit";
	  }

	  //レビューの更新
	  @PostMapping("{id}/update")
	  public String update(@ModelAttribute@Validated EditForm EditForm, BindingResult bindingResult,@PathVariable(name = "restaurantId") Integer restaurantId, Model model, RedirectAttributes redirectAttributes) {
		   if(bindingResult.hasErrors()) {
			   return "review/edit";
		   }
		   Restaurant restaurant = restaurantRepository.getReferenceById(restaurantId);
		   model.addAttribute("restaurant", restaurant);
		   reviewService.update(EditForm);
		   redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");
		   return "redirect:/restaurants/{restaurantId}";

	  }
	  
	  //レビューの削除
	  @GetMapping("/{id}/delete")
	  public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		   reviewRepository.deleteById(id);
		   redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		   return "redirect:/restaurants/{restaurantId}";
	  }

}