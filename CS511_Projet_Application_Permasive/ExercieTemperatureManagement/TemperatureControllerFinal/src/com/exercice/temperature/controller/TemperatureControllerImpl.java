package com.exercice.temperature.controller;

import java.util.concurrent.TimeUnit;

import com.exercice.temperature.interfaces.configuration.TemperatureConfiguration;

import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;
import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Cooler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemperatureControllerImpl implements DeviceListener, PeriodicRunnable, TemperatureConfiguration {

	public static final String LOCATION_PROPERTY_NAME = "Location";
	public static final String LOCATION_UNKNOWN = "unknown";

	/** Field for heaters dependency */
	private Heater[] heaters;
	/** Field for thermometers dependency */
	private Thermometer[] thermometers;
	/** Field for coolers dependency */
	private Cooler[] coolers;

	private Double targetTemperatureKitchen = 300.0;
	private Double targetTemperatureLivingroom = 295.0;
	private Double targetTemperatureBathroom = 289.0;
	private Double targetTemperatureBedroom = 291.0;

	@Override
	public void run() {

		System.out.println("Run");

		for (Thermometer thermometer : thermometers) {

			String thermoLocation = (String) thermometer.getPropertyValue(LOCATION_PROPERTY_NAME);

			List<Heater> heatersInLocation = findHeatersInRoom(thermoLocation);
			List<Cooler> coolersInLocation = findCoolersInRoom(thermoLocation);
			Double temperature = findTemperatureInRoom(thermoLocation);

			System.out.println("Temperature in " + thermoLocation + " = " + temperature);

			if (temperature > getTargetedTemperature(thermoLocation) + 0.5) {
				for (Heater heater : heatersInLocation) {
					heater.setPowerLevel(0);
				}
				for (Cooler cooler : coolersInLocation) {
					cooler.setPowerLevel(1);
				}
			} else {
				if (temperature < getTargetedTemperature(thermoLocation) - 0.5) {
					for (Heater heater : heatersInLocation) {
						heater.setPowerLevel(1);
					}
					for (Cooler cooler : coolersInLocation) {
						cooler.setPowerLevel(0);
					}
				} else {
					
					Double gain = Math.abs(getTargetedTemperature(thermoLocation) - temperature);
					gain = (gain*100)/getTargetedTemperature(thermoLocation);
					
					if(temperature < getTargetedTemperature(thermoLocation)) {			
						for (Heater heater : heatersInLocation) {
							heater.setPowerLevel(gain);
						}
						for (Cooler cooler : coolersInLocation) {
							cooler.setPowerLevel(0);
						}
					} else {
						for (Heater heater : heatersInLocation) {
							heater.setPowerLevel(0);
						}
						for (Cooler cooler : coolersInLocation) {
							cooler.setPowerLevel(gain);
						}
					}
					
				}
			}
		}
	}

	public Double findTemperatureInRoom(String room) {

		List<Thermometer> listThermo = new ArrayList<Thermometer>();
		Double temperature = 0.0;

		for (Thermometer thermometer : thermometers) {
			if (thermometer.getPropertyValue(LOCATION_PROPERTY_NAME).equals(room)) {
				listThermo.add(thermometer);
			}
		}

		for (Thermometer thermo : listThermo) {
			temperature += thermo.getTemperature();
		}

		temperature = temperature / listThermo.size();

		if (listThermo.isEmpty()) {
			return null;
		} else {
			return temperature;
		}
	}

	public List<Cooler> findCoolersInRoom(String room) {

		List<Cooler> liste = new ArrayList<Cooler>();
		for (Cooler cooler : coolers) {
			if (cooler.getPropertyValue(LOCATION_PROPERTY_NAME).equals(room)) {
				liste.add(cooler);
			}
		}

		if (liste.isEmpty()) {
			return null;
		} else {
			return liste;
		}

	}

	public List<Heater> findHeatersInRoom(String room) {

		List<Heater> liste = new ArrayList<Heater>();
		for (Heater heater : heaters) {
			if (heater.getPropertyValue(LOCATION_PROPERTY_NAME).equals(room)) {
				liste.add(heater);
			}
		}
		if (liste.isEmpty()) {
			return null;
		} else {
			return liste;
		}
	}

	@Override
	public long getPeriod() {
		//  
		return 10;
	}

	@Override
	public TimeUnit getUnit() {
		//  
		return TimeUnit.SECONDS;
	}

	/** Bind Method for coolers dependency */
	public void bindCooler(Cooler cooler, Map<Object, Object> properties) {
		System.out.println("bindCooler " + cooler.getSerialNumber());
		cooler.addListener(this);
	}

	/** Unbind Method for coolers dependency */
	public void unbindCooler(Cooler cooler, Map<Object, Object> properties) {
		System.out.println("unbindCooler " + cooler.getSerialNumber());
		cooler.removeListener(this);
	}

	/** Bind Method for heaters dependency */
	public void bindHeater(Heater heater, Map<Object, Object> properties) {
		System.out.println("bindHeater " + heater.getSerialNumber());
		heater.addListener(this);
	}

	/** Unbind Method for heaters dependency */
	public void unbindHeater(Heater heater, Map<Object, Object> properties) {
		System.out.println("unbindHeater " + heater.getSerialNumber());
		heater.removeListener(this);
	}

	/** Bind Method for thermometers dependency */
	public void bindThermometer(Thermometer thermometer, Map<Object, Object> properties) {
		System.out.println("bindThermometer " + thermometer.getSerialNumber());
		thermometer.addListener(this);
	}

	/** Unbind Method for thermometers dependency */
	public void unbindThermometer(Thermometer thermometer, Map<Object, Object> properties) {
		System.out.println("unbindThermometer " + thermometer.getSerialNumber());
		thermometer.removeListener(this);
	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("CONTROLLER is stopping...");
		for(Thermometer thermometer : thermometers) {
			thermometer.removeListener(this);
		}
		for(Heater heater : heaters) {
			heater.removeListener(this);
		}
		for(Cooler cooler : coolers) {
			cooler.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("CONTROLLER is starting...");
	}

	@Override
	public void setTargetedTemperature(String targetedRoom, Double temperature) {

		if (targetedRoom.equals("kitchen")) {
			targetTemperatureKitchen = temperature;
		} else if (targetedRoom.equals("bedroom")) {
			targetTemperatureBedroom = temperature;
		} else if (targetedRoom.equals("livingroom")) {
			targetTemperatureLivingroom = temperature;
		} else if (targetedRoom.equals("bathroom")) {
			targetTemperatureBathroom = temperature;
		} else {
			//
		}
	}

	@Override
	public Double getTargetedTemperature(String room) {

		if (room.equals("kitchen")) {
			return targetTemperatureKitchen;
		} else if (room.equals("bedroom")) {
			return targetTemperatureBedroom;
		} else if (room.equals("livingroom")) {
			return targetTemperatureLivingroom;
		} else if (room.equals("bathroom")) {
			return targetTemperatureBathroom;
		} else {
			return null;
		}
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
	public void devicePropertyModified(GenericDevice device, String nameProperty, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub
		if(device instanceof Heater) {
			if(nameProperty.equals(Heater.LOCATION_PROPERTY_NAME)) {
				System.out.println("old : " + oldValue + ". New : " + newValue);
			}
			
		}
		
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
