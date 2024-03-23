import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Explosion here.
 * 
 * @author Felix Zhao
 * @version (a version number or a date)
 */
public class Explosion extends Effects
{
    private static final int VOLUME = 70;
    private static GreenfootSound[] soundList;
    
    
    public Explosion(int time, int width, int height, GifImage image) {
        super(time, width, height, image);
        getSoundIndex(soundList);
    }
    
    public Explosion(int time, int width, int height) {
        super(time, width, height, new GifImage("explosion.gif"));
        getSoundIndex(soundList);
    }
    
    public static void init() {
        // capped at a low number so it doesn't sound bad at high speeds
        soundList = new GreenfootSound[8];
        for (int i = 0; i < soundList.length; i++) {
            soundList[i] = new GreenfootSound("explosion_sound.wav");
            soundList[i].setVolume(VOLUME);
            soundList[i].play();
            Greenfoot.delay(1);
            soundList[i].stop();
        }
    }
    
    /**
     * Act - do whatever the Explosion wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // will change images every 8 acts
        imageCount++;
        imageCount %= images.size()*8;
        setImage(images.get(imageCount/8));
        timeCounter++;
        if (timeCounter >= time) {
            getWorld().removeObject(this);
        }
    }
}
