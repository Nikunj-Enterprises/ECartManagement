package in.test.mywebapp.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

import in.test.mywebapp.common.ApplicationConstants;
import in.test.mywebapp.common.measure.Unit;
import in.test.mywebapp.exception.AlreadyExistException;
import in.test.mywebapp.exception.NotFoundException;
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
	
	public Category findCategoryById(ObjectId objectId) {
		Query query = new Query();
		query.addCriteria(Criteria.where(ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN).is(objectId));
		DBObject obj = 
				mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).findOne(query.getQueryObject());
		return obj == null? null:convertToCategoryObject(obj);
	}
	
	public Category findCategory(String categoryName, String parentCategoryName) {
		Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").is(parentCategoryName));
        query.fields().include(ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN);
        DBCursor cursor = mongoTemplate.getCollection( CATEGORY_COLLECTION_NAME).find(query.getQueryObject());
        List<ObjectId> ids = new ArrayList<>();
        while(cursor.hasNext()) {
        	ids.add((ObjectId) cursor.next().get(ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN));
        }
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("categoryName")
        		.is(categoryName)
        		.and("parentCategoryId").in(ids));
        DBObject  obj = 
        		mongoTemplate.getCollection( CATEGORY_COLLECTION_NAME).findOne(query2.getQueryObject());
        
        return obj == null? null :convertToCategoryObject(obj);
	}
	
	public List<Category> getCategories(Category parentCategory){
		List<Category> categoryList = new ArrayList<>();
		
		ObjectId parentCategoryId = parentCategory.getId();
		if(parentCategoryId == null || findCategoryById(parentCategoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
        
		Query query = new Query();
		query.addCriteria(Criteria.where("parentCategoryId").is(parentCategoryId));
		
		DBCursor cursor = 
				mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).find(query.getQueryObject());
		
		while(cursor.hasNext()) {
		    categoryList.add(convertToCategoryObject(cursor.next()));
		}
		
		return categoryList;
	}
	
	public List<CustomProperty> getProperties(Category category){
		ObjectId categoryId = category.getId();
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		query.fields().include("propertyName");
		query.fields().include("propertyValue");
		List<CustomProperty> properties = new ArrayList<>();
		
		DBCursor cursor =
				mongoTemplate.getCollection(CATEGORY_PROPERTY_COLLECTION_NAME).find(query.getQueryObject());
		
		while(cursor.hasNext()) {
			CustomProperty property = new CustomProperty();
			DBObject obj = cursor.next();
			property.setPropertyName((String) obj.get("propertyName"));
			property.setPropertyValue((String) obj.get("propertyValue"));
			properties.add(property);
		}
		return properties;
	}
	
	public String getProperty(Category category, String propName){
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId).and("propertyName").is(propName));
		
		return mongoTemplate.findOne(query, String.class,CATEGORY_PROPERTY_COLLECTION_NAME);
	}
	
	public List<CustomImage> getImages(Category category){
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId));
		query.fields().include("imageIndex");
		query.fields().include("imageData");
		List<CustomImage> images = new ArrayList<>();
		
		DBCursor cursor =
				mongoTemplate.getCollection(CATEGORY_IMAGE_COLLECTION_NAME).find(query.getQueryObject());
		
		while(cursor.hasNext()) {
			CustomImage image = new CustomImage();
			DBObject obj= cursor.next();
			image.setImageIndex((Integer) obj.get("imageIndex"));
			image.setImageData((byte[]) obj.get("imageData"));
			images.add(image);
		}
		
		return images;
	}
	
	public byte[] getImage(Category category, int index) {
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		
		Query query = new Query();
		query.addCriteria(Criteria.where("categoryId").is(categoryId).and("imageIndex").is(index));
		query.fields().include("imageData");
		return mongoTemplate.findOne(query, byte[].class,CATEGORY_IMAGE_COLLECTION_NAME);
	}
	
	
	public int createCategory(Category category) {
		if(category.getCategoryName() != null && !category.getCategoryName().isEmpty()) {	
			if(category.getParentCategoryId() ==null ) {
				category.setParentCategoryId(new ObjectId(ApplicationConstants.TOP_MOST_CATEGORY_ID));
			}else{
				Category parentCategory = findCategoryById(category.getParentCategoryId());
				if(parentCategory == null)
				    throw new NotFoundException("parent category does not exist");
				
				if(findCategory(category.getCategoryName(), parentCategory.getCategoryName()) != null) {
					throw new AlreadyExistException("Category already created");
				}
			}
			
			mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME).insert(convertToDBObject(category));
			
			Query query = new Query();
	        query.addCriteria(Criteria.where("categoryName")
	        		.is(category.getCategoryName())
	        		.and("parentCategoryId")
	        		.is(category.getParentCategoryId()));
	        
			category.setId((ObjectId) mongoTemplate.getCollection(CATEGORY_COLLECTION_NAME)
					                    .findOne(query.getQueryObject())
					                    .get(ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN));
			return 1;
		}
		return 0;
	}
	
	public int addProperty(Category category, String propName, String propValue) {
        ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		
		if(getProperty(category, propName) != null) {
			throw new AlreadyExistException("Property already exist");
		}
		return 0;
	}
	
	public int addImage(Category category, int index, byte[] image) {		
        ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		return 0;
	}
	
	public int update(Category category) {
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		return 0;
	}
	
	public int updateProperty(Category category,String  propName, String propValue) {
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		return 0;
	}
	
	public int updateImage(Category category,int  index, byte[] image) {
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
		return 0;
	}
	
	public void removeCategory(Category category) {
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
	}
    
    public void removeProperty(Category category,String  propName) {
    	ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}	
	}
	
	public void removeImage(Category category,int  index) {
		ObjectId categoryId = category.getId();
		
		if(categoryId == null || findCategoryById(categoryId) == null) {
			throw new NotFoundException("Category Not found");
		}
	}
	
	
	private DBObject convertToDBObject(Category aCategory) {
        DBObject dbo = new BasicDBObject();
        dbo.put(ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN, aCategory.getId());
        dbo.put("categoryName", aCategory.getCategoryName());
        dbo.put("isItem",false);
        ObjectId parentId = aCategory.getParentCategoryId();
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
                	ObjectId parentId = (ObjectId) dbObject.get("parentCategoryId");
                	categoryObj.setParentCategoryId(parentId);
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
                case ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN:
                	categoryObj.setId((ObjectId) dbObject.get(ApplicationConstants.CATEGORY_COLLECTION_ID_COLUMN));
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
}
