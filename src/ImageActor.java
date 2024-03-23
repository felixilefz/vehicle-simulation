import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * ImageActor is used to display images for mainly visual purposes.
 * May be used as a hitbox but serves no other function then that
 * 
 * @author Felix Zhao
 * @version 1.0
 */
public class ImageActor extends Actor
{
    public ImageActor(GreenfootImage image) {
        setImage(image);
    }
    
    /**
     * Since getIntersectingObjects is private this method allows other classes to see what objects the image actor is touching
     *
     * @param cls The class to be found
     * @return returns the list of intersecting objects
     */
    public <A> List<A> intersectingObjects(Class<A> cls) {
        return getIntersectingObjects(cls);
    }
}
