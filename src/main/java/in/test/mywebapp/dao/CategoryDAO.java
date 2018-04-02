package in.test.mywebapp.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import in.test.mywebapp.model.Category;
import in.test.mywebapp.model.CustomImage;
import in.test.mywebapp.model.CustomProperty;

@Repository
public class CategoryDAO {
	private final String CATEGORY_COLLECTION_NAME = "categories";
	private final String CATEGORY_PROPERTY_COLLECTION_NAME = "category_properties";
	private final String CATEGORY_IMAGE_COLLECTION_NAME = "category_images";
	
	private MongoOperations mongoTemplate;
	
	@Autowired
	public CategoryDAO(MongoOperations mongoTemplate) {
		this.mongoTemplate= mongoTemplate;
	}
	
	public List<Category> getCategories(Category parentCategory){
		List<Category> categoryList = new ArrayList<>();
		
		Long parentCategoryId = 
				findCategoryId(parentCategory.getCategoryName(), 
						parentCategory.getParentCategoryName());
        
		Query query = new Query();
		query.addCriteria(Criteria.where("parentCategoryId").is(parentCategoryId));
		
		categoryList.addAll(mongoTemplate.find(query, Category.class, CATEGORY_COLLECTION_NAME));
		
		return categoryList;
	}
	
	public List<CustomProperty> getProperties(Category category){
		Long categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		query.fields().include("propertyName");
		query.fields().include("propertyValue");
		List<CustomProperty> properties = mongoTemplate.find(query, CustomProperty.class,CATEGORY_PROPERTY_COLLECTION_NAME);
		return properties ==null?new ArrayList<>():properties;
	}
	
	public String getProperty(Category category, String propName){
		Long categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId).and("propertyName").is(propName));
		query.fields().include("propertyValue");
		return mongoTemplate.findOne(query, String.class,CATEGORY_PROPERTY_COLLECTION_NAME);
	}
	
	public List<CustomImage> getImages(Category category){
		Long categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		query.fields().include("imageIndex");
		query.fields().include("imageData");
		List<CustomImage> images = mongoTemplate.find(query, CustomImage.class,CATEGORY_IMAGE_COLLECTION_NAME);
		
		return images ==null?new ArrayList<>():images;
	}
	
	public byte[] getImage(Category category, int index) {
		Long categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId).and("imageIndex").is(index));
		query.fields().include("imageData");
		return mongoTemplate.findOne(query, byte[].class,CATEGORY_IMAGE_COLLECTION_NAME);
		
	}
	
	public Category findCategory(String categoryName, String parentCategoryName) {
		Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").is(parentCategoryName));
        query.fields().include("id");
        List<Long> ids = mongoTemplate.find(query, Long.class, CATEGORY_COLLECTION_NAME);
        
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("categoryName")
        		.is(categoryName)
        		.and("parentCategoryId").in(ids));
        Category aCategory = 
        		mongoTemplate.findOne(query2, Category.class, CATEGORY_COLLECTION_NAME);
        aCategory.setParentCategoryName(parentCategoryName);
        
        return aCategory;
	}
	
	
	public int createCategory(Category category) {
		if(category.getCategoryName() != null && category.getParentCategoryName() !=null
		&& !category.getCategoryName().isEmpty() && !category.getParentCategoryName().isEmpty()) {
			category.setIsItem(false);
			mongoTemplate.insert(category, CATEGORY_COLLECTION_NAME);
		}
		return 0;
	}
	
	public int addProperty(Category category, String propName, String propValue) {
		return 0;
	}
	
	public int addImage(Category category, int index, byte[] image) {
		return 0;
	}
	
	public int update(Category oldCategory, Category newCategory) {
		return 0;
	}
	
	public int updateProperty(Category category,String  propName, String propValue) {
		return 0;
	}
	
	public int updateImage(Category category,int  index, byte[] image) {
		return 0;
	}
	
	public void removeCategory(Category category) {
		
	}
    
    public void removeProperty(Category category,String  propName) {
		
	}
	
	public void removeImage(Category category,int  index) {
		
	}
	
	private Long findCategoryId(String categoryName, String parentCategoryName) {
		Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").is(parentCategoryName));
        query.fields().include("id");
        List<Long> ids = mongoTemplate.find(query, Long.class, CATEGORY_COLLECTION_NAME);
        
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("categoryName").
        		is(categoryName).and("parentCategoryId").in(ids));
        query2.fields().include("id");
        
        return mongoTemplate.findOne(query2, Long.class,CATEGORY_COLLECTION_NAME);
	}
	
	private DBObject convertToDBObject(Category aCategory) {
        DBObject dbo = new BasicDBObject();
        dbo.put("name", aCategory.getCategoryName());
       
        // remove fields with no values, to avoid updating with null values
        List<String> keys = new ArrayList<>();
        keys.addAll(dbo.keySet());
        for (String key : keys) {
            if (dbo.get(key) == null) {
                dbo.removeField(key);
            }
        }

        return dbo;
    }

    private Category convertToCategoryObject(DBObject dbObject) {
        Category categoryObj = new Category();
        Set<String> keys = dbObject.keySet();

        for (String key : keys) {
            switch (key) {
                case "name":
                    categoryObj.setCategoryName((String) dbObject.get("categoryName"));
                    break;
                default:
            }
        }

        return categoryObj;
    }

	
}
