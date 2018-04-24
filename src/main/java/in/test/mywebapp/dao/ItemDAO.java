package in.test.mywebapp.dao;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import in.test.mywebapp.model.Category;
import in.test.mywebapp.model.Item;
import in.test.mywebapp.model.Quantity;

@Repository
public class ItemDAO {
	@Autowired
	private MongoOperations mongoTemplate;
	
	public int createItem(Item item) {
		return 0;
	}
	
	public int addProperty(Item item, String propName, String propValue) {
		return 0;
	}
	
	public int addImage(Item item, int index, byte[] image) {
		return 0;
	}
	
	public int updateNames(Item oldItem, Item newItem) {
		return 0;
	}
	
	public int updateProperty(Item item, String propName, String propValue) {
		return 0;
	}
	
	public int updateImage(Item item, int  index, byte[] image) {
		return 0;
	}
	
	public List<Item> getItemsOfCategory(Category parentCategory){
		return null;
	}

	public Item findCategoryItem(ObjectId parentCategoryId, String itemName) {
		return null;
	}
	
	public List<Item> findItemsByProperty(String propName, String propValue){
		return null;
	}
	
	public Map<String, String> getProperties(Item item){
		return null;
	}
	
	public String getProperty(Item item, String propName){
		return null;
	}
	
	public Map<String, byte[]> getImages(Item item){
		return null;
	}
	
	public byte[] getImage(Item item, int index) {
		return null;
	}
	
	public void removeProperty(Item item, String  propName) {

	}

	public void removeImage(Item item, int  index) {

	}
    public void removeItem(Item item) {
		
	}

	public Quantity<?> getItemQuantity(Item item){
		return null;
	}
	
	public int updateItemQuantity(Item item, Quantity<?> quantity) {
		return 0;
	}
	
	public double getItemPerUnitPrice(Item item) {
		return 0.0;
	}

	public int updateItemPerUnitPrice(Item item, double price) {
		return 0;
	}
}
