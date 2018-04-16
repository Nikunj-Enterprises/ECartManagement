package in.test.mywebapp.dao;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import in.test.mywebapp.common.ApplicationConstants;
import in.test.mywebapp.common.measure.Unit;
import in.test.mywebapp.exception.AlreadyExistException;
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
		
		ObjectId parentCategoryId = 
				findCategoryId(parentCategory.getCategoryName(), 
						parentCategory.getParentCategoryName());
        
		Query query = new Query();
		query.addCriteria(Criteria.where("parentCategoryId").is(parentCategoryId));
		
		categoryList.addAll(mongoTemplate.find(query, Category.class, CATEGORY_COLLECTION_NAME));
		
		return categoryList;
	}
	
	public List<CustomProperty> getProperties(Category category){
		ObjectId categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		query.fields().include("propertyName");
		query.fields().include("propertyValue");
		List<CustomProperty> properties = mongoTemplate.find(query, CustomProperty.class,CATEGORY_PROPERTY_COLLECTION_NAME);
		return properties ==null?new ArrayList<>():properties;
	}
	
	public String getProperty(Category category, String propName){
		ObjectId categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId).and("propertyName").is(propName));
		query.fields().include("propertyValue");
		return mongoTemplate.findOne(query, String.class,CATEGORY_PROPERTY_COLLECTION_NAME);
	}
	
	public List<CustomImage> getImages(Category category){
		ObjectId categoryId = 
				findCategoryId(category.getCategoryName(), category.getParentCategoryName());
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		query.fields().include("imageIndex");
		query.fields().include("imageData");
		List<CustomImage> images = mongoTemplate.find(query, CustomImage.class,CATEGORY_IMAGE_COLLECTION_NAME);
		
		return images ==null?new ArrayList<>():images;
	}
	
	public byte[] getImage(Category category, int index) {
		ObjectId categoryId = 
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
        DBCursor cursor = mongoTemplate.getCollection( CATEGORY_COLLECTION_NAME).find(query.getQueryObject());
        List<ObjectId> ids = new ArrayList<>();
        while(cursor.hasNext()) {
        	ids.add((ObjectId) cursor.next().get("id"));
        }
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("categoryName")
        		.is(categoryName)
        		.and("parentCategoryId").in(ids));
        DBObject  obj = 
        		mongoTemplate.getCollection( CATEGORY_COLLECTION_NAME).findOne(query2.getQueryObject());
        
        if(obj == null) {
        	return null;
        }
        Category aCategory = convertToCategoryObject(obj);
        		
        aCategory.setParentCategoryName(parentCategoryName);
        
        return aCategory;
	}
	
	
	public int createCategory(Category category) {
		if(category.getCategoryName() != null && !category.getCategoryName().isEmpty()) {	
			if(category.getParentCategoryName() ==null || category.getParentCategoryName().isEmpty()) {
				category.setParentCategoryName(ApplicationConstants.TOP_MOST_CATEGORY_NAME);
			}
			if(findCategory(category.getCategoryName(), category.getParentCategoryName()) != null) {
				throw new AlreadyExistException("Category already created");
			}
			mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).insert(convertToDBObject(category));
			return 1;
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
	
	private ObjectId findCategoryId(String categoryName, String parentCategoryName) {
		Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").is(parentCategoryName));
        query.fields().include("id");
        DBCursor cursor = 
        		mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).find(query.getQueryObject());
        
        List<ObjectId> ids = new ArrayList<>();
        while(cursor.hasNext()) {
        	ids.add((ObjectId) cursor.next().get("id"));
        }
        
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("categoryName").
        		is(categoryName).and("parentCategoryId").in(ids));
        query2.fields().include("id");
        DBObject obj = 
        		mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).findOne(query2.getQueryObject());
        return obj == null? null:(ObjectId) obj.get("id");
	}
	
	private DBObject convertToDBObject(Category aCategory) {
        DBObject dbo = new BasicDBObject();
        dbo.put("categoryName", aCategory.getCategoryName());
        dbo.put("isItem",false);
        ObjectId parentId = 
        		findCategoryId(aCategory.getCategoryName(), aCategory.getParentCategoryName());
        if(parentId == null ) {
        	parentId = new ObjectId(ApplicationConstants.TOP_MOST_CATEGORY_ID);
        }
        dbo.put("parentCategoryId", parentId);
        
        if(aCategory.getCreatedAt() == 0L) {
        	dbo.put("startDate",Instant.now().getEpochSecond());
        }else {
            dbo.put("startDate", aCategory.getCreatedAt());
        }
        if(aCategory.getValidTill() == 0L) {
        	dbo.put("endDate", Instant.MAX.getEpochSecond()); 
        }else {
            dbo.put("endDate", aCategory.getValidTill());
        }
        dbo.put("measureUnit", aCategory.getDefaultMeasureUnitForCategoryItems());
       
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
                case "categoryName":
                    categoryObj.setCategoryName((String) dbObject.get("categoryName"));
                    break;
                case "parentCategoryId":
                	String parentName = getCategoryNameById((ObjectId) dbObject.get("parentCategoryId"));
                	categoryObj.setParentCategoryName(parentName);
                	break;
                case "measureUnit":
                	String unitName = (String) dbObject.get("measureUnit");
                	Unit unit = getUnitOf(unitName);
                	categoryObj.setDefaultMeasureUnitForCategoryItems(unit);
                	break;
                case "startDate":
                	Long createdAt = (Long) dbObject.get("startDate");
                	categoryObj.setCreatedAt(createdAt);
                	break;
                case "endDate":
                	Long validTill = (Long) dbObject.get("endDate");
                	categoryObj.setValidTill(validTill);
                	break;
                case "id":
                	break;
                case "isItem":
                	break;
                case "quantity":
                	break;
                case "pricePerUnit":
                	break;
                default:
            }
        }

        return categoryObj;
    }

	private Unit getUnitOf(String unitName) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getCategoryNameById(ObjectId objectId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(objectId));
		DBObject obj = 
				mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).findOne(query.getQueryObject());
		return obj == null? null:(String)obj.get("categoryName");
	}

	
}
