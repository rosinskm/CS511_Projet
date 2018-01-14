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
import fr.liglab.adele.icasa.service.zone.size.calculator.ZoneSizeCalculator;
import fr.liglab.adele.icasa.device.light.Photometer;

public class BinaryFollowMeImpl implements DeviceListener, FollowMeConfiguration {

	/** 
	 * The maximum number of lights to turn on when a user enters the room :
	 **/
	private int maxLightsToTurnOnPerRoom = 2;

	/**
	* The maximum energy consumption allowed in a room in Watt:
	**/
	private double maximumEnergyConsumptionAllowedInARoom = 250d;

	/**
	* The targeted illuminance in each room
	**/
	private double targetedIlluminance = 4000.0d;

	/**
	 * Watt to lumens conversion factor
	 * It has been considered that: 1 Watt=680.0 lumens at 555nm.
	 */
	public final static double ONE_WATT_TO_ONE_LUMEN = 680.0d;

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;
	/** Field for dimmerLights dependency */
	private DimmerLight[] dimmerLights;
	/** Field for photometers dependency */
	private Photometer[] photometers;
	/** Field for sizeCalculator dependency */
	private ZoneSizeCalculator sizeCalculator;

	
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
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor, Map<Object, Object> properties) {
		System.out.println("Unbind presence sensor " + presenceSensor.getSerialNumber());
		presenceSensor.removeListener(this);
	}

	/** Bind Method for photometer dependency */
	public void bindPhotometer(Photometer photometer, Map properties) {
		System.out.println("bind photometer " + photometer.getSerialNumber());
		photometer.addListener(this);
	}

	/** Unbind Method for photometer dependency */
	public void unbindPhotometer(Photometer photometer, Map properties) {
		System.out.println("Unbind photometer " + photometer.getSerialNumber());
		photometer.removeListener(this);
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
			dimmerLightManagment();
		}

		if (device instanceof DimmerLight) {
			System.out.println("Dimmer Light changement detected\n");
			if (propertyName.equals(LOCATION_PROPERTY_NAME)) {
				dimmerLightManagment();
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

	private synchronized void dimmerLightManagment() {
		turnOffLight();
		for (PresenceSensor preSens : presenceSensors) {
			if (preSens.getSensedPresence()) {
				String detectorLocation = (String) preSens.getPropertyValue(LOCATION_PROPERTY_NAME);

				//R be the area of the room
				double currentIlluminanceAllowedInARoom = 0;
				int nbLightOn = 0;
				double R = sizeCalculator.getSurfaceInMeterSquare(detectorLocation);
				double B = ONE_WATT_TO_ONE_LUMEN;
				double maxIlluminanceDimmer = 100 * B / R;

				List<DimmerLight> sameLocationDimmerLigths = getDimmerLightFromLocation(detectorLocation);
				for (DimmerLight dimmerLight : sameLocationDimmerLigths) {
					if (preSens.getSensedPresence()) {
						if (maxLightsToTurnOnPerRoom > nbLightOn) {
							if ((targetedIlluminance - currentIlluminanceAllowedInARoom) > 0) {

								if ((targetedIlluminance - currentIlluminanceAllowedInARoom) > maxIlluminanceDimmer) {
									dimmerLight.setPowerLevel(1);
									currentIlluminanceAllowedInARoom = currentIlluminanceAllowedInARoom + maxIlluminanceDimmer;
									nbLightOn++;
								} else {
									dimmerLight.setPowerLevel(((targetedIlluminance - currentIlluminanceAllowedInARoom) * R) / (100 * B) );
									currentIlluminanceAllowedInARoom = targetedIlluminance;
									nbLightOn++;
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
		System.out.println("Get maximum light to turn on " + maxLightsToTurnOnPerRoom);
		return maxLightsToTurnOnPerRoom;
	}

	@Override
	public void setMaximumNumberOfLightsToTurnOn(int maximumNumberOfLightsToTurnOn) {
		System.out.println("Set maximum light to turn on " + maximumNumberOfLightsToTurnOn);
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

	@Override
	public double getTargetedIlluminance() {
		System.out.println("get target illuminance is " + targetedIlluminance);
		return targetedIlluminance;
	}

	@Override
	public void setTargetedIlluminance(double illuminance) {
		System.out.println("Set target illuminance is " + illuminance);
		this.targetedIlluminance = illuminance;
	}

}
