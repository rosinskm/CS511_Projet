package org.example.follow.me.manager;
 
/**
 * This enum describes the different energy goals associated with the
 * manager.
 */
public enum EnergyGoal {
    LOW(100d), MEDIUM(250d), HIGH(1000d);
 
    /**
     * The corresponding maximum energy in watt
     */
    private double maximumEnergyInRoom;
 
    /**
     * get the maximum energy consumption in each room
     * 
     * @return the energy in watt
     */
    public double getMaximumEnergyInRoom() {
        return maximumEnergyInRoom;
    }
 
    private EnergyGoal(double powerInWatt) {
        maximumEnergyInRoom = powerInWatt;
    }
}