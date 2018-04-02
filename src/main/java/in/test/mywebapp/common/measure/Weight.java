package in.test.mywebapp.common.measure;


public abstract class Weight implements Unit {
      private double multiplyFactor;
      private enum weights{
    	  KILO, GRAM, KG, GM
      }
      
      public Weight(String unitName){
    	  switch(weights.valueOf(unitName.toUpperCase()))
    	  {
    	    case KILO:
    	    	this.multiplyFactor = 1000;
    	    	break;
    	    case KG:
    	    	this.multiplyFactor = 1000;
    	    	break;
    	    case GRAM:
    	    	this.multiplyFactor = 1;
    	    	break;
    	    case GM:
    	    	this.multiplyFactor = 1;
    	    	break;
    	    default:
    	    	throw new RuntimeException("Unknown weight unit");
    	  }
      }
      
      public double getMultiplyFactor(){
    	  return this.multiplyFactor;
      }
      
      public String getUnitType(){
    	  return "Weight";
      }
}
