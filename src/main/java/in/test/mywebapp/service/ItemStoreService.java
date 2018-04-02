package in.test.mywebapp.service;

import java.util.List;

import in.test.mywebapp.model.Category;
import in.test.mywebapp.model.Item;
import in.test.mywebapp.model.Quantity;

public interface ItemStoreService {
	
	public List<Item> getItemsUnderCategoryByProperty(Category category, String propertyName, String propertyValue);
	public List<Item> getItemsUnderCategory(Category category);
	public Item findItemByNameUnderCategory(Category category, String name);
	
	public void addOrUpdateItemImage(Item item, int index, byte[] image);
    public byte[] getItemImage(Item item, int index);
    public void deleteItemImage(Item item, int index);
    
    public void addOrUpdateItemProperty(Item item, String propName, String propValue);
    public String getItemProperty(Item item, String propName);
    public void deleteItemProperty(Item item, String propName);
    
    public void addItemUnderCategory(Category category, Item item);
    public void removeItemUnderCategory(Category category, Item item);
    
    public Quantity<?> getTotalItemQuantity(Item item);
    public void setTotalItemQuantity(Item item, Quantity<?> quantity);
    
    public void setItemPricePerUnit(Item item, double price);
    public double getItemPrice(Item item);
}
