package com.exercice.temperature.command;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import com.exercices.temperature.interfaces.manager.TemperatureManagerAdministration;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;

//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "temperature.administration.command")
//Use the handler command and declare the command as a command provider. The
//namespace is used to prevent name collision.
@CommandProvider(namespace = "temperature")
public class TemperatureCommandImpl {

	// Declare a dependency to a TemperatureAdministration service
    @Requires
    private TemperatureManagerAdministration m_administrationService;
 
 
    /**
     * Command implementation to express that the temperature is too high in the given room
     *
     * @param room the given room
     */
 
    // Each command should start with a @Command annotation
    //tempTooHigh kitchen
    @Command
    public void tempTooHigh(String room) {
        m_administrationService.temperatureIsTooHigh(room);
    }
    
    //tempTooLow livingroom
    //tempTooLow kitchen
    @Command
    public void tempTooLow(String room){
    	m_administrationService.temperatureIsTooLow(room);
    }
    
    //getTemperatureMin kitchen
    //getTempMin kitchen livingroom
    @Command
    public void getTempMin(String room){
    	Double temperature = m_administrationService.getTemperatureMin(room);
    	System.out.println("Température kitchen : " + temperature);
    }
    
    //getTemperatureMax kitchen
    //getTemperatureMax livingroom
    @Command
    public void getTemperatureMax(String room){
    	System.out.println(m_administrationService.getTemperatureMax(room));
    }
    
    @Validate
    public void start() {
    	System.out.println("COMMAND is starting...");
    }
    
    @Invalidate
    public void stop() {
    	System.out.println("COMMAND is stopping...");
    }
	
}
