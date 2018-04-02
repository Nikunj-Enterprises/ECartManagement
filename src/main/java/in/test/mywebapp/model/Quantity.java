package in.test.mywebapp.model;

import in.test.mywebapp.common.measure.Unit;

public class Quantity<T extends Unit> {
	private double value;
	private T unit;


	public Quantity(double value, T unit){
		this.value = value;
		this.unit = unit;
	}

    public double value(){
    	return this.value;
    }
    
    public String unit(){
    	return this.unit.getUnitName();
    }
    
    public Unit getQuantityUnit(){
    	return this.unit;
    }
    
    public Quantity<T> add(Quantity<?> quantity){
    	if(this.unit.getUnitType().equals(quantity.getQuantityUnit().getUnitType())){
    		double finalVal = (this.value * this.unit.getMultiplyFactor()) 
    				+ (quantity.value() * quantity.getQuantityUnit().getMultiplyFactor());
    		Quantity<T> newQuantity = new Quantity<T>(finalVal/this.unit.getMultiplyFactor(), this.unit);
    		return newQuantity;
    	}
    	throw new RuntimeException("Operation on quantities of different type");
    }
    
    public Quantity<T> remove(Quantity<?> quantity){
    	if(this.unit.getUnitType().equals(quantity.getQuantityUnit().getUnitType())){
    		double finalVal = (this.value * this.unit.getMultiplyFactor()) 
    				- (quantity.value() * quantity.getQuantityUnit().getMultiplyFactor());
    		Quantity<T> newQuantity = new Quantity<T>(finalVal/this.unit.getMultiplyFactor(), this.unit);
    		return newQuantity;
    	}
    	throw new RuntimeException("Operation on quantities of different type");
    }
}
