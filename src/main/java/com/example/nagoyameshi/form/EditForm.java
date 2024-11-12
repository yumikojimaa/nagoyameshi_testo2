package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditForm {
	
	@NotNull
	private Integer id;
	
	private Integer restaurantId;
	
	private String star;
	
	private String review;
}	
