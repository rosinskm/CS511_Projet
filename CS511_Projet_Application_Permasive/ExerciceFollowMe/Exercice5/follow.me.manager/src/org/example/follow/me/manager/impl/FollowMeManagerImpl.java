package org.example.follow.me.manager.impl;

import follow.me.configuration.FollowMeConfiguration;
import org.example.follow.me.manager.*;
import java.util.Map;

import org.example.follow.me.manager.FollowMeAdministration;

public class FollowMeManagerImpl implements FollowMeAdministration {

	/** Field for IlluminanceGoal dependency */
	private FollowMeConfiguration IlluminanceGoal;
	/** Field for EnergyGoal dependency */
	private FollowMeConfiguration EnergyGoal;
	/** Injected field for the service property m_administrationService */
	private FollowMeAdministration m_administrationService;

	/** Bind Method for IlluminanceGoal dependency */
	public void bindIlluminanceGoal(FollowMeConfiguration followMeConfiguration, Map properties) {

	}

	/** Unbind Method for IlluminanceGoal dependency */
	public void unbindIlluminanceGoal(FollowMeConfiguration followMeConfiguration, Map properties) {

	}

	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component FollowMeManager is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component FollowMeManager is starting...");
	}

	@Override
	public void setIlluminancePreference(org.example.follow.me.manager.IlluminanceGoal illuminanceGoal) {
		switch (illuminanceGoal) {
		case SOFT:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(1);
			IlluminanceGoal.setTargetedIlluminance(500d);
			break;
		case MEDIUM:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(2);
			IlluminanceGoal.setTargetedIlluminance(4000d);
			break;
		case FULL:
			IlluminanceGoal.setMaximumNumberOfLightsToTurnOn(3);
			IlluminanceGoal.setTargetedIlluminance(10000d);
			break;
		}

	}

	@Override
	public IlluminanceGoal getIlluminancePreference() {
		Integer maxLightToTurnOn = IlluminanceGoal.getMaximumNumberOfLightsToTurnOn();
		if (maxLightToTurnOn == 1) {
			return org.example.follow.me.manager.IlluminanceGoal.SOFT;
		} else if (maxLightToTurnOn == 2) {
			return org.example.follow.me.manager.IlluminanceGoal.MEDIUM;
		} else if (maxLightToTurnOn == 3) {
			return org.example.follow.me.manager.IlluminanceGoal.FULL;
		} else {
			return org.example.follow.me.manager.IlluminanceGoal.FULL;
		}

	}

	@Override
	public EnergyGoal getEnergyGoal() {
		double maxEnergieUse = EnergyGoal.getMaximumAllowedEnergyInRoom();
		if (maxEnergieUse == 100d) {
			return org.example.follow.me.manager.EnergyGoal.LOW;
		} else if (maxEnergieUse == 250d) {
			return org.example.follow.me.manager.EnergyGoal.MEDIUM;
		} else if (maxEnergieUse == 1000d) {
			return org.example.follow.me.manager.EnergyGoal.HIGH;
		} else {
			return org.example.follow.me.manager.EnergyGoal.HIGH;
		}
	}

	@Override
	public void setEnergySavingGoal(org.example.follow.me.manager.EnergyGoal energyGoal) {
		switch (energyGoal) {
		case LOW:
			EnergyGoal.setMaximumAllowedEnergyInRoom(100d);
			break;
		case MEDIUM:
			EnergyGoal.setMaximumAllowedEnergyInRoom(250d);
			break;
		case HIGH:
			EnergyGoal.setMaximumAllowedEnergyInRoom(1000d);
			break;
		}

	}

}
