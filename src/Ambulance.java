import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * The Ambulance subclass
 */
public class Ambulance extends Vehicle
{
    public final static Color TRANSPARENT_GREEN = new Color(150, 255, 150, 100);
    private boolean upgraded;
    private ImageActor healingCircle;
    public Ambulance(VehicleSpawner origin){
        super (origin); // call the superclass' constructor first
        
        maxSpeed = 2.0;
        speed = maxSpeed;
        getImage().scale(134, 65);
        yOffset = 7;
        upgraded = false;
        // HP
        currentHP = 15;
        maxHP = 15;
    }

    /**
     * Act - do whatever the Ambulance wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // stored for the healing circle
        World w = getWorld();
        super.act();
        if (getWorld() == null) {
            if (healingCircle != null && healingCircle.getWorld() != null) {
                w.removeObject(healingCircle);
            }
            return;
        }
        // the extra checks arent needed but are in here just in case it gets removed for some reason
        if (upgraded && healingCircle != null && healingCircle.getWorld() != null) {
            healingCircle.setLocation(getX(), getY());
        }
    }

    public boolean checkHitPedestrian () {
        // loops through all people, only heals people
        ArrayList<Pedestrian> hitList = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);
        boolean personHit = false;
        for (int i = 0; i < hitList.size(); i++) {
            if (!hitList.get(i).isAwake()) {
                hitList.get(i).healMe();
                personHit = true;
            }
        }
        // has to be upgraded first
        if (upgraded) {
            hitList = (ArrayList<Pedestrian>)getObjectsInRange((int)(getImage().getHeight()*1.5)+2, Pedestrian.class);
            for (int i = 0; i < hitList.size(); i++) {
                if (!hitList.get(i).isAwake()) {
                    hitList.get(i).healMe();
                }
            }
        }
        
        return personHit;
    }
    
    public void takeDamage(int damage) {
        World w = getWorld();
        super.takeDamage(damage);
        if (getWorld() == null && healingCircle != null && healingCircle.getWorld() != null) {
            w.removeObject(healingCircle);
        }
    }
    
    /**
     * Upgrades the ambulance to have a healing circle
     * 
     * @return returns if the upgrade is succesful  
     *
     */
    public boolean upgrade() {
        if (upgraded) {
            return false;
        }
        
        GreenfootImage image = new GreenfootImage(getImage().getHeight()*3, getImage().getHeight()*3);
        image.setColor(TRANSPARENT_GREEN);
        image.fillOval(0, 0, getImage().getHeight()*3, getImage().getHeight()*3);
        healingCircle = new ImageActor(image);
        getWorld().addObject(healingCircle, getX(), getY());
        upgraded = true;
        return true;
    }
}
