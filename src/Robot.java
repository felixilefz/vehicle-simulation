import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * The robot attack other actors except other robot related actors
 * Can shoot a laser blast and has different types
 * One type can merge into a bigger robot
 * 
 * @author Felix Zhao
 * @version 1.0
 */
public class Robot extends Pedestrian
{
    private static GreenfootSound[] mergeSoundEffects;
    private static GreenfootSound[] deathSoundEffects;
    
    private int type; // determines it's AI | 0 - Regular | 1 - Semi-follow | 2 - Full follow
    private int shootCooldown = 180; // 180 - About 3 seconds in regular time
    private int cooldownCount = 0; // when this reaches shoot cooldown the robot is able to shoot something
    private int laserDamage; // the laser damage
    private Person target;
    public Robot(int direction) {
        super(direction);
        currentHP = 10;
        maxHP = 10;
        maxSpeed = 2;
        ySpeed = maxSpeed;
        xSpeed = maxSpeed;
        
        type = 1;
        laserDamage = 5;
        
        // hp bar
        hpBar = new SuperStatBar(maxHP, currentHP, this, 50, 10, -10-getImage().getHeight()/2, BLUE, RED, true);
    }
    
    public static void init() {
        mergeSoundEffects = new GreenfootSound[3];
        deathSoundEffects = new GreenfootSound[5];
        for (int i = 0; i < mergeSoundEffects.length; i++) {
            mergeSoundEffects[i] = new GreenfootSound("robot_combine.wav");
            mergeSoundEffects[i].setVolume(8);
            mergeSoundEffects[i].play();
            Greenfoot.delay(1);
            mergeSoundEffects[i].stop();
        }
        
        for (int i = 0; i < deathSoundEffects.length; i++) {
            deathSoundEffects[i] = new GreenfootSound("robot_death.wav");
            deathSoundEffects[i].setVolume(80);
            deathSoundEffects[i].play();
            Greenfoot.delay(1);
            deathSoundEffects[i].stop();
        }
    }
    
    public void addedToWorld(World w) {
        if (isNew) {
            type = Greenfoot.getRandomNumber(2);
            if (((VehicleWorld)w).getSeason() == 3) {
                type++;
            }
            
            if (type == 2) {
                setImage("big_robot.png");
                currentHP = 30;
                maxHP = 30;
                
                maxSpeed = 1.3;
                ySpeed = maxSpeed;
                xSpeed = maxSpeed;
                laserDamage = 10;
                // replaces old one
                hpBar = new SuperStatBar(maxHP, currentHP, this, 50, 10, -10-getImage().getHeight()/2, BLUE, RED, true);
            }
            super.addedToWorld(w);
            
        }
    }
    
