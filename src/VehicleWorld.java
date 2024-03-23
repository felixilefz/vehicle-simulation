import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Collections;
import java.util.ArrayList;
/**
 * <h1>The new and vastly improved 2022 Vehicle Simulation Assignment.</h1>
 * <p> This is the first redo of the 8 year old project. Lanes are now drawn dynamically, allowing for
 *     much greater customization. Pedestrians can now move in two directions. The graphics are better
 *     and the interactions smoother.</p>
 * <p> The Pedestrians are not as dumb as before (they don't want straight into Vehicles) and the Vehicles
 *     do a somewhat better job detecting Pedestrians.</p>
 * 
 * Version Notes - Feb 2023
 * --> Includes grid <--> lane conversion method
 * --> Now starts with 1-way, 5 lane setup (easier)
 * 
 * V2023_021
 * --> Improved Vehicle Repel (still work in progress)
 * --> Implemented Z-sort, disabled paint order between Pedestrians and Vehicles (looks much better now)
 * --> Implemented lane-based speed modifiers for max speed
 * 
 * V2023_04
 * --> Repel has been re-imagined and now takes the sizes of Actors into consideration better, and also only
 *     moves Actors verically. (The code to move in both dimensions is there and works but it's commented out
 *     because this is the effect I was going for).
 * --> TODO -- Improve flow to avoid Removed From World errors when a Vehicle calls super.act() and is removed there.
 * 
 * 
 * (1) List of Features
 * There is a day/night system plus a season system. These systems change the spawnrates of actors and some other attributes of the actors (such as speed)
 * There are filters for the seasons and day and night. (Visual)
 * The starting season be changed via the "seasonNumber" variable 
 * Each season has a "theme". Winter is the robot invasion and spring is the robot cleanup (as example)
 * Drunk cars crash through other cars, robot trucks drop dynamite which explode into a portal that deals damage and slows down other vehicles
 * Robots shoot laser blasts that deal damage. Some robots follow pedestrians and in winter some robots are bigger and will fully follow pedestrians
 * and they can merge into bigger and stronger robots
 * Some instances of Person can be drunk at night which means they move randomly in the x direction
 * Bears will eat other actors and when eating plant truck it's stats get boosted
 * Plant trucks shoot laser beams at robots and robot trucks. They do percent plus flat damage to robots
 * Ambulences are spawned (if possible) in all lanes when enough pedestrians are knocked down. (Ambulences can still spawn naturally)
 * Pedestrian will decay if knocked down for too long
 * Technician will upgrade certain vehicles making them stronger / faster
 * - Ambulances will a get healing circle that extend their healing hitbox
 * - Buses get a force field that protects them from laser blasts
 * - Plant trucks stats are increased and can heal pedestrians like an ambulance
 * Technicians also sabotage robot trucks and remove dynamite from the ground
 * 
 * (2) Sources (Link - Author / Website)
 * Art:
 * https://www.pixilart.com/art/pixel-bear-86578693f8ae4a8 - ethanf30games
 * https://ragnapixel.itch.io/particle-fx - ragnapixel
 * https://free-game-assets.itch.io/free-truck-constructor-pixel-art - itch.io
 * https://lvgames.itch.io/free-glowing-ball-sprite-pixel-fx-rpg-maker-ready - lvgames
 * https://tumas81.itch.io/minerman-adventure - Tumas81
 * https://free-game-assets.itch.io/free-summer-pixel-art-backgrounds - itch.io
 * https://free-game-assets.itch.io/free-townspeople-cyberpunk-pixel-art - itch.io
 * https://opengameart.org/content/pixel-robot - David Harrington
 * 
 * Sounds (No specific authors):
 * https://mixkit.co - mixkit
 * https://pixabay.com/sound-effects - pixabay
 * 
 * Code:
 * SuperStatBar by Jorden Cohen
 * 
 * (3) Known Bugs
 * (Not really a bug) Plant Trucks can spawn camp robots and it likes like it targeted nothing
 * Vehicles may be able to pass through each other for some reason (seems very rare to occur, might know why but not entirely sure and might be fixed but it's hard to tell)
 * 
 * 
 * 
 */
