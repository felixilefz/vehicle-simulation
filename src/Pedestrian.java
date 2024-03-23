import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Pedestrian that tries to walk across the street
 */
public abstract class Pedestrian extends SuperSmoothMover
{
    // colour constants
    public static Color GREEN = new Color(10, 255, 10);
    public static Color RED = new Color(255, 10, 10);
    public static Color BLUE = new Color(50, 100, 255);
    
    protected double ySpeed;
    protected double maxSpeed;
    protected int yDirection; // direction is always -1 or 1, for moving down or up, respectively
    protected boolean awake, entering;
    protected int maxHP = 1;
    protected int currentHP = 1;
    protected int timeToDecay = 1200; // Negative numbers represent infinity
    protected int decayTimer = 0; // time for pedestrian to decay and die with no help
    protected SuperStatBar hpBar;
    protected double xSpeed;
    protected boolean isNew;
    protected int xDirection;
    
    public Pedestrian(int direction) {
        // choose a random speed
        maxSpeed = Math.random() * 2 + 1;
        ySpeed = maxSpeed;
        xSpeed = maxSpeed;
        // start as awake 
        awake = true;
        entering = true;
        yDirection = direction;
        xDirection = 0;
        isNew = true;
    }

    protected abstract void takeDamage(int damage, Vehicle vehicle);
    
    protected abstract void takeDamage(int damage);
    
    public static void init() {
        Robot.init();
        Bear.init();
        Technician.init();
    }
    
    /**
     * Adds the things that could not be added in the constructor due the lack of a world
     *
     * @param world The world
     */
    public void addedToWorld(World world) {
        if (isNew) {
            world.addObject(hpBar, getX(), getY());
            isNew = false;
        }
    }

    /**
     * Act - do whatever the Pedestrian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // stops if the person gets removed right before this gets run
        if (getWorld() == null) {
            return;
        }
        
        // Awake is false if the Pedestrian is "knocked down"
        if (awake){
            // Check in the direction I'm moving vertically for a Vehicle -- and only move if there is no Vehicle in front of me.
            if (getOneObjectAtOffset(0, (int)(yDirection * getImage().getHeight()/2 + (int)(yDirection * ySpeed)), Vehicle.class) == null){
                setLocation (getPreciseX(), getPreciseY() + (ySpeed*yDirection));
            }
            
            if (yDirection == -1 && getY() < 100){
                getWorld().removeObject(this);
            } else if (yDirection == 1 && getY() > getWorld().getHeight() - 30){
                getWorld().removeObject(this);
            }

        } else {
            decayTimer++;
            // The second condition is for the option to have no decay time at all
            if (decayTimer >= timeToDecay && timeToDecay >= 0) {
                getWorld().removeObject(this);
            }
        }
    }



    /**
     * Method to allow a downed Pedestrian to be healed
     * 
     */
    public void healMe () {
        ySpeed = maxSpeed;
        xSpeed = maxSpeed;
        setRotation(0);
        awake = true;
        currentHP = Math.max(maxHP, 1);
        decayTimer = 0;
        hpBar.update(currentHP);
    }
    
    /**
     * Similar to "Healme" however instead of recovering to max health it heals by a number
     *
     * @param heal How much to heal by
     */
    public void healByHP(int heal) {
        currentHP = Math.min(maxHP, currentHP+heal);
    }

    public boolean isAwake () {
        return awake;
    }
    
    /**
     * Returns the max health of the pedestrian
     *
     * @return Returns the max health of the pedestrian
     */
    public int getMaxHealth() {
        return maxHP;
    }
    
    /**
     * Returns current health
     *
     * @return Returns current health
     */
    public int getCurrentHealth() {
        return currentHP;
    }
    
    
}
