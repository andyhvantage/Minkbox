package com.minkbox.model;
public class AllCategoriesData {
	
	public int id;
	public String categoriesImageUrl;
	public String category;
	public String getCategoriesName() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int categoriesID;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCategoriesImageUrl() {
		return categoriesImageUrl;
	}
	public void setCategoriesImageUrl(String categoriesImageUrl) {
		this.categoriesImageUrl = categoriesImageUrl;
	}
	public int getCategoriesID() {
		return categoriesID;
	}
	public void setCategoriesID(int categoriesID) {
		this.categoriesID = categoriesID;
	}
	
	

}
