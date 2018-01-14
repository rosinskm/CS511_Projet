package org.example.follow.me.manager.command;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.example.follow.me.manager.EnergyGoal;
import org.example.follow.me.manager.FollowMeAdministration;
import org.example.follow.me.manager.IlluminanceGoal;
//import com.example.follow.me.configuration.FollowMeConfiguration;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "follow.me.manager.command")
//Use the handler command and declare the command as a command provider. The
//namespace is used to prevent name collision.
@CommandProvider(namespace = "followme")
public class FollowMeManagerCommandImpl {

	// Declare a dependency to a FollowMeAdministration service
	@Requires
	private FollowMeAdministration m_administrationService;

	/**
	 * Felix shell command implementation to sets the illuminance preference.
	 *
	 * @param goal the new illuminance preference ("SOFT", "MEDIUM", "FULL")
	 */

	// Each command should start with a @Command annotation
	@Command
	public void setIlluminancePreference(String goal) {
		// The targeted goal
		IlluminanceGoal illuminanceGoal;
		// goal and fail if the entry is not "SOFT", "MEDIUM" or "HIGH"
		if (goal.equals("SOFT")) {
			illuminanceGoal = IlluminanceGoal.SOFT;
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else if (goal.equals("MEDIUM")) {
			illuminanceGoal = IlluminanceGoal.MEDIUM;
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else if (goal.equals("FULL")) {
			illuminanceGoal = IlluminanceGoal.FULL;
			m_administrationService.setIlluminancePreference(illuminanceGoal);
		} else {
			System.out.println("Error, please enter SOFT, MEDIUM or FULL");
		}

	}

	@Command
	public void getIlluminancePreference() {
		System.out.println("The illuminance goal is " + m_administrationService.getIlluminancePreference());
	}
	
	@Command
	public void setEnergyPreference(String goal) {
		// The targeted goal
		EnergyGoal energyGoal;
		// goal and fail if the entry is not "LOW", "MEDIUM" or "HIGH"
		if (goal.equals("LOW")) {
			energyGoal = EnergyGoal.LOW;
			m_administrationService.setEnergySavingGoal(energyGoal);
		} else if (goal.equals("MEDIUM")) {
			energyGoal = EnergyGoal.MEDIUM;
			m_administrationService.setEnergySavingGoal(energyGoal);
		} else if (goal.equals("HIGH")) {
			energyGoal = EnergyGoal.HIGH;
			m_administrationService.setEnergySavingGoal(energyGoal);
		} else {
			System.out.println("Error, please enter LOW, MEDIUM or HIGH");
		}

	}	
	
	@Command
	 public void getEnergyPreference() {
		System.out.println("The energy goal is " + m_administrationService.getEnergyGoal());
	}
	
	/** Component Lifecycle Method */
	public void stop() {
		System.out.println("Component FollowMeManagerCommand is stopping...");
	}

	/** Component Lifecycle Method */
	public void start() {
		System.out.println("Component FollowMeManagerCommand is starting...");
	}

}