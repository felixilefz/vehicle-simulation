import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * The Car subclass
 */
public class Car extends Vehicle
{
    
    
    public Car(VehicleSpawner origin) {
        super(origin); // call the superclass' constructor
        maxSpeed = 1.5 + Greenfoot.getRandomNumber(10)/5;
        speed = maxSpeed;
        yOffset = 4;
        followingDistance = 6;
        
        // HP
        currentHP = 5;
        maxHP = 5;
        getImage().scale(111, 55);
        // hp bar
        // hpBar = new SuperStatBar(maxHP, currentHP, this, 50, 10, -10-getImage().getHeight/()/2, GREEN, RED, true);
    }

    public void act()
    {
        super.act();
    }

    /**
     * When a Car hit's a Pedestrian, it should knock it over
     */
    public boolean checkHitPedestrian () {
        // Only one points because it's more generous
        Pedestrian p = (Pedestrian)getOneObjectAtOffset(((int)speed + getImage().getWidth()/2)*direction, 0, Pedestrian.class);
        if (p != null && p.isAwake()) {
            p.takeDamage(currentHP, this);
            return true;
        } 
        return false;
        
    }
}
