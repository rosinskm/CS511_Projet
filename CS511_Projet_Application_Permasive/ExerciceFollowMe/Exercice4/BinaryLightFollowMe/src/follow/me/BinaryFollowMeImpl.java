package follow.me;

import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import follow.me.configuration.FollowMeConfiguration;

public class BinaryFollowMeImpl implements DeviceListener, FollowMeConfiguration {

	/** 
	 * The maximum number of lights to turn on when a user enters the room :
	 **/
	private int maxLightsToTurnOnPerRoom = 2;

	/**
	* The maximum energy consumption allowed in a room in Watt:
	**/
	private double maximumEnergyConsumptionAllowedInARoom = 250d;

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;

	/**
	 * The name of the LOCATION property
	 */
	public static final String LOCATION_PROPERTY_NAME = "Location";

	/**
	 * The name of the location for unknown value
	 */
	public static final String LOCATION_UNKNOWN = "unknown";

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
		System.out.println("bind binary light " + binaryLight.getSerialNumber());
		binaryLight.addListener(this);
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLight(BinaryLight binaryLight, Map<Object, Object> properties) {
		System.out.println("unbind binary light " + binaryLight.getSerialNumber());
		binaryLight.removeListener(this);
	}

	/** Bind Method for dimmerLights dependency */
	public void bindDimmerLight(DimmerLight dimmerLight, Map<Object, Object> properties) {
		System.out.println("bind Dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.addListener(this);
	}

	/** Unbind Method for dimmerLights dependency */
	public void unbindDimmerLight(DimmerLight dimmerLight, Map<Object, Object> properties) {
		System.out.println("unbind Dimmer light " + dimmerLight.getSerialNumber());
		dimmerLight.removeListener(this);
	}

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
		System.out.println("bind presence sensor " + presenceSensor.getSerialNumber());
		// Add the listener to the presence sensor
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		// Remove the listener from the presence sensor
		presenceSensor.removeListener(this);
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		System.out.println("Component is stopping...");
		for (PresenceSensor sensor : presenceSensors) {
			sensor.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
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

	public void devicePropertyModified(GenericDevice device, String propertyName, Object oldValue, Object newValue) {

		if (device instanceof PresenceSensor) {
			System.out.println("Presence sensor changement detected\n");
			PresenceSensor changingSensor = (PresenceSensor) device;
			// check the change is related to presence sensing
			if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)) {
				// get the location where the sensor is:
				String detectorLocation = (String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
				// if the location is known :
				if (!detectorLocation.equals(LOCATION_UNKNOWN)) {
					double currentEnergyConsumptionAllowedInARoom = 0;
					List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
					for (BinaryLight binaryLight : sameLocationLigths) {
						// and switch them on/off depending on the sensed presence
						if (changingSensor.getSensedPresence()) {
							if((currentEnergyConsumptionAllowedInARoom + 100) <= maximumEnergyConsumptionAllowedInARoom) {
								binaryLight.turnOn();
								currentEnergyConsumptionAllowedInARoom = currentEnergyConsumptionAllowedInARoom + 100;
							}else {
								binaryLight.turnOff();
							}
							
						}else {
							binaryLight.turnOff();
						}
					}
					
					List<DimmerLight> sameLocationDimmerLigths = getDimmerLightFromLocation(detectorLocation);
					for (DimmerLight dimmerLight : sameLocationDimmerLigths) {
						// and switch them on/off depending on the sensed presence
						if (changingSensor.getSensedPresence()) {
							if((maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom) > 0) {
								if((maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom) > 100) {
									dimmerLight.setPowerLevel(1);
									currentEnergyConsumptionAllowedInARoom = currentEnergyConsumptionAllowedInARoom + 100;
								}else {
									dimmerLight.setPowerLevel((maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom)/100);
									System.out.println("La dimmer lampe a une consomation de " + (maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom));
									currentEnergyConsumptionAllowedInARoom = maximumEnergyConsumptionAllowedInARoom;
								}
							} else {
								dimmerLight.setPowerLevel(0);
							}
						} else {
							dimmerLight.setPowerLevel(0);
						}
					}
				}
			}
		}
		
		if ((device instanceof BinaryLight)||(device instanceof DimmerLight)) {
			System.out.println("Binary Light changement detected\n");
			if (propertyName.equals(LOCATION_PROPERTY_NAME)) {
				turnOffLight();
				for (PresenceSensor preSens : presenceSensors) {
					if (preSens.getSensedPresence()) {
						String detectorLocation = (String) preSens.getPropertyValue(LOCATION_PROPERTY_NAME);
						double currentEnergyConsumptionAllowedInARoom = 0;
						List<BinaryLight> sameLocationLigths = getBinaryLightFromLocation(detectorLocation);
						for (BinaryLight binaryLight : sameLocationLigths) {
							// and switch them on/off depending on the sensed presence
								if((currentEnergyConsumptionAllowedInARoom + 100) <= maximumEnergyConsumptionAllowedInARoom) {
									binaryLight.turnOn();
									currentEnergyConsumptionAllowedInARoom = currentEnergyConsumptionAllowedInARoom + 100;
								}else {
									binaryLight.turnOff();
								}
						}
						
						List<DimmerLight> sameLocationDimmerLigths = getDimmerLightFromLocation(detectorLocation);
						for (DimmerLight dimmerLight : sameLocationDimmerLigths) {
							// and switch them on/off depending on the sensed presence
								if((maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom) > 0) {
									if((maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom) > 100) {
										dimmerLight.setPowerLevel(1);
										currentEnergyConsumptionAllowedInARoom = currentEnergyConsumptionAllowedInARoom + 100;
									}else {
										dimmerLight.setPowerLevel((maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom)/100);
										System.out.println("La dimmer lampe a une consomation de " + (maximumEnergyConsumptionAllowedInARoom - currentEnergyConsumptionAllowedInARoom));
										currentEnergyConsumptionAllowedInARoom = maximumEnergyConsumptionAllowedInARoom;
									}
								} else {
									dimmerLight.setPowerLevel(0);
								}
						}
					}
				}
			}
		}
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {

	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {

	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {

	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {

	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {

	}
	

	/**
	 * Turn off all BinaryLight from the given location
	 * 
	 * @param 
	 * @return 
	 */
	private synchronized void turnOffLight() {
		for (BinaryLight binLight : binaryLights) {
				binLight.turnOff();
		}
		for (DimmerLight dimLight : dimmerLights) {
				dimLight.setPowerLevel(0);
		}
	}
	
	
	
	/**
	 * Return all BinaryLight from the given location
	 * 
	 * @param location
	 *            : the given location
	 * @return the list of matching BinabinaryryLights
	 */
	private synchronized List<BinaryLight> getBinaryLightFromLocation(String location) {
		List<BinaryLight> binaryLightsLocation = new ArrayList<BinaryLight>();
		for (BinaryLight binLight : binaryLights) {
			if (binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				binaryLightsLocation.add(binLight);
			}
		}
		return binaryLightsLocation;
	}

	/**
	 * Return all BinaryLight from the given location
	 * 
	 * @param location
	 *            : the given location
	 * @return the list of matching BinabinaryryLights
	 */
	private synchronized List<DimmerLight> getDimmerLightFromLocation(String location) {
		List<DimmerLight> DimmerLightsLocation = new ArrayList<DimmerLight>();
		for (DimmerLight dimLight : dimmerLights) {
			if (dimLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)) {
				DimmerLightsLocation.add(dimLight);
			}
		}
		return DimmerLightsLocation;
	}

	


	@Override
	public int getMaximumNumberOfLightsToTurnOn() {
		return maxLightsToTurnOnPerRoom;
	}

	@Override
	public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
		this.maxLightsToTurnOnPerRoom = maximumNumberOfLightsToTurnOn;
	}

	@Override
	public double getMaximumAllowedEnergyInRoom() {
		return maximumEnergyConsumptionAllowedInARoom;
	}

	@Override
	public void setMaximumAllowedEnergyInRoom(double maximumEnergy) {
		this.maximumEnergyConsumptionAllowedInARoom = maximumEnergy;
	}

	
	
	
	//zone size calculator
	
	
}