public class VehicleWorld extends World
{
    private GreenfootImage mainBackground;
    private ImageActor[] filters;

    // Color Constants
    public static final Color GREY_BORDER = new Color (108, 108, 108);
    public static final Color GREY_STREET = new Color (88, 88, 88);
    public static final Color YELLOW_LINE = new Color (255, 216, 0);

    // All the colour filters in one array. First filter is releated to day and night. The other four are season related
    public static final Color[] COLOUR_FILTERS = new Color[]{new Color (0, 0, 0, 130), new Color(200, 250, 200, 60), 
            new Color (250, 250, 200, 60), new Color(250, 160, 0, 60), new Color(50, 50, 50, 60)};
    // Spawn rates - Should add up to 100 in each of the arrays
    // Ambulence, Bus, Car, Drunk Car, Robot Truck, Plant Truck - Order of each spawn chance
    public static final int[][] VEHICLE_SPAWN_RATES = new int[][] {
            {5, 25, 30, 10, 10, 20},
            {5, 20, 32, 25, 2, 16},
            {5, 35, 45, 0, 10, 5},
            {5, 35, 30, 10, 20, 0}
        };
    // This adds to the spawn rates above when at night - Negative means less likely to spawn - Postive means more likely
    // Beware of values that will make the values above negative as that might cause werid issues. Same thing with too high of values
    // There are more factors to spawning than the seasons and the time of day. Although they aren't listed since they are very specific
    public static final int[] VEHICLE_NIGHT_SPAWN_BONUS = new int[] {5, -20, 0, 15, 0, 0};

    // Person, Robot - Order of each spawn choice 
    public static final int[][] PEDESTRIAN_SPAWN_RATES = new int[][] {
            {50, 10, 20, 20},
            {65, 5, 20, 10},
            {50, 20, 10, 20},
            {50, 40, 0, 10}
        };
    // Robots spawn more often at night
    public static final int[] PEDESTRIAN_NIGHT_SPAWN_BONUS = new int[] {-10, 10, 0, 0};
    // when rolling a random number to decide if a vehicle should get spawned, the number has to be higher than this (out of 1000)
    public static final int VEHICLE_BASE_SPAWN_CHANCE = 980; 
    public static final int PEDESTRIAN_BASE_SPAWN_CHANCE = 985; 

    public static boolean SHOW_SPAWNERS = true;

    // Set Y Positions for Pedestrians to spawn
    // Spawns are the different y spwans positions. The First one is top spawn, middle is just to index it, third if bottom spawn
    public static final int[] SPAWNS = new int[]{190, 0, 705}; // Top spawn | middle just so the indexing works | Bottom Spawn
    //public static final int TOP_SPAWN = 190; // Pedestrians who spawn on top
    //public static final int BOTTOM_SPAWN = 705; // Pedestrians who spawn on the bottom
    public static final int TIME_TO_CHANGE_HOURS = 60; // 60 acts for one hour change
    public static final int TIME_TO_CHANGE_DAY = 12; // every 12 hour changes will change the day / background. One full day is 24 hours
    public static final int TIME_TO_CHANGE_SEASONS = 5; // 5 full days in a month
    public static final String[] SEASONS = new String[]{"Spring", "Summer", "Fall", "Winter"}; // order of the seasons
    public static final int START_HOUR = 7; // The starting hour which is also changes when it will turn day / night

    // Instance variables / Objects
    private boolean twoWayTraffic, splitAtCenter;
    private int laneHeight, laneCount, spaceBetweenLanes;
    private int[] lanePositionsY;
    private VehicleSpawner[] laneSpawners;
    private int seasonNumber = 0; // 0 - Spring | 1 - Summer | 2 - Fall | 3 - Winter
    private boolean dayTime = true;
    private int dayNumber = 0; // goes from 0-4 but displays with 1 more (1-5)
    private int hourNumber; // goes from 0-23
    private int hourCounter = 0; 
    private Label informationLabel; // shows some current world data, such as time of day and season
    private GreenfootSound trafficSound;
    private GreenfootSound morningSound;
    private GreenfootSound ambulanceSound;

