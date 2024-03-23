import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Projectile here.
 * 
 * @author Felix
 * @version 1.0
 */
public abstract class Projectile extends SuperSmoothMover
{
    protected int speed;
    protected int damage;
    protected SuperSmoothMover owner;

    /**
     * Projectile Constructor creates a projectile
     *
     * @param speed how fast the projectile goes
     * @param damage how much damage it does
     * @param owner The actor who shoot the projectile
     */
    public Projectile(int speed, int damage, SuperSmoothMover owner) {
        this.speed = speed;
        this.damage = damage;
        this.owner = owner;
    }
    
    
    /**
     * Projectile Constructor creates a projectile with a certain size
     *
     * @param speed how fast the projectile goes
     * @param damage how much damage it does
     * @param owner The actor who shoot the projectile
     * @param width sets the width of the projectile
     * @param height sets the height of the projectile
     */
    public Projectile(int speed, int damage, SuperSmoothMover owner, int width, int height) {
        this.speed = speed;
        this.damage = damage;
        this.owner = owner;
        getImage().scale(width, height);
    }
    
    
    
    
    /**
     * Act - do whatever the Projectile wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        move(speed);
        if (isAtEdge()) {
            getWorld().removeObject(this);
        }
    }
    
}
