import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Dynamite stays on the road and explodes on contact with another vehicle. When it explodes it summons a gravity portal
 * The hitbox of the gravity portal is the hitbox of the dynamite (uses itself to detect)
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Dynamite extends Projectile
{
    private boolean exploded;
    private int time;
    private int timeCounter;
    private HashSet<SuperSmoothMover> hitList = new HashSet<SuperSmoothMover>();
    
    /**
     * Creates dynamite
     *
     * @param damage Damage it does 
     * @param owner The owner is the actor who summoned the this projectile
     */
    public Dynamite(int damage, SuperSmoothMover owner) {
        super(0, damage, owner);
        exploded = false;
        time = 300;
        timeCounter = 0;

    }
    
    /**
     * Creates dynamite with certain dimensions 
     *
     * @param damage Damage it does 
     * @param owner The owner is the actor who summoned the this projectile
     * @param width Width of the image
     * @param height Height of the image
     */
    public Dynamite(int damage, SuperSmoothMover owner, int width, int height) {
        super(0, damage, owner, width, height);
        exploded = false;
        time = 300;
        timeCounter = 0;
    }
    
    /**
     * Checks collisions with vehicles and if it interacts with one it explodes and summons another projectile
     */
    public void act()
    {
        Vehicle vehicle = (Vehicle)getOneIntersectingObject(Vehicle.class);
        if (!exploded && vehicle != null && !(vehicle instanceof RobotTruck)) {
            GravityPortal gravity = new GravityPortal(time, 60, 96, 80);
            getWorld().addObject(gravity, getX(), getY());
            vehicle.takeDamage(15);
            vehicle.multiplySpeed(0.5);
            exploded = true;
            getImage().setTransparency(0); 
            hitList.add(vehicle);
        }
        
        if (exploded) {
            timeCounter++;
            Pedestrian p = (Pedestrian)getOneIntersectingObject(Pedestrian.class);
            Robot r = (Robot)getOneIntersectingObject(Robot.class);
            if (timeCounter >= time) {
                getWorld().removeObject(this);
                return;
            }
            if (vehicle != null && !hitList.contains(vehicle) && !(vehicle instanceof RobotTruck)) {
                vehicle.multiplySpeed(0.5);
                vehicle.takeDamage(5);
                hitList.add(vehicle);
                // System.out.println(hitList);
                // System.out.println(vehicle);
            }
            
            if (p != null && !hitList.contains(p)) {
                if (p instanceof Robot) {
                    p.healByHP(5);
                } else {
                    p.takeDamage(5);
                }
                hitList.add(p);
                
            }
            
            
            
        }
        
        
    }
}