    /**
     * Constructor for objects of class MyWorld.
     * 
     * Note that the Constrcutor for the default world is always called
     * when you click the reset button in the Greenfoot scenario screen -
     * this is is basically the code that runs when the program start.
     * Anything that should be done FIRST should go here.
     * 
     */
    public VehicleWorld()
    {    
        // Create a new world with 1024x800 pixels, UNBOUNDED
        super(1024, 800, 1, false); 

        // This command (from Greenfoot World API) sets the order in which 
        // objects will be displayed. In this example, Pedestrians will
        // always be on top of everything else, then Vehicles (of all
        // sub class types) and after that, all other classes not listed
        // will be displayed in random order. 
        //setPaintOrder (Pedestrian.class, Vehicle.class); // Commented out to use Z-sort instead

        // set up background -- If you change this, make 100% sure
        // that your chosen image is the same size as the World
        mainBackground = new GreenfootImage ("background01.png");
        //setBackground(mainBackground);

        // Set critical variables - will affect lane drawing
        laneCount = 6;
        laneHeight = 66;
        spaceBetweenLanes = 6;
        splitAtCenter = true;
        twoWayTraffic = true;

        // Init lane spawner objects 
        laneSpawners = new VehicleSpawner[laneCount];

        //Prepare lanes method - draws the lanes
        lanePositionsY = prepareLanes(this, mainBackground, laneSpawners, 232, laneHeight, laneCount, spaceBetweenLanes, twoWayTraffic, splitAtCenter);

        laneSpawners[0].setSpeedModifier(1.2);
        laneSpawners[5].setSpeedModifier(1.2);

        setBackground(mainBackground);
        setActOrder(Vehicle.class, Pedestrian.class, Projectile.class);
        // Text labels are always on top and StatBars and then image actors
        setPaintOrder(Label.class, SuperStatBar.class, ImageActor.class);

        // init sounds
        Explosion.init();
        Vehicle.init();
        GravityPortal.init();
        Pedestrian.init();
        
        trafficSound = new GreenfootSound("traffic_sounds.mp3");
        trafficSound.setVolume(30);
        morningSound = new GreenfootSound("morning_time.mp3");
        morningSound.setVolume(30);
        ambulanceSound = new GreenfootSound("ambulance_sirens.mp3");
        ambulanceSound.setVolume(40);
        ambulanceSound.play();
        morningSound.play();
        Greenfoot.delay(1);
        ambulanceSound.stop();
        morningSound.stop();

        // labels
        informationLabel = new Label("Season: Spring | Day: 1 | 0:00", 40);
        addObject(informationLabel, 512, 50);

        // create the filters and background. tempFilter is used to create all the filters and is used to create the image actors
        filters = new ImageActor[COLOUR_FILTERS.length];

        for (int i = 0; i < COLOUR_FILTERS.length; i++) {
            GreenfootImage tempFilter = new GreenfootImage(1024, 800);
            tempFilter.setColor(COLOUR_FILTERS[i]);
            tempFilter.fillRect(0, 0, 1024, 800);
            filters[i] = new ImageActor(tempFilter);
        }
        // intialize the hour and season filter
        hourNumber = START_HOUR;
        changeSeasons();
    }

    public void started() {
        trafficSound.playLoop();
    }

    public void stopped() {
        trafficSound.pause();
        // stops all gravity portal sounds via the method in GravityPortal
        GravityPortal.stopSound();
        ambulanceSound.stop();
    }

