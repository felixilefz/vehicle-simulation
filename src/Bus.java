import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * The Bus subclass
 */
public class Bus extends Vehicle
{
    public final static Color TRANSPARENT_BLUE = new Color(150, 150, 255, 50);
    // Timer is measured in acts
    private int stopTimer = 0;
    private int maxStopTime = 60; // In acts
    private int capacity = 0;
    private int maxCapacity = 5;
    private boolean upgraded;
    ArrayList<Pedestrian> passengerList = new ArrayList<Pedestrian>(); 
    private ImageActor forceField;

    public Bus(VehicleSpawner origin){
        super (origin); // call the superclass' constructor first

        //Set up values for Bus
        maxSpeed = 1.5 + Greenfoot.getRandomNumber(5)/5;
        speed = maxSpeed;
        maxCapacity += (int) (Math.random()*5);
        // because the Bus graphic is tall, offset it a up (this may result in some collision check issues)
        yOffset = 10;
        getImage().scale(170, 77);

        // HP
        currentHP = 25;
        maxHP = 25;
    }

    /**
     * Act - do whatever the Bus wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // world stored to be able to remove force field if bus is destoryed / removed
        World w = getWorld();
        super.act();
        if (!moving) {
            if (stopTimer > 0) {
                stopTimer --;
            } else {
                moving = true;
            }
        }
        if (getWorld() == null) {
            if (forceField != null && forceField.getWorld() != null) {
                w.removeObject(forceField);
            }
            return;
        }
        // the extra checks arent needed but are in here just in case it gets removed for some reason
        if (upgraded && forceField != null && forceField.getWorld() != null) {
            forceField.setLocation(getX(), getY());
            ArrayList<LaserBlast> laserBlasts = (ArrayList<LaserBlast>)getObjectsInRange((int)(getImage().getHeight()*1.5), LaserBlast.class);
            for (LaserBlast blast : laserBlasts) {
                getWorld().removeObject(blast);
            }
        }
    }

    /**
     * Checks for a pedestrain in front
     * If there is pickup the pedestrain if the bus is not full
     */
    public boolean checkHitPedestrian () {
        // two points are used - not more because pedestrian will still most likely enter from the sides
        Person p1 = (Person)getOneObjectAtOffset(((int)speed + getImage().getWidth()/2)*direction, getImage().getHeight()/4, Person.class);
        Person p2 = (Person)getOneObjectAtOffset(((int)speed + getImage().getWidth()/2)*direction, -getImage().getHeight()/4, Person.class);
        if (p1 != null && p1.isAwake())
        {
            boolean pickedUp = pickUpPedestrian(p1);
            return pickedUp;
        } else if (p2 != null && p2.isAwake()) {
            boolean pickedUp = pickUpPedestrian(p2);
            return pickedUp;
        }
        return false;
    }

    /**
     * Picks up the pedestrain given. If full return false and does nothing
     * If there's space the bus will pick the person up and stop for the maxStopTime in acts and returns true
     */
    public boolean pickUpPedestrian(Pedestrian p) {
        if (capacity >= maxCapacity) {
            return false;
        }
        moving = false;
        getWorld().removeObject(p);
        stopTimer = maxStopTime;
        capacity += 1;
        return true;
    }

    /**
     * Upgrades the bus
     *
     * @return Returns if upgrade works
     */
    public boolean upgrade() {
        if (upgraded) {
            return false;
        }

        GreenfootImage image = new GreenfootImage(getImage().getHeight()*3, getImage().getHeight()*3);
        image.setColor(TRANSPARENT_BLUE);
        image.fillOval(0, 0, getImage().getHeight()*3, getImage().getHeight()*3);
        forceField = new ImageActor(image);
        getWorld().addObject(forceField, getX(), getY());
        upgraded = true;
        return true;
    }
    
    public void takeDamage(int damage) {
        World w = getWorld();
        super.takeDamage(damage);
        
        if (getWorld() == null && forceField != null && forceField.getWorld() != null) {
            w.removeObject(forceField);
        }
    }
}
