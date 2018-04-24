package in.test.mywebapp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.test.mywebapp.dao.CategoryDAO;
import in.test.mywebapp.dao.ItemDAO;
import in.test.mywebapp.exception.NotFoundException;
import in.test.mywebapp.model.Category;
import in.test.mywebapp.model.Item;
import in.test.mywebapp.model.Quantity;

@Service
public class ItemStoreServiceImpl implements ItemStoreService {

	@Autowired
	private CategoryDAO categoryDAO;
	
	@Autowired
	private ItemDAO itemDAO;
	
	@Override
	public List<Item> getItemsUnderCategory(Category category) {
		validateCategoryExistance(category);
		return itemDAO.getItemsOfCategory(category);
	}
	
	@Override
	public Item findItemByNameUnderCategory(Category category, String name) {
		validateCategoryExistance(category);
		return itemDAO.findCategoryItem(category.getId(), name);
	}

	@Override
	public void addItemUnderCategory(Category category, Item item) {
		validateCategoryExistance(category);
		if(item.getCategoryName() != null && !item.getCategoryName().isEmpty()) {
			item.setParentCategoryId(category.getId());
			Item dbItem = findItemByNameUnderCategory(category, item.getCategoryName());
			if(dbItem == null) {
				//Item is being newly created
				int i = itemDAO.createItem(item);
				if(i == 1) {
					// Now add Quantity, Measure Unit, price for created item
					Quantity<?> itemQuantity = item.getItemQuantity();
					if(itemQuantity != null && itemQuantity.value() > 0.0) {
						itemDAO.updateItemQuantity(item, itemQuantity);
					}else if(item.getMeasureUnit() !=null){
						itemDAO.updateItemQuantity(item, 
								new Quantity<>(0.0, item.getMeasureUnit()));
					}else{
						category = 
								categoryDAO.findCategoryById(category.getId());
						itemDAO.updateItemQuantity(item, 
								new Quantity<>(0.0, category.getDefaultMeasureUnitForCategoryItems()));
					}
					if(item.getPricePerUnit() > 0.0) {
						itemDAO.updateItemPerUnitPrice(item, item.getPricePerUnit());
					}
				}
			}else {
				//Item is already in db. Add quantity
				Quantity<?> itemQuantity = item.getItemQuantity();
				if(itemQuantity != null && itemQuantity.value() > 0.0) {
					Quantity<?> newQuantity = itemQuantity.add(dbItem.getItemQuantity());
					item.setItemQuantity(newQuantity);
					itemDAO.updateItemQuantity(item, newQuantity);
				}
			}
		}
	}

	@Override
	public void removeItemUnderCategory(Category category, Item item) {
		validateCategoryExistance(category);
		if(item.getCategoryName() != null && !item.getCategoryName().isEmpty()) {
			item.setParentCategoryId(category.getId());
			Item dbItem = findItemByNameUnderCategory(category, item.getCategoryName());
			if(dbItem != null) {
				Quantity<?> itemQuantity = item.getItemQuantity();
				if(itemQuantity != null && itemQuantity.value() > 0.0) {
					Quantity<?> newQuantity = dbItem.getItemQuantity().remove(itemQuantity);
					item.setItemQuantity(newQuantity);
					itemDAO.updateItemQuantity(item, newQuantity);
				}
			}
		}
	}

	@Override
	public Quantity<?> getTotalItemQuantity(Item item) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			return dbItem.getItemQuantity();
		}
		throw new NotFoundException("item not found");
	}


	@Override
	public void setTotalItemQuantity(Item item, Quantity<?> quantity) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			 // if quantity is valid
			 itemDAO.updateItemQuantity(item, quantity);
		}
		throw new NotFoundException("item not found");
	}

	@Override
	public void setItemPricePerUnit(Item item, double price) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			itemDAO.updateItemPerUnitPrice(item, price);
		}
		throw new NotFoundException("item not found");
	}

	@Override
	public double getItemPrice(Item item) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			Quantity<?> quantity = getTotalItemQuantity(item);
			double price = itemDAO.getItemPerUnitPrice(item);
			return price*quantity.value();
		}
		throw new NotFoundException("item not found");
	}

	@Override
	public List<Item> getItemsUnderCategoryByProperty(Category category, String propertyName, String propertyValue) {
		validateCategoryExistance(category);
		List<Item> items = itemDAO.findItemsByProperty(propertyName, propertyValue);
		return items == null? new ArrayList<>():items;
	}

	@Override
	public void addOrUpdateItemImage(Item item, int index, byte[] image) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			if(itemDAO.getImage(dbItem, index) != null) {
				itemDAO.updateImage(dbItem, index, image);
			}else {
				itemDAO.addImage(dbItem, index, image);
			}
		}else {
			throw new NotFoundException("item not found");
		}
	}

	@Override
	public byte[] getItemImage(Item item, int index) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			return itemDAO.getImage(dbItem, index);
		}
		throw new NotFoundException("item not found");
	}

	@Override
	public void deleteItemImage(Item item, int index) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			itemDAO.removeImage(dbItem, index);
		}
		throw new NotFoundException("item not found");
		
	}

	@Override
	public void addOrUpdateItemProperty(Item item, String propName, String propValue) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			if(itemDAO.getProperty(dbItem, propName) != null) {
				itemDAO.updateProperty(dbItem, propName, propValue);
			}else {
				itemDAO.addProperty(dbItem, propName, propValue);
			}
		}else {
			throw new NotFoundException("item not found");
		}
		
	}

	@Override
	public String getItemProperty(Item item, String propName) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			itemDAO.getProperty(dbItem, propName);
		}
		throw new NotFoundException("item not found");
	}

	@Override
	public void deleteItemProperty(Item item, String propName) {
		Item dbItem = itemDAO.findCategoryItem(item.getParentCategoryId(), item.getCategoryName());
		if(dbItem != null) {
			itemDAO.removeProperty(dbItem, propName);
		}else {
		    throw new NotFoundException("item not found");
		}
		
	}
	
	private void validateCategoryExistance(Category category) {
		if(category.getCategoryName() == null || category.getCategoryName().isEmpty() 
				|| category.getParentCategoryId() == null ||category.getId() == null
				|| categoryDAO.findCategoryById(category.getId()) == null) {
			throw new NotFoundException("Non-existing category");
		}
	}

}
