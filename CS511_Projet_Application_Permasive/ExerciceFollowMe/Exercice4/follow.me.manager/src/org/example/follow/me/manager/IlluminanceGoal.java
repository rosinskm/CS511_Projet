package org.example.follow.me.manager;
 
/**
 * This enum describes the different illuminance goals associated with the
 * manager.
 */
public enum IlluminanceGoal {
 
    /** The goal associated with soft illuminance. */
    SOFT(1),
    /** The goal associated with medium illuminance. */
    MEDIUM(2),
    /** The goal associated with full illuminance. */
    FULL(3);
 
    /** The number of lights to turn on. */
    private int numberOfLightsToTurnOn;
 
    /**
     * Gets the number of lights to turn On.
     * 
     * @return the number of lights to turn On.
     */
    public int getNumberOfLightsToTurnOn() {
        return numberOfLightsToTurnOn;
    }
 
    /**
     * Instantiates a new illuminance goal.
     * 
     * @param numberOfLightsToTurnOn
     *            the number of lights to turn on.
     */
    private IlluminanceGoal(int numberOfLightsToTurnOn) {
        this.numberOfLightsToTurnOn = numberOfLightsToTurnOn;
    }
}