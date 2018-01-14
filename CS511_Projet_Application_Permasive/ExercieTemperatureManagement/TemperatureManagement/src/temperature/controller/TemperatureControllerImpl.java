package temperature.controller;

import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemperatureControllerImpl implements DeviceListener {

	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for coolers dependency */
	private Cooler[] coolers;
	
	public static final String LOCATION_PROPERTY_NAME = "Location";
	public static final String LOCATION_UNKNOWN = "unknown";
	public static final Double TARGET_TEMPERATURE = 300.00;
	

	/** Bind Method for coolers dependency */
	public void bindCooler(Cooler cooler, Map properties) {
		System.out.println("\nbindCooler with cooler.serialNumber = " + cooler.getSerialNumber());
		cooler.addListener(this);
		cooler.setPowerLevel(1);
	}

	/** Unbind Method for coolers dependency */
	public void unbindCooler(Cooler cooler, Map properties) {
		System.out.println("\nunbindCooler with cooler.serialNumber = " + cooler.getSerialNumber());
		cooler.removeListener(this);
	}

	/** Bind Method for thermometers dependency */
	public void bindThermometer(Thermometer thermometer, Map properties) {
		System.out.println("\nbindThermometer with thermometer.serialNumber = " + thermometer.getSerialNumber());
		thermometer.addListener(this);
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometer(Thermometer thermometer, Map properties) {
		System.out.println("\nunbindThermometer with thermometer.serialNumber = " + thermometer.getSerialNumber());
		thermometer.removeListener(this);
	}

	/** Bind Method for heaters dependency */
	public void bindHeater(Heater heater, Map properties) {
		System.out.println("\nbindHeater with heater.serialNumber = " + heater.getSerialNumber());
		heater.addListener(this);
		heater.setPowerLevel(1);
	}

	/** Unbind Method for heaters dependency */
	public void unbindHeater(Heater heater, Map properties) {
		System.out.println("\nunbindHeater with heater.serialNumber = " + heater.getSerialNumber());
		heater.removeListener(this);
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component is stopping...");
		for(Thermometer th : thermometers) {
			th.removeListener(this);
		}
		for(Cooler c : coolers) {
			c.removeListener(this);
		}
		for(Heater h : heaters) {
			h.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		// TODO: Add your implementation code here
		System.out.println("Component is starting...");
	}
	
	
	
    /**
     * This method is part of the DeviceListener interface and is called when a
     * subscribed device property is modified.
     * 
     * @param device
     *            is the device whose property has been modified.
     * @param propertyName
     *            is the name of the modified property.
     * @param oldValue
     *            is the old value of the property
     * @param newValue
     *            is the new value of the property
     */
    public void devicePropertyModified(GenericDevice device,
        String propertyName, Object oldValue,Object newValue) {
    	
    	boolean check = true;
    	
     
      // check the change is related to presence sensing
      if (propertyName.equals(Thermometer.THERMOMETER_CURRENT_TEMPERATURE)) {
    	  
    	  while(check) {
    		  
    		  for(Thermometer thermometer : thermometers) {
        		  
    			  String location = (String) thermometer.getPropertyValue(LOCATION_PROPERTY_NAME);
        		  
        		  List<Heater> listHeater = getHeatersInRoom(location);
            	  List<Cooler> listCooler = getCoolersInRoom(location);
            	  
            	  if(thermometer.getTemperature() > TARGET_TEMPERATURE ) {
            		  for(Cooler cooler : coolers) {
            			  cooler.setPowerLevel(0.2);
            		  }
            		  for(Heater heater : heaters) {
            			  heater.setPowerLevel(0);
            		  }
            	  } else {
            		  for(Cooler cooler : coolers) {
            			  cooler.setPowerLevel(0);
            		  }
            		  for(Heater heater : heaters) {
            			  heater.setPowerLevel(0.2);
            		  }
            	  }
            	  
        	  }
    	  }
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  
    	  Thermometer thermometerInUse = (Thermometer) device;
    	  //Compte le nombre de chauffage et de climatiseur dans la pièce et fait la moyenne
    	  
    	  String location = (String) thermometerInUse.getPropertyValue(LOCATION_PROPERTY_NAME);
    	  
    	  List<Heater> listHeater = getHeatersInRoom(location);
    	  List<Cooler> listCooler = getCoolersInRoom(location);
    	  
    	  if(listHeater.isEmpty()) {
    		  if(listCooler.isEmpty()) {
    			  //nothing to do
    		  } else {
    			  if(thermometerInUse.getTemperature() > TARGET_TEMPERATURE ) {
    				  for(Cooler cooler : listCooler) {
    					  cooler.setPowerLevel(1);
    				  }
    			  } else {
    				  for(Cooler cooler : listCooler) {
    					  cooler.setPowerLevel(0);
    				  }
    			  }
    		  }
    	  } else {
    		  if(listCooler.isEmpty()) {
    			  if(thermometerInUse.getTemperature() > TARGET_TEMPERATURE ) {
    				  for(Heater heater : listHeater) {
    					  heater.setPowerLevel(0);
    				  }
    			  } else {
    				  for(Heater heater : listHeater) {
    					  heater.setPowerLevel(1);
    				  }
    			  }
    		  } else {
    			  if(thermometerInUse.getTemperature() > TARGET_TEMPERATURE ) {
    				  for(Heater heater : listHeater) {
    					  heater.setPowerLevel(0);
    				  }
    				  for(Cooler cooler : listCooler) {
    					  cooler.setPowerLevel(1);
    				  }
    			  } else {
    				  for(Heater heater : listHeater) {
    					  heater.setPowerLevel(1);
    				  }
    				  for(Cooler cooler : listCooler) {
    					  cooler.setPowerLevel(0);
    				  }
    			  }
    		  }
    	  }
    	  
    	  
        System.out.println("Le thermomètre numéro " + thermometerInUse.getSerialNumber()+" change de température");
        System.out.println("Il est dans la pièce :" + location);  
        System.out.println("L'ancienne température est de : " + oldValue + " Kelvin");
        System.out.println("La nouvelle température est de : " + newValue + " Kelvin");  
      }
     
    }
    
    public List<Heater> getHeatersInRoom(String location) {
    	List<Heater> heatersInRoom = new ArrayList<Heater>();
    	for(Heater heater : heaters) {
    		if(heater.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
    			heatersInRoom.add(heater);
    		}
    	}
    	return heatersInRoom;
    }
    
    public List<Cooler> getCoolersInRoom(String location) {
    	List<Cooler> coolersInRoom = new ArrayList<Cooler>();
    	for(Cooler cooler : coolers) {
    		if(cooler.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
    			coolersInRoom.add(cooler);
    		}
    	}
    	return coolersInRoom;
    }
    
    public void ajustCoolerPowerLevel(List<Cooler> listCooler) {
    	//TODO 
    }
    
    public void ajustHeaterPowerLevel(List<Heater> listHeater) {
    	//TODO 
    }

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

}
