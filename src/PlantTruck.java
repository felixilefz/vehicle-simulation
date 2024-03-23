import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Plant Truck fights against the robots
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PlantTruck extends Vehicle
{
    private int shootCooldown;
    private int shootCounter;
    private boolean upgraded;
    private static GreenfootSound[] laserList;
    private int laserDamage;
    public PlantTruck(VehicleSpawner origin) {
        super(origin);
        maxSpeed = 1.7 + Greenfoot.getRandomNumber(5)/5;
        speed = maxSpeed;

        currentHP = 30;
        maxHP = 30;

        getImage().scale(128, 70);
        yOffset = 5;
        // laser variables
        shootCooldown = 180;
        shootCounter = 0;
        laserDamage = 10;
    }

    public static void init() {
        laserList = new GreenfootSound[5];
        for (int i = 0; i < laserList.length; i++) {
            laserList[i] = new GreenfootSound("laser.mp3");
            laserList[i].setVolume(20);
            laserList[i].play();
            Greenfoot.delay(1);
            laserList[i].play();
        }
    }

    /**
     * Act - do whatever the PlantTruck wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
        super.act();
        if (getWorld() == null) {
            return;
        }

        shootCounter++;
        if (shootCounter >= shootCooldown) {
            if (shoot()) {
                shootCounter = 0;
            }
        }
    }

    private boolean shoot() {
        ArrayList<Robot> robots = (ArrayList<Robot>) getObjectsInRange(300, Robot.class);
        if (robots.size() != 0) {
            Robot target = robots.get(0);
            int distance = (int)Math.sqrt((Math.pow(target.getX() - getX(), 2) + Math.pow(target.getY() - getY(), 2)));
            int xCenter = (int) (target.getX()+getX())/2;
            int yCenter = (int) (target.getY()+getY())/2;
            LaserBeam laser = new LaserBeam(laserDamage, this, distance+5, 12);
            getWorld().addObject(laser, xCenter, yCenter);
            laser.turnTowards(target);
            laser.doDamage();
            playLaserSound();
            return true;
        }

        ArrayList<RobotTruck> robotTrucks = (ArrayList<RobotTruck>) getObjectsInRange(200, RobotTruck.class);
        if (robotTrucks.size() != 0) {
            RobotTruck target = robotTrucks.get(0);
            int distance = (int)Math.sqrt((Math.pow(target.getX() - getX(), 2) + Math.pow(target.getY() - getY(), 2)));
            int xCenter = (int) (target.getX()+getX())/2;
            int yCenter = (int) (target.getY()+getY())/2;
            LaserBeam laser = new LaserBeam(laserDamage, this, distance, 12);
            getWorld().addObject(laser, xCenter, yCenter);
            laser.turnTowards(target);
            laser.doDamage();
            playLaserSound();
            return true;
        }
        return false;
    }

    private void playLaserSound() {
        for (GreenfootSound sound : laserList) {
            if (!sound.isPlaying()) {
                sound.play();
                break;
            }
        }
    }

    /**
     * Upgrades the plant truck
     *
     * @return Returns if the upgrade worked
     */
    public boolean upgrade() {
        if (upgraded) {
            return false;
        }
        maxHP = 50;
        currentHP = 50;
        hpBar.setMaxVal(maxHP);
        hpBar.update(currentHP);
        laserDamage = 20;
        upgraded = true;
        return true;
    }

    public boolean checkHitPedestrian() {
        boolean personHit = false;
        if (upgraded) {
            // loops through all people, only heals people when upgraded
            ArrayList<Pedestrian> hitList = (ArrayList<Pedestrian>)getIntersectingObjects(Pedestrian.class);
            for (int i = 0; i < hitList.size(); i++) {
                if (!hitList.get(i).isAwake()) {
                    hitList.get(i).healMe();
                    personHit = true;
                }
            }
        }
        return personHit;
    }
}
