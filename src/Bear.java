import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Bear here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Bear extends Pedestrian
{
    private int type;
    private int biteCooldown;
    private int biteCounter;
    private int biteDamage;
    private static GreenfootSound[] biteSoundEffects;
    private boolean upgraded;
    private Pedestrian target;
    
    public Bear(int direction) {
        super(direction);
        maxSpeed = 1 + Math.random() * 2;
        ySpeed = maxSpeed;
        xSpeed = maxSpeed;
        upgraded = false;
        // bite numbers
        biteCooldown = 90;
        biteCounter = 0;
        biteDamage = 10;
        type = Greenfoot.getRandomNumber(2);
        
        
        // hp bar
        maxHP = 10;
        currentHP = 10;
        hpBar = new SuperStatBar(maxHP, currentHP, this, 50, 10, -10-getImage().getHeight()/2, GREEN, RED, true);
    }
    
    public static void init() {
        biteSoundEffects = new GreenfootSound[8];
        for (int i = 0; i < biteSoundEffects.length; i++) {
            biteSoundEffects[i] = new GreenfootSound("bear_bite.mp3");
            biteSoundEffects[i].setVolume(25);
            biteSoundEffects[i].play();
            Greenfoot.delay(1);
            biteSoundEffects[i].stop();
        }
    }
    
    /**
     * Act - do whatever the Bear wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        super.act();
        biteCounter++;
        if (getWorld() == null) {
            return;
        }
        if (biteCounter >= biteCooldown) {
            Vehicle vehicle = (Vehicle) getOneObjectAtOffset(0, yDirection * getImage().getHeight()/2 + (int)(yDirection * ySpeed), Vehicle.class);
            Pedestrian pedestrian = (Pedestrian) getOneObjectAtOffset(0, yDirection * getImage().getHeight()/2 + (int)(yDirection * ySpeed), Pedestrian.class);
            if (vehicle != null) {
                if (vehicle instanceof PlantTruck && !upgraded) {
                    upgraded = true;
                    maxHP = 20;
                    currentHP = maxHP;
                    hpBar.setMaxVal(maxHP);
                    hpBar.update(currentHP);
                    
                    
                    
                    getImage().scale((int)(getImage().getWidth()*1.2), (int)(getImage().getHeight() * 1.2));
                    biteDamage = 15;
                }
                vehicle.takeDamage(biteDamage);
                biteCounter = 0;
            } else if (pedestrian != null && !(pedestrian instanceof Robot)) {
                pedestrian.takeDamage(biteDamage);
                biteCounter = 0;
            }
            
            if (biteCounter == 0) {
                for (GreenfootSound sound : biteSoundEffects) {
                    if (!sound.isPlaying()) {
                        sound.play();
                        break;
                    }
                }
            }
        }
        
        if (type == 1) {
            if (target == null || !target.isAwake() || target.getWorld() == null) {
                ArrayList<Pedestrian> people = (ArrayList<Pedestrian>)getObjectsInRange(200, Pedestrian.class);
                boolean foundTarget = false;
                for (Pedestrian pedestrian : people) {
                    if (pedestrian.isAwake() && !(pedestrian instanceof Bear)) {
                        target = pedestrian;
                        foundTarget = true;
                        break;
                    }
                }
                
                if (!foundTarget) {
                    target = null;
                }
            }
            
            if (target != null) {
                if (target.getX()+5 < getX()) {
                    xDirection = -1;
                } else if (target.getX()-5 > getX()) {
                    xDirection = 1;
                } else {
                    xDirection = 0;
                }
            } else {
                xDirection = 0;
            }
            setLocation(getPreciseX()+xSpeed*xDirection, getPreciseY());
        }
        
    }
    
    /**
     * Takes damage
     *
     * @param damage The damage taken
     */
    public void takeDamage(int damage) {
        currentHP -= damage;
        hpBar.update(currentHP);
        if (currentHP <= 0) {
            getWorld().removeObject(this);
        }
    }
    
    /**
     * Takes damage and bites back at the car hit
     *
     * @param damage The damage taken
     * @param vehicle The vehicle that hit the bear
     */
    public void takeDamage(int damage, Vehicle vehicle) {
        takeDamage(damage);
        vehicle.takeDamage(biteDamage);
    }
}
