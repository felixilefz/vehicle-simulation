import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RobotTruck here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RobotTruck extends Vehicle
{
    protected int ammo;
    protected double distanceTraveled;
    
    public RobotTruck(VehicleSpawner origin) {
        super(origin);
        ammo = 1;
        distanceTraveled = 0; // first bomb can be dropped sooner when it's above 0
        
        maxSpeed = 1.5 + Greenfoot.getRandomNumber(10)/5;;
        speed = maxSpeed;
        yOffset = 4;
        followingDistance = 6;
        getImage().scale(174, 67);
        
        // HP
        currentHP = 30;
        maxHP = 30;
    }
    
    public void addedToWorld(World w) {
        if (isNew) {
            super.addedToWorld(w);
            if (((VehicleWorld)w).getSeason() == 3) {
                ammo++;
            }
        }
    }
    
    /**
     * First calls the parent act method
     * Then checks if it should drop bombs on the road. The chance is dependent on how far the car has traveled when it last dropped bombs (starts at 0) 
     */
    public void act() {
        super.act();
        if (getWorld() == null) {
            return;
        }
        distanceTraveled += speed;
        int dropBombCheck = Greenfoot.getRandomNumber((int)(distanceTraveled-200)/55+200);
        if (dropBombCheck > 201 && ammo > 0) {
            dropBomb();
            distanceTraveled = 0;
            ammo--;
        }
    }
    
    private void dropBomb() {
        Dynamite dynamite = new Dynamite(15, this, 38, 48);
        VehicleWorld w = (VehicleWorld)getWorld();
        if ((Greenfoot.getRandomNumber(2) == 0 && myLaneNumber >= 1) || myLaneNumber == 5) {
            w.addObject(dynamite, getX(), w.getLaneY(myLaneNumber-1));
        } else {
            w.addObject(dynamite, getX(), w.getLaneY(myLaneNumber+1));
        }
    }
    
    public void gainAmmo() {
        ammo++;
    }
    
    public boolean checkHitPedestrian() {
        Pedestrian p = (Pedestrian)getOneObjectAtOffset((int)speed + getImage().getWidth()/2, 0, Pedestrian.class);
        if (p != null && p.isAwake() && !(p instanceof Robot))
        {
            p.takeDamage(currentHP, this);
            return true;
        }
        return false;
    }
}