    public void act () {
        spawn();
        zSort((ArrayList<Actor>)(getObjects(Actor.class)), this);
        hourCounter++;
        if (hourCounter == TIME_TO_CHANGE_HOURS+START_HOUR) {
            hourNumber++;
            hourCounter = 0;
            if (hourNumber-START_HOUR == TIME_TO_CHANGE_DAY) {
                changeDay();
            } 
            // So dayNumber always changes at 0:00
            if (hourNumber == 2*TIME_TO_CHANGE_DAY) {
                hourNumber = 0;
                dayNumber++;
                if (dayNumber == TIME_TO_CHANGE_SEASONS) {
                    dayNumber = 0;
                    seasonNumber = (seasonNumber+1) % 4;
                    changeSeasons();
                }
            }

            if (hourNumber == START_HOUR) {
                changeDay();
            }
        }

        informationLabel.setValue("Season: " + SEASONS[seasonNumber] + " | Day: " + (dayNumber+1) + " | " + hourNumber + ":00");
    }

    /**
     * Change the seasons - When changing seasons all cars currently on the screen will move faster
     */
    private void changeSeasons() {

        for (int i = 1; i < filters.length; i++) {
            if (i == seasonNumber+1 && filters[i].getWorld() == null) {
                addObject(filters[i], filters[i].getImage().getWidth()/2, filters[i].getImage().getHeight()/2);
            } else if (filters[i].getWorld() != null) {
                removeObject(filters[i]);
            }
        }

        // If it night time and the season changes have the night filter on top
        if (!dayTime && filters[0].getWorld() != null) {
            removeObject(filters[0]);
            addObject(filters[0], filters[0].getImage().getWidth()/2 , filters[0].getImage().getHeight()/2);
        }

        ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>) getObjects(Vehicle.class);
        // vehicles get their speed changed at the beginning of new seasons (some only)
        for (Vehicle vehicle : vehicles) {
            if (seasonNumber == 1) {
                vehicle.multiplySpeed(1.5);
            } else if (seasonNumber == 3) {
                vehicle.multiplySpeed(0.5);
            }

        }
    }

    private void changeDay() {
        dayTime = !dayTime;
        if (dayTime) {
            morningSound.play();
            ArrayList<Vehicle> vehicles = (ArrayList<Vehicle>) getObjects(Vehicle.class);
            for (Vehicle vehicle : vehicles) {
                vehicle.multiplySpeed(1.2);
            }
        }

        if (dayTime && filters[0].getWorld() != null) {
            removeObject(filters[0]);
        } else if (filters[0].getWorld() == null && !dayTime){
            addObject(filters[0], filters[0].getImage().getWidth()/2 , filters[0].getImage().getHeight()/2);
        }

    }

    private void spawn () {
        // Chance to spawn a vehicle
        // Chance being the mininum number needed to be rolled for a vehicle / pedestrian to be spawned - Higher means lower chance
        int vehicleChance = VEHICLE_BASE_SPAWN_CHANCE; 
        int pedestrianChance = PEDESTRIAN_BASE_SPAWN_CHANCE;
        if (!isDay()) {
            vehicleChance += 7;
            pedestrianChance += 3;
        }
        if (seasonNumber == 1) {
            vehicleChance -= 70;
            pedestrianChance -= 20;
        } else if (seasonNumber == 3) {
            vehicleChance += 1;
            pedestrianChance += 1;
        }

        int spawnNumber = Greenfoot.getRandomNumber(1000)+1;

        if (spawnNumber >= vehicleChance){
            int lane = Greenfoot.getRandomNumber(laneCount);
            if (!laneSpawners[lane].isTouchingVehicle()){
                int vehicleType = Greenfoot.getRandomNumber(100)+1;
                int sum = 0;
                for (int i = 0; i < 6; i++) {
                    sum += VEHICLE_SPAWN_RATES[seasonNumber][i];
                    if (!dayTime) {
                        sum +=  + VEHICLE_NIGHT_SPAWN_BONUS[i];
                    }
                    if (vehicleType <= sum) {
                        if (i == 0) {
                            addObject(new Ambulance(laneSpawners[lane]), 0, 0);
                            break;
                        } else if (i == 1) {
                            addObject(new Bus(laneSpawners[lane]), 0, 0);
                            break;
                        } else if (i == 2) {
                            addObject(new Car(laneSpawners[lane]), 0, 0);
                            break;
                        } else if (i == 3) {
                            addObject(new DrunkCar(laneSpawners[lane]), 0, 0);
                            break;
                        } else if (i == 4) {
                            addObject(new RobotTruck(laneSpawners[lane]), 0, 0);
                            break;
                        } else if (i == 5) {
                            addObject(new PlantTruck(laneSpawners[lane]), 0, 0);
                            break;
                        }
                    }
                }
            }
        }

        if (getObjects(Ambulance.class).size() <= 1) {
            boolean spawned = false;
            ArrayList<Person> people = (ArrayList<Person>)getObjects(Person.class);
            int dead = 0;
            for (Person p : people) {
                if (!p.isAwake()) {
                    dead++;
                }
            }

            if (dead >= 8) {
                for (int i = 0; i < laneCount; i++) {
                    if (!laneSpawners[i].isTouchingVehicle()) {
                        addObject(new Ambulance(laneSpawners[i]), 0, 0);
                        spawned = true;
                    }
                }
                if (spawned) {
                    ambulanceSound.play();
                }
            }
            
        }

        // Chance to spawn a Pedestrian (rerolls the number again)
        spawnNumber = Greenfoot.getRandomNumber(1000)+1;
        if (spawnNumber >= pedestrianChance){
            int xSpawnLocation = Greenfoot.getRandomNumber (824) + 100; // random between 99 and 699, so not near edges
            int direction = Greenfoot.getRandomNumber(2) == 0 ? 1 : -1;
            int pedestrianType = Greenfoot.getRandomNumber(100)+1;

            int sum = 0;
            for (int i = 0; i < 4; i++) {
                sum += PEDESTRIAN_SPAWN_RATES[seasonNumber][i];
                if (!dayTime) {
                    sum += PEDESTRIAN_NIGHT_SPAWN_BONUS[i];
                }
                if (pedestrianType <= sum) {
                    if (i == 0) {
                        addObject(new Person(direction), xSpawnLocation, SPAWNS[Math.abs(direction-1)]);
                        break;
                    } else if (i == 1) {
                        addObject(new Robot(direction), xSpawnLocation, SPAWNS[Math.abs(direction-1)]);
                        break;
                    } else if (i == 2) {
                        addObject(new Bear(direction), xSpawnLocation, SPAWNS[Math.abs(direction-1)]);
                        break;
                    } else if (i == 3) {
                        addObject(new Technician(direction), xSpawnLocation, SPAWNS[Math.abs(direction-1)]);
                        break;
                    }
                }
            }
        }

    }

    public boolean isDay() {
        return dayTime;
    }

    public int getSeason() {
        return seasonNumber;
    }

    /**
     *  Given a lane number (zero-indexed), return the y position
     *  in the centre of the lane. (doesn't factor offset, so 
     *  watch your offset, i.e. with Bus).
     *  
     *  @param lane the lane number (zero-indexed)
     *  @return int the y position of the lane's center, or -1 if invalid
     */
    public int getLaneY (int lane){
        if (lane < lanePositionsY.length && lane >= 0){
            return lanePositionsY[lane];
        } 
        return -1;
    }

    /**
     * Given a y-position, return the lane number (zero-indexed).
     * Note that the y-position must be valid, and you should 
     * include the offset in your calculations before calling this method.
     * For example, if a Bus is in a lane at y=100, but is offset by -20,
     * it is actually in the lane located at y=80, so you should send
     * 80 to this method, not 100.
     * 
     * @param y - the y position of the lane the Vehicle is in
     * @return int the lane number, zero-indexed
     * 
     */
    public int getLane (int y){
        for (int i = 0; i < lanePositionsY.length; i++){
            if (y == lanePositionsY[i]){
                return i;
            }
        }
        return -1;
    }

    /**
     * Method returns the amount of lanes in the world
     *
     * @return returns the lane count
     */
    public int getLaneCount() {
        return laneCount;
    }

    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit, int centreSpacing)
    {
        // Declare an array to store the y values as I calculate them
        int[] lanePositions = new int[lanes];
        // Pre-calculate half of the lane height, as this will frequently be used for drawing.
        // To help make it clear, the heightOffset is the distance from the centre of the lane (it's y position)
        // to the outer edge of the lane.
        int heightOffset = heightPerLane / 2;
        // draw top border
        target.setColor(GREY_BORDER);
        target.fillRect(0, startY, target.getWidth(), spacing);

        // Main Loop to Calculate Positions and draw lanes
        for (int i = 0; i < lanes; i++){
            // calculate the position for the lane
            lanePositions[i] = startY + spacing + (i * (heightPerLane+spacing)) + heightOffset ;

            // draw lane
            target.setColor(GREY_STREET); 
            // the lane body
            target.fillRect (0, lanePositions[i] - heightOffset, target.getWidth(), heightPerLane);
            // the lane spacing - where the white or yellow lines will get drawn
            target.fillRect(0, lanePositions[i] + heightOffset, target.getWidth(), spacing);

            // Place spawners and draw lines depending on whether its 2 way and centre split
            if (twoWay && centreSplit){
                // first half of the lanes go rightward (no option for left-hand drive, sorry UK students .. ?)
                if ( i < lanes / 2){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else { // second half of the lanes go leftward
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw yellow lines if middle 
                if (i == lanes / 2){
                    target.setColor(YELLOW_LINE);
                    target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                } else if (i > 0){ // draw white lines if not first lane
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                } 

            } else if (twoWay){ // not center split
                if ( i % 2 == 0){
                    spawners[i] = new VehicleSpawner(false, heightPerLane, i);
                    world.addObject(spawners[i], target.getWidth(), lanePositions[i]);
                } else {
                    spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                    world.addObject(spawners[i], 0, lanePositions[i]);
                }

                // draw Grey Border if between two "Streets"
                if (i > 0){ // but not in first position
                    if (i % 2 == 0){
                        target.setColor(GREY_BORDER);
                        target.fillRect(0, lanePositions[i] - heightOffset - spacing, target.getWidth(), spacing);

                    } else { // draw dotted lines
                        for (int j = 0; j < target.getWidth(); j += 120){
                            target.setColor (YELLOW_LINE);
                            target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                        }
                    } 
                }
            } else { // One way traffic
                spawners[i] = new VehicleSpawner(true, heightPerLane, i);
                world.addObject(spawners[i], 0, lanePositions[i]);
                if (i > 0){
                    for (int j = 0; j < target.getWidth(); j += 120){
                        target.setColor (Color.WHITE);
                        target.fillRect (j, lanePositions[i] - heightOffset - spacing, 60, spacing);
                    }
                }
            }
        }
        // draws bottom border
        target.setColor (GREY_BORDER);
        target.fillRect (0, lanePositions[lanes-1] + heightOffset, target.getWidth(), spacing);

        return lanePositions;
    }

    /**
     * A z-sort method which will sort Actors so that Actors that are
     * displayed "higher" on the screen (lower y values) will show up underneath
     * Actors that are drawn "lower" on the screen (higher y values), creating a
     * better perspective. 
     */
    public static void zSort (ArrayList<Actor> actorsToSort, World world){
        ArrayList<ActorContent> acList = new ArrayList<ActorContent>();
        // Create a list of ActorContent objects and populate it with all Actors sent to be sorted
        for (Actor a : actorsToSort){
            if (a instanceof SuperSmoothMover) {
                acList.add(new ActorContent (a, ((SuperSmoothMover)a).getPreciseX(), ((SuperSmoothMover)a).getPreciseY()));
                continue;
            }
            acList.add (new ActorContent (a, a.getX(), a.getY()));
        }    
        // Sort the Actor, using the ActorContent comparitor (compares by y coordinate)
        Collections.sort(acList);
        // Replace the Actors from the ActorContent list into the World, inserting them one at a time
        // in the desired paint order (in this case lowest y value first, so objects further down the 
        // screen will appear in "front" of the ones above them).
        for (ActorContent a : acList){
            Actor actor  = a.getActor();
            world.removeObject(actor);
            world.addObject(actor, (int)a.getX(), (int)a.getY());
            if (actor instanceof SuperSmoothMover) {
                ((SuperSmoothMover)actor).setLocation(a.getX(), a.getY());
            }
        }
    }

    /**
     * <p>The prepareLanes method is a static (standalone) method that takes a list of parameters about the desired roadway and then builds it.</p>
     * 
     * <p><b>Note:</b> So far, Centre-split is the only option, regardless of what values you send for that parameters.</p>
     *
     * <p>This method does three things:</p>
     * <ul>
     *  <li> Determines the Y coordinate for each lane (each lane is centered vertically around the position)</li>
     *  <li> Draws lanes onto the GreenfootImage target that is passed in at the specified / calculated positions. 
     *       (Nothing is returned, it just manipulates the object which affects the original).</li>
     *  <li> Places the VehicleSpawners (passed in via the array parameter spawners) into the World (also passed in via parameters).</li>
     * </ul>
     * 
     * <p> After this method is run, there is a visual road as well as the objects needed to spawn Vehicles. Examine the table below for an
     * in-depth description of what the roadway will look like and what each parameter/component represents.</p>
     * 
     * <pre>
     *                  <=== Start Y
     *  ||||||||||||||  <=== Top Border
     *  /------------\
     *  |            |  
     *  |      Y[0]  |  <=== Lane Position (Y) is the middle of the lane
     *  |            |
     *  \------------/
     *  [##] [##] [##| <== spacing ( where the lane lines or borders are )
     *  /------------\
     *  |            |  
     *  |      Y[1]  |
     *  |            |
     *  \------------/
     *  ||||||||||||||  <== Bottom Border
     * </pre>
     * 
     * @param world     The World that the VehicleSpawners will be added to
     * @param target    The GreenfootImage that the lanes will be drawn on, usually but not necessarily the background of the World.
     * @param spawners  An array of VehicleSpawner to be added to the World
     * @param startY    The top Y position where lanes (drawing) should start
     * @param heightPerLane The height of the desired lanes
     * @param lanes     The total number of lanes desired
     * @param spacing   The distance, in pixels, between each lane
     * @param twoWay    Should traffic flow both ways? Leave false for a one-way street (Not Yet Implemented)
     * @param centreSplit   Should the whole road be split in the middle? Or lots of parallel two-way streets? Must also be two-way street (twoWay == true) or else NO EFFECT
     * 
     */
    public static int[] prepareLanes (World world, GreenfootImage target, VehicleSpawner[] spawners, int startY, int heightPerLane, int lanes, int spacing, boolean twoWay, boolean centreSplit){
        return prepareLanes (world, target, spawners, startY, heightPerLane, lanes, spacing, twoWay, centreSplit, spacing);
    }

}

/**
 * Container to hold and Actor and an LOCAL position (so the data isn't lost when the Actor is temporarily
 * removed from the World).
 */
class ActorContent implements Comparable <ActorContent> {
    private Actor actor;
    private double xx, yy;
    public ActorContent(Actor actor, double xx, double yy){
        this.actor = actor;
        this.xx = xx;
        this.yy = yy;
    }

    public void setLocation (int x, int y){
        xx = x;
        yy = y;
    }

    public double getX() {
        return xx;
    }

    public double getY() {
        return yy;
    }

    public Actor getActor(){
        return actor;
    }

    public String toString () {
        return "Actor: " + actor + " at " + xx + ", " + yy;
    }

    public int compareTo (ActorContent a){
        return (int)(this.getY() - a.getY());
    }

}
