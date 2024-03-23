import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Techncian here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Technician extends Pedestrian
{
    private static GreenfootSound[] workSounds;
    public Technician(int direction) {
        super(direction);
        
        hpBar = new SuperStatBar(maxHP, currentHP, this, 50, 10, -10-getImage().getHeight()/2, GREEN, RED, true);
    }
    
    public static void init() {
        workSounds = new GreenfootSound[8];
        for (int i = 0; i < workSounds.length; i++) {
            workSounds[i] = new GreenfootSound("work_sound.mp3");
            workSounds[i].setVolume(30);
            workSounds[i].play();
            Greenfoot.delay(1);
            workSounds[i].stop();
        }
    }
    /**
     * Act - do whatever the Techncian wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        super.act();
        if (getWorld() == null) {
            return;
        }
        
        if (awake) {
            Vehicle vehicle = (Vehicle)getOneObjectAtOffset(0, (int)(yDirection * getImage().getHeight()/2 + (int)(yDirection * ySpeed)), Vehicle.class);
            if (vehicle instanceof Ambulance) {
                if (((Ambulance)vehicle).upgrade()) {
                    playWorkSound();
                }
            } else if (vehicle instanceof Bus) {
                if (((Bus)vehicle).upgrade()) {
                    playWorkSound();
                }
            } else if (vehicle instanceof PlantTruck) {
               if (((PlantTruck)vehicle).upgrade()) {
                   playWorkSound();
               }
            } else if (vehicle instanceof RobotTruck) {
                vehicle.multiplySpeed(0.7);
                vehicle.takeDamage(10);
                playWorkSound();
            }
            Dynamite dynamite = (Dynamite)getOneObjectAtOffset(0, (int)(yDirection * getImage().getHeight()/2 + (int)(yDirection * ySpeed)), Dynamite.class);
            if (dynamite != null) {
                getWorld().removeObject(dynamite);
                playWorkSound();
            }
        }
    }
    
    private void playWorkSound() {
        for (GreenfootSound sound : workSounds) {
            if (!sound.isPlaying()) {
                sound.play();
                break;
            }
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
     * And makes the vehicle slow down
     * @param damage is the damage the pedsetrian will take
     * @param vechicle The vehicle that hit this instance
     */
    public void takeDamage(int damage, Vehicle vehicle) {
        currentHP -= damage;
        hpBar.update(currentHP);
        
        if (currentHP <= 0) {
            knockDown();
            hpBar.update(0);
        }
        
        vehicle.multiplySpeed(0.5);
    }
    
    
    /**
     * Method to allow a downed Pedestrian to be healed
     */
    public void healMe () {
        super.healMe();
        xDirection = 0;
    }
}
