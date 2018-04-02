package in.test.mywebapp.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.test.mywebapp.common.measure.Unit;

import static in.test.mywebapp.common.ApplicationConstants.*;

public class Category {
	private String categoryName;
	private String parentCategoryName = TOP_MOST_CATEGORY_NAME;
	private boolean isItem = false;
	private List<Category> subCategories;
	private Unit defaultMeasureUnitForCategoryItems;
	private long createdAt;
	private long validTill;
	
	private Map<String, byte[]> imageMap = new HashMap<String, byte[]>();
	private Map<String, String> propertyMap = new HashMap<String, String>();
	
	public void setCategoryName(String name){
		this.categoryName = name;
	}
	public String getCategoryName(){
		return this.categoryName;
	}
	public void setParentCategoryName(String name){
		this.parentCategoryName = name;
	}
	public String getParentCategoryName(){
		return this.parentCategoryName;
	}
	public boolean isItem(){
		return this.isItem;
	}
	public void setIsItem(boolean isItem){
		this.isItem = isItem;
	}
	public List<Category>  getSubCategories(){
		return this.subCategories;
	}
	public void setSubCategories(List<Category> subCategories){
		this.subCategories = subCategories;
	}
	
	public void setDefaultMeasureUnitForCategoryItems(Unit unit){
		this.defaultMeasureUnitForCategoryItems = unit;
	}
	public Unit getDefaultMeasureUnitForCategoryItems(){
		return this.defaultMeasureUnitForCategoryItems;
	}
	
	public void setImage(String imageName, byte[] data){
		this.imageMap.put(imageName, data);
	}
	public byte[] getImage(String imageName){
		return this.imageMap.get(imageName);
	}
	
	public void setProperty(String propertyName, String propertyValue){
		this.propertyMap.put(propertyName, propertyValue);
	}
	public String getProperty(String propertyName){
		return this.propertyMap.get(propertyName);
	}
	
	public long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
	
	public long getValidTill() {
		return validTill;
	}
	public void setValidTill(long validTill) {
		this.validTill = validTill;
	}
}
