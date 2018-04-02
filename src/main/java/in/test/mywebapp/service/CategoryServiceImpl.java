package in.test.mywebapp.service;

import static in.test.mywebapp.common.ApplicationConstants.TOP_MOST_CATEGORY_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.test.mywebapp.dao.CategoryDAO;
import in.test.mywebapp.exception.AlreadyExistException;
import in.test.mywebapp.exception.InvalidInputException;
import in.test.mywebapp.exception.NotFoundException;
import in.test.mywebapp.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService{
	
	@Autowired
	private CategoryDAO categoryDAO;

	@Override
	public List<Category> getRootCategories() {
		Category parentCategory = new Category();
		parentCategory.setCategoryName(TOP_MOST_CATEGORY_NAME);
		return categoryDAO.getCategories(parentCategory);
	}

	@Override
	public List<Category> getSubCategories(Category category) {
		validateCategoryExistance(category);
		return categoryDAO.getCategories(category);
	}

	@Override
	public void createRootCategory(Category category) {
		if(category.getCategoryName() == null) {
			throw new InvalidInputException("category name can not be null");
		}
		if(categoryDAO.findCategory(category.getCategoryName(), TOP_MOST_CATEGORY_NAME) == null) {
			category.setParentCategoryName(TOP_MOST_CATEGORY_NAME);
			categoryDAO.createCategory(category);
		}else {
			throw new AlreadyExistException("Category "+category.getCategoryName()+" already exist");
		}
	}

	@Override
	public void addSubCategory(Category category, Category subCategory) {
		validateCategoryExistance(category);
		if(subCategory.getCategoryName() == null) {
			throw new InvalidInputException("category name can not be null");
		}
		if(categoryDAO.findCategory(subCategory.getCategoryName(), category.getCategoryName())== null) {
			subCategory.setParentCategoryName(category.getCategoryName());
			categoryDAO.createCategory(subCategory);
		}else {
			throw new AlreadyExistException("Category "+subCategory.getCategoryName()+" already exist");
		}
	}

	@Override
	public void removeRootCategory(Category category) {
		category.setParentCategoryName(TOP_MOST_CATEGORY_NAME);
		validateCategoryExistance(category);
		categoryDAO.removeCategory(category);		
	}

	@Override
	public void removeSubCategory(Category category, Category subCategory) {
		validateCategoryExistance(category);
		subCategory.setParentCategoryName(category.getCategoryName());
		validateCategoryExistance(subCategory);
		categoryDAO.removeCategory(subCategory);
	}

	@Override
	public void addOrUpdateCategoryImage(Category category, int index,  byte[] image) {
		validateCategoryExistance(category);

		if(categoryDAO.getImage(category, index) != null) {
			categoryDAO.updateImage(category, index, image);
		}else {
			categoryDAO.addImage(category, index, image);
		}
	}

	@Override
	public byte[] getCategoryImage(Category category, int index) {
		return categoryDAO.getImage(category, index);
	}

	@Override
	public Category findRootCategoryByName(String name) {
		return categoryDAO.findCategory(name, TOP_MOST_CATEGORY_NAME);
	}

	@Override
	public Category findSubCategoryByName(Category category, String name) {
		validateCategoryExistance(category);
		return categoryDAO.findCategory(name, category.getCategoryName());
	}

	@Override
	public void updateCategory(Category oldCategory, Category newCategory) {
		validateCategoryExistance(oldCategory);
		try {
			validateCategoryExistance(newCategory);
			throw new AlreadyExistException("Category "+newCategory.getCategoryName()+" already exist");
		}catch(NotFoundException e) {
			// newCategory names safe to use
			if(newCategory.getCategoryName() != null && newCategory.getParentCategoryName() != null 
				&& !newCategory.getCategoryName().isEmpty() && !newCategory.getParentCategoryName().isEmpty())
			categoryDAO.update(oldCategory, newCategory);
		}
	}

	@Override
	public void deleteCategoryImage(Category category, int index) {
		validateCategoryExistance(category);
		categoryDAO.removeImage(category, index);
	}

	@Override
	public void addOrUpdateCategoryProperty(Category category, String propName, String propValue) {
		validateCategoryExistance(category);
		if(categoryDAO.getProperty(category, propName) == null ) {
			categoryDAO.addProperty(category, propName, propValue);
		}else {
			categoryDAO.updateProperty(category, propName, propValue);
		}
	}

	@Override
	public String getCategoryProperty(Category category, String propName) {
		validateCategoryExistance(category);
		return categoryDAO.getProperty(category, propName);
	}

	@Override
	public void deleteCategoryProperty(Category category, String propName) {
		validateCategoryExistance(category);
		categoryDAO.removeProperty(category, propName);
	}
	
	private void validateCategoryExistance(Category category) {
		if(category.getCategoryName() == null || category.getParentCategoryName() == null
				|| category.getCategoryName().isEmpty() || category.getParentCategoryName().isEmpty()
				|| categoryDAO.findCategory(category.getCategoryName(), category.getParentCategoryName()) == null) {
			throw new NotFoundException("Non-existing category");
		}
	}
}
