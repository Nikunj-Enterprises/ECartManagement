package in.test.mywebapp.common.measure;

public enum MeasureTypes {
	
    Weight(Weight.class), Count(Count.class);
	
	private Class<?> type;
	MeasureTypes(Class<?> cls){
		type = cls;
	}
	
	Class<?> type(){
		return type;
	}
	
}
