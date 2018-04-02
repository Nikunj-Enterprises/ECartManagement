package in.test.mywebapp.service;

import java.util.List;

import in.test.mywebapp.model.Category;

public interface CategoryService {
	public List<Category> getRootCategories();
	public List<Category> getSubCategories(Category category);
	
	public void createRootCategory(Category category);
	public void addSubCategory(Category category, Category subCategory);
	public void removeRootCategory(Category category);
    public void removeSubCategory(Category category, Category subCategory);
    
    public void addOrUpdateCategoryImage(Category category, int index, byte[] image);
    public byte[] getCategoryImage(Category category, int index);
    public void deleteCategoryImage(Category category, int index);
    
    public void addOrUpdateCategoryProperty(Category category, String propName, String propValue);
    public String getCategoryProperty(Category category, String propName);
    public void deleteCategoryProperty(Category category, String propName);
    
    public Category findRootCategoryByName(String name);
    public Category findSubCategoryByName(Category category, String name);
    
    public void updateCategory(Category oldCategory, Category newCategory);
}
