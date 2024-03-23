import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DrunkMan here.
 * 
 * @author Felix Zhao
 * @version (a version number or a date)
 */
public class Person extends Pedestrian
{
    private final static int CHANGE_DIRECTION_COOLDOWN = 100; // The time needed for the drunk man to change directions
    private int directionChangeCounter;
    private boolean isDrunk;
    
    public Person(int direction) {
        super(direction);

        directionChangeCounter = 0;
        
        isDrunk = false;
        
        // hp bar - seperate in pedestrian rather than in addedtoworld since robot has different health bar
        hpBar = new SuperStatBar(maxHP, currentHP, this, 50, 10, -10-getImage().getHeight()/2, GREEN, RED, true);
    }
    
    /**
     * Updates some values beforehand
     *
     * @param w The world
     */
    public void addedToWorld(World w) {
        // has to check if it's new cause of zSort
        if (isNew) {
            super.addedToWorld(w);
            VehicleWorld world = (VehicleWorld) w;
            // 50/50 chance to be drunk at night only
            if (!world.isDay()) {
                isDrunk = Greenfoot.getRandomNumber(2) == 0 ? false : true;
            }
        }
        
    }
    
    /**
     * Act - do whatever the DrunkMan wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
        
        if (awake) {
            if (isDrunk) {
                directionChange();
            }
            
            Bus bus = (Bus)getOneObjectAtOffset(0, (int)(yDirection * getImage().getHeight()/2 + (int)(yDirection * ySpeed)), Bus.class);
            if (bus != null) {
                bus.pickUpPedestrian(this);
                return;
            }
        }
        super.act();
        
    }
    
    /**
     * The logic to check for a direction change
     */
    private void directionChange() {
        directionChangeCounter++;
        if (directionChangeCounter >= CHANGE_DIRECTION_COOLDOWN) {
            int newDirection = Greenfoot.getRandomNumber(3)-1;
            xDirection = newDirection;
            directionChangeCounter = 0;
        }
        move(xDirection*xSpeed);
        if (getX() >= getWorld().getWidth()*0.9) {
            xDirection = -1;
        } else if (getX() <= getWorld().getWidth()*0.1) {
            xDirection = 1;
        }
    }
    
    /**
     * Method to cause this Pedestrian to become knocked down - stop moving, turn onto side
     */
    public void knockDown () {
        ySpeed = 0;
        setRotation (yDirection * 90);
        awake = false;
        xSpeed = 0;
    }
    
    /**
     * Does damage to the pedestrian. If hp is 0 or less, the person gets knocked down (for projectiles)
     * @param damage is the damage the pedsetrian will take
     */
    public void takeDamage(int damage) {
        currentHP -= damage;
        hpBar.update(currentHP);
        if (currentHP <= 0) {
            hpBar.update(0);
            knockDown();
        }
    }
    
    /**
     * Does damage to the pedestrian. If hp is 0 or less, the person gets knocked down
     * vehicle does nothing in parent class, however it can be overidden to do something back to the vehicle
     * @param damage is the damage the pedsetrian will take
     * @param vechicle Has no function for person currently but other subclasses have functions for it
     */
    public void takeDamage(int damage, Vehicle vehicle) {
        currentHP -= damage;
        hpBar.update(currentHP);
        
        if (currentHP <= 0) {
            knockDown();
            hpBar.update(0);
        }
    }
    
    
    /**
     * Method to allow a downed Pedestrian to be healed
     */
    public void healMe () {
        super.healMe();
        // after being healed they are no longer drunk
        isDrunk = false;
        xDirection = 0;
    }
}
