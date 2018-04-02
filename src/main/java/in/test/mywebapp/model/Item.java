package in.test.mywebapp.model;

import in.test.mywebapp.common.measure.Unit;

public class Item extends Category {
	private Unit measureUnit;
	private Quantity<?> itemQuantity;
	private double pricePerUnit;
	
	public Item(){
		this.setIsItem(true);
	}
	
    public void setMeasureUnit(Unit unit){
    	this.measureUnit = unit;
    }
	public Unit getMeasureUnit(){
		return this.measureUnit;
	}
	public Quantity<?> getItemQuantity(){
		return this.itemQuantity;
	}
	public void setItemQuantity(Quantity<?> quantity){
		this.itemQuantity = quantity;
	}
	public void setPricePerUnit(double price){
		this.pricePerUnit = price;
	}
	public double getPricePerUnit(){
		return this.pricePerUnit;
	}
}
