import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Effects here.
 * 
 * @author Felix Zhao
 * @version (a version number or a date)
 */
public abstract class Effects extends Actor
{
    protected int time;
    protected int timeCounter;
    protected int width;
    protected int height;
    protected GifImage image;
    protected ArrayList<GreenfootImage> images; // Has all the images from the gif so the effect can be played correctly depending on the world speed. 
    protected GreenfootSound sound;
    protected boolean isNew; // checks if the object is new and not from zSort

    
    protected int imageCount = 0; // used to switch images
    
    public Effects(int time, int width, int height, GifImage image) {
        this.time = time;
        this.width = width;
        this.height = height;
        this.image = image;
        
        for (GreenfootImage frame : image.getImages()) {
            frame.scale(width, height);
        }
        images = (ArrayList<GreenfootImage>)image.getImages();
        isNew = true;
        timeCounter = 0;
    }
    
    public abstract void act();
    
    /**
     * Finds the first sound object that isn't being playing in the given sound list
     *
     * @param soundList The list of all sounds in a specific effect sublcass
     */
    protected void getSoundIndex(GreenfootSound[] soundList) {
        for (int i = 0; i < soundList.length; i++) {
            if (!soundList[i].isPlaying()) {
                sound = soundList[i];
                break;
            }
        }
    }
    
    public void addedToWorld(World w) {
        if (sound != null && isNew) {
            
            sound.play();
            isNew = false;
            
        }
    }
}