    /**
     * Act - do whatever the Robot wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {

        
        if (type == 0) {
            super.act();
        } else if (type == 1 || type == 2) {
            if (type == 1) {
                super.act();
                if (getWorld() == null) {
                    return;
                }
            }
            
            
            if (target == null || !target.isAwake() || target.getWorld() == null) {
                ArrayList<Person> people = (ArrayList<Person>)getObjectsInRange(200, Person.class);
                boolean foundTarget = false;
                for (Person person : people) {
                    if (person.isAwake()) {
                        target = person;
                        foundTarget = true;
                        break;
                    }
                }
                
                if (!foundTarget) {
                    target = null;
                }
            }
            // +-10 so it doesn't have to be the exact x position just a close enough position. 
            if (target != null) {
                if (target.getX()+20 < getX()) {
                    xDirection = -1;
                } else if (target.getX()-20 > getX()) {
                    xDirection = 1;
                } else {
                    xDirection = 0;
                }
            } else {
                xDirection = 0;
            }
            setLocation(getPreciseX()+xSpeed*xDirection, getPreciseY());
            if (type == 2) {
                if (target != null) {
                    if (target.getY()+50 < getY()) {
                        yDirection = -1;
                    } else if (target.getY()-50 > getY()) {
                        yDirection = 1;
                    } else {
                        yDirection = 0;
                    }
                } else {
                    if (getY() < 50) {
                        yDirection = 1;
                    } else if (getY() > 750) {
                        yDirection = -1;
                    }
                }
                setLocation(getPreciseX(), getPreciseY()+(ySpeed*yDirection));
                
                // Combining of robots
                Robot otherRobot = (Robot)getOneIntersectingObject(Robot.class);
                
                
                if (otherRobot != null && otherRobot.getType() == 2) {
                    currentHP += otherRobot.getCurrentHealth()+20;
                    maxHP += otherRobot.getMaxHealth()+20;
                    shootCooldown = Math.max(Math.min(shootCooldown, otherRobot.shootCooldown)-10, 10);
                    hpBar.setMaxVal(maxHP);
                    hpBar.update(currentHP);
                    
                    laserDamage = Math.max(otherRobot.laserDamage, laserDamage)+5;
                    getWorld().removeObject(otherRobot);
                    int imageWidth = Math.max(getImage().getWidth(), otherRobot.getImage().getWidth());
                    int imageHeight = Math.max(getImage().getHeight(), otherRobot.getImage().getHeight());
                    getImage().scale(Math.min((int)(imageWidth*1.1), 138), Math.min((int)(imageHeight*1.1), 180));
                    for (GreenfootSound sound : mergeSoundEffects) {
                        if (!sound.isPlaying()) {
                            sound.play();
                            break;
                        }
                    }
                }
                
            }
        } else {
            super.act();
        }
        
        if (getWorld() == null) {
            return;
        }
        
        cooldownCount++;
        if (cooldownCount >= shootCooldown) {
            boolean hasShot = shoot();
            if (hasShot) {
                cooldownCount = 0;
            }
        }
    }
    
    /**
     * Method takeDamage
     *
     * @param damage damage taken
     * @param vehicle deals back damage to the vehicle that hit it
     */
    public void takeDamage(int damage, Vehicle vehicle) {
        vehicle.takeDamage(currentHP);
        takeDamage(damage);
    }
    
    /**
     * Method takeDamage 
     *
     * @param damage damage taken
     */
    public void takeDamage(int damage) {
        currentHP -= damage;
        hpBar.update(currentHP);
        if (currentHP <= 0) {
            getWorld().removeObject(this);
            for (GreenfootSound sound : deathSoundEffects) {
                if (!sound.isPlaying()) {
                    sound.play();
                    break;
                }
            }
        }
    }
    
    /**
     * Returns the type of robot the instance is
     *
     * @return returns the type of the robot
     */
    public int getType() {
        return type;
    }
    
    /**
     * Method shoot This method shoots at a pedestrian or a vehicle
     *
     * @return returns false if the robot did not shoot, returns true if the robot has shot something
     */
    private boolean shoot() {
        ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>)getObjectsInRange(400, Vehicle.class);
        ArrayList<Pedestrian> pedestrians = (ArrayList<Pedestrian>)getObjectsInRange(400, Pedestrian.class);
        if (target != null && target.getWorld() != null) {
            LaserBlast blast = new LaserBlast(5, laserDamage, this, 48, 48);
            getWorld().addObject(blast, getX(), getY());
            blast.turnTowards(target.getX(), target.getY());
            return true;
        } else if (vehicles.size() * 2 >= pedestrians.size()) { // if there's at least a vehicle for every 2 people, shoot a vehicle 
            for (int i = 0; i < vehicles.size(); i++) {
                if (vehicles.get(i) instanceof RobotTruck) {
                    continue;
                }
                LaserBlast blast = new LaserBlast(5, laserDamage, this, 48, 48);
                getWorld().addObject(blast, getX(), getY());
                blast.turnTowards(vehicles.get(i).getX(), vehicles.get(i).getY());
                return true;
            }
        } else {
            for (int i = 0; i < pedestrians.size(); i++) {
                if (pedestrians.get(i) instanceof Robot || !pedestrians.get(i).isAwake()) {
                    continue;
                }
                LaserBlast blast = new LaserBlast(5, laserDamage, this, 48, 48);
                getWorld().addObject(blast, getX(), getY());
                blast.turnTowards(pedestrians.get(i).getX(), pedestrians.get(i).getY());
                return true;
            }
        }
        return false;
    }
    
    
}
