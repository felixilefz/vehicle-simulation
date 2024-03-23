import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Drunk Car Subclass. The driver is drunk and will go through all cars.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DrunkCar extends Vehicle
{
    
    public DrunkCar(VehicleSpawner origin) {
        super(origin); // call the superclass' constructor
        maxSpeed = 2.0 + Greenfoot.getRandomNumber(10)/5;
        speed = maxSpeed;
        yOffset = 0;
        followingDistance = 6;
        maxHP = 15;
        currentHP = 15;
        getImage().scale(113, 49);
    }
    
    /**
     * Act - do whatever the DrunkCar wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        super.act();
    }
    
    /**
     * Since the car ingnores the person infront the drive method is different
     * Instead of slowing down, it will crash through and both cars will explode. 
     */
    public void drive() 
    {
        // Ahead is a generic vehicle - we don't know what type BUT
        // since every Vehicle "promises" to have a getSpeed() method,
        // we can call that on any vehicle to find out it's speed
        Vehicle ahead = (Vehicle) getOneObjectAtOffset (direction * (int)(speed + getImage().getWidth()/2 + 6), 0, Vehicle.class);
        double otherVehicleSpeed = -1;
        if (ahead != null) {
            otherVehicleSpeed = ahead.getSpeed();
        }
        

        

        if (otherVehicleSpeed >= 0 && otherVehicleSpeed < maxSpeed){ // Vehicle ahead is slower?
            if (ahead != null) {
                explode(ahead);
            }
        }

        else {
            speed = maxSpeed; // nothing impeding speed, so go max speed
        }
        
        if (!moving) {
            speed = 0;
        }

        move(speed * direction);
    } 
    
    private void explode(Vehicle other) {
        int otherHP = other.getHP();
        
        other.takeDamage(currentHP);
        this.takeDamage(otherHP);
        
    }
    
    
    public boolean checkHitPedestrian () {
        // Only one point because it's more generous
        Pedestrian p = (Pedestrian)getOneObjectAtOffset(((int)speed + getImage().getWidth()/2)*direction, 0, Pedestrian.class);

        if (p != null && p.isAwake()) {
            p.takeDamage(currentHP, this);
            return true;
        } 
        return false;
        
    }
}
