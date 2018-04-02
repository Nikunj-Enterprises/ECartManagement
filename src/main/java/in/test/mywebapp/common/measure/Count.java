package in.test.mywebapp.common.measure;


public abstract class Count implements Unit {
	private double multiplyFactor;
    private enum counts{
  	  PIECE, PC, DOZEN
    }
    
    public Count(String unitName){
  	  switch(counts.valueOf(unitName.toUpperCase()))
  	  {
  	    case PIECE:
  	    	this.multiplyFactor = 1;
  	    	break;
  	    case PC:
  	    	this.multiplyFactor = 1;
  	    	break;
  	    case DOZEN:
  	    	this.multiplyFactor = 12;
  	    	break;
  	    default:
  	    	throw new RuntimeException("Unknown count unit");
  	  }
    }
    
    public double getMultiplyFactor(){
  	  return this.multiplyFactor;
    }

    public String getUnitType(){
  	  return "Count";
    }
}
