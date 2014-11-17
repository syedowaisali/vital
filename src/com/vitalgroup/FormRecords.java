package com.vitalgroup;

public class FormRecords {
	
	// private variables
	private String form_id;
	private String store_name;
	private String brand_name;
	private String city;
	private String notes;
	private String image_data;
	private String latitude;
	private String longitude;
	private String user_id;
	
	public FormRecords(){
		
	}
	
	public FormRecords(String _store_name, String _brand_name, String _city, String _notes, String _image_data, String _latitude, String _longitude, String _user_id){
		this.form_id = "1";
		this.store_name = _store_name;
		this.brand_name = _brand_name;
		this.city = _city;
		this.notes = _notes;
		this.image_data = _image_data;
		this.latitude = _latitude;
		this.longitude = _longitude;
		this.user_id = _user_id;
	}
	
	// get form ID
	public String getFormId(){
		return this.form_id;
	}
	
	// set form ID
	public void setFormId(String fid){
		this.form_id = fid;
	}
	
	// get store name
	public String getStoreName(){
		return this.store_name;
	}
	
	// set store name
	public void setStoreName(String f_store_name){
		this.store_name = f_store_name;
	}
	
	// get brand name
	public String getBrandName(){
		return this.brand_name;
	}
	
	// set brand name
	public void setBrandName(String f_brand_name){
		this.brand_name = f_brand_name;
	}
	
	// get city
	public String getCity(){
		return this.city;
	}
	
	// set city
	public void setCity(String f_city){
		this.city = f_city;
	}
	
	// get notes
	public String getNotes(){
		return this.notes;
	}
	
	// set notes
	public void setNotes(String f_notes){
		this.notes = f_notes;
	}
	
	// get image data
	public String getImageData(){
		return this.image_data;
	}
	
	// set image data
	public void setImageData(String f_image_data){
		this.image_data = f_image_data;
	}
	
	// get latitude
	public String getLatitude(){
		return this.latitude;
	}
	
	// set latitude
	public void setLatitude(String f_latitude){
		this.latitude = f_latitude;
	}
	
	// get longitude
	public String getLongitude(){
		return this.longitude;
	}
	
	// set longitude
	public void setLongitude(String f_longitude){
		this.longitude = f_longitude;
	}
	
	// get user id
	public String getUserId(){
		return this.user_id;
	}
	
	// set user id
	public void setUserId(String f_user_id){
		this.user_id = f_user_id;
	}
}
