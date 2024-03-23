import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class LaserBeam here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LaserBeam extends Projectile
{
    private int duration;
    private int counter;
    
    /**
     * LaserBeam Constructor creates a projectile with a certain size
     *
     * @param damage how much damage it does
     * @param owner The actor who shoot the projectile
     * @param width sets the width of the projectile
     * @param height sets the height of the projectile
     */
    public LaserBeam(int damage, SuperSmoothMover owner, int width, int height) {
        super(0, damage, owner, width, height);
        GreenfootImage image = new GreenfootImage(width, height);
        image.setColor(new Color(100, 255, 100));
        image.fillRect(0, 0, width, height);
        duration = 60;
        counter = 0;
        setImage(image);
    }
    
    /**
     * This method is called after being put into the world and being rotated
     * Does damage to robots (percent + base) and damage to robot trucks (base damage only)
     * Removes all robot related projectile if it comes in contact
     */
    public void doDamage() {
        ArrayList<Pedestrian> pList = (ArrayList<Pedestrian>) getIntersectingObjects(Pedestrian.class);
        ArrayList<Vehicle> vList = (ArrayList<Vehicle>) getIntersectingObjects(Vehicle.class);
        ArrayList<Projectile> projectileList = (ArrayList<Projectile>) getIntersectingObjects(Projectile.class);
        
        for (Pedestrian pedestrian : pList) {
            if (pedestrian instanceof Robot) {
                pedestrian.takeDamage(damage + (int)(pedestrian.getMaxHealth() * 0.1));
            } else {
                continue;
            }
        }
        
        for (Vehicle vehicle : vList) {
            if (vehicle instanceof RobotTruck) {
                vehicle.takeDamage(damage);
            } else {
                continue;
            }
        }
        
        for (Projectile projectile : projectileList) {
            if (projectile instanceof Dynamite) {
                getWorld().removeObject(projectile);
            } else if (projectile instanceof LaserBlast) {
                getWorld().removeObject(projectile);
            }
        }
    }
    
    
    /**
     * This act method fades away the laser beam
     */
    public void act()
    {
        // Add your action code here.
        counter++;
        getImage().setTransparency((int)((1-counter/(double)duration)*255));
        if (counter >= duration) {
            getWorld().removeObject(this);
        }
    }
}
