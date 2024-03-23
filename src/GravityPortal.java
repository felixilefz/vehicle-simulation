import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class GravityPortal here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GravityPortal extends Effects
{
    private static final int VOLUME = 50;
    private static GreenfootSound[] soundList;
    private int fadeTime;
    private int fadeCounter;
    
    
    /**
     * Creates the gravity portal effect
     *
     * @param time The time the effect lasts
     * @param fadeTime Time it takes to decay into nothing, this time is added on to the time
     * @param width Width of the effect
     * @param height Height of the effect
     * @param image The image / gif of the effect (has a defult one already, so this isn't really used)
     */
    public GravityPortal(int time, int fadeTime, int width, int height, GifImage image) {
        super(time, width, height, image);
        getSoundIndex(soundList);
        this.fadeTime = fadeTime;
        fadeCounter = 0;
    }
    
    /**
     * Creates the gravity portal effect - For some reason the sound effect may not be heard even though the sound is playing
     *
     * @param time The time the effect lasts
     * @param fadeTime Time it takes to decay into nothing, this time is added on to the time
     * @param width Width of the effect
     * @param height Height of the effect
     */
    public GravityPortal(int time, int fadeTime, int width, int height) {
        super(time, width, height, new GifImage("gravity.gif"));
        getSoundIndex(soundList);
        this.fadeTime = fadeTime;
        fadeCounter = 0;
    }
    
    public static void init() {
        // capped at a low number so it doesn't sound bad at high speeds
        soundList = new GreenfootSound[5];
        for (int i = 0; i < soundList.length; i++) {
            soundList[i] = new GreenfootSound("gravity_portal_sound.mp3");
            soundList[i].setVolume(VOLUME);
            soundList[i].play();
            Greenfoot.delay(1);
            soundList[i].stop();
        }
    }
    
    public static void stopSound() {
        for (int i = 0; i < soundList.length; i++) {
            soundList[i].stop();
        }
    }
    
    /**
     * Act - do whatever the GravityPortal wants to do. This method is called whenever
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
            fadeCounter++;
            if (fadeCounter >= fadeTime) {
                if (sound != null) {
                    sound.stop();
                    sound.setVolume(VOLUME);
                }
                getWorld().removeObject(this);
                return;
            }
            double fadedPercent = (fadeTime-fadeCounter)/ (double)fadeTime;
            if (sound != null) {
                sound.setVolume((int)(VOLUME*fadedPercent));
            }
            

            for (GreenfootImage image : images) {
                image.setTransparency((int)(255*fadedPercent));
            }
        }
    }
}
