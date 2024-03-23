import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class LaserBlast here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class LaserBlast extends Projectile
{
    /**
     * Creates a laser blast
     *
     * @param speed how fast the projectile goes
     * @param damage how much damage it does
     * @param owner The actor who shoot the projectile
     */
    public LaserBlast(int speed, int damage, SuperSmoothMover owner) {
        super(speed, damage, owner);
    }
    
    /**
     * LaserBlast Constructor creates a projectile with a certain size
     *
     * @param speed how fast the projectile goes
     * @param damage how much damage it does
     * @param owner The actor who shoot the projectile
     * @param width sets the width of the projectile
     * @param height sets the height of the projectile
     */
    public LaserBlast(int speed, int damage, SuperSmoothMover owner, int width, int height) {
        super(speed, damage, owner, width, height);
    }
    
    public void act()
    {
        super.act();
        if (getWorld() == null) {
            return;
        }
        ArrayList<Vehicle> objectVehicles = (ArrayList<Vehicle>)getIntersectingObjects(Vehicle.class);
        ArrayList<Pedestrian> objectPedestrians = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);
        // priotizes vehicles over pedestrians
        for (int i = 0; i < objectVehicles.size(); i++) {
            if (objectVehicles.get(i) instanceof RobotTruck) {
                ((RobotTruck)objectVehicles.get(i)).gainAmmo();
                getWorld().removeObject(this);
                return;
            }
            objectVehicles.get(i).takeDamage(damage);
            Explosion explosion = new Explosion(50, 100, 100);
            getWorld().addObject(explosion, getX(), getY());
            getWorld().removeObject(this);
            return;
        }
        
        for (int i = 0; i < objectPedestrians.size(); i++) {
            if (objectPedestrians.get(i) == owner || !objectPedestrians.get(i).isAwake() || objectPedestrians.get(i) instanceof Robot) {
                continue;
            }
            objectPedestrians.get(i).takeDamage(damage);
            Explosion explosion = new Explosion(50, 100, 100);
            getWorld().addObject(explosion, getX(), getY());
            getWorld().removeObject(this);
            return;
        }
    }
}
