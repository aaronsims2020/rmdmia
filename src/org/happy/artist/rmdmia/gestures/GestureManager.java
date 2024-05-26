package org.happy.artist.rmdmia.gestures;

// TODO: It became obvious recently that GestureManager should not be implemented inside RCSM,
// and that GestureManager is separate from the Movement Processor system integrated
// into RCSM. This is the new GestureManager object. Implementation must be performed,
// and this will likely become a Mission/Task Manager simulator until those are implemented,
// as well as a Robot Controller tester with pre-defined Gesture scripts.

/** GestureManager.java - Create a Gesture Manager Object Prototype 1 with the following functionality:
 * Able to Create/Read/Update/Delete Gesture Files (simple text files to cached memory in the
 * following format:
 * A follow up prototype release will support loading/saving gestures to/from text files, or a
 * database to the Gesture Manager.
 * The File/Database Gesture reader will parse the read gestures into a Gesture object.
 * The Gesture Text File format is key/ value pair:
 * timing.interval=int
 * timing.interval.type=type
 * HardwareID1=data or movement pattern, data or movement pattern
 * HardwareID2=data or movement pattern, data or movement pattern
 * Each comma represents a synchronized time interval for multiple hardware targets movements.
 * The Gesture Manager will implement a method to send a gesture to the simulator.
 * Gesture Manager Prototype 1 will support Simple Gestures. A Simple Gesture can send timed
 * hardware movement instructions.
 * 
 * Movement Processor implements algorithmic movement control for movement 
 * calibration and movement based event launching. Algorithms built into movement 
 * processors are intended to increase/decrease numeric input parameters for 
 * movement updates. Modifying the movements in a movement processor manner 
 * is not efficient and in a later release the architecture of the RMDMIA will 
 * need to be re-architected. 
 * 
 * Dynamic Gestures will be implemented as follows:
 * a Gesture Object contains timed hardware movement instructions by time interval. 
 * Each interval contains multiple hardware identifiers to be executed together.
 * 
 * A Movement Processing Collection Object will support the following functions 
 * to support managing each Gesture's movement processors:
 *  - add
 *  - remove
 *  - hold
 *  - continue 
 *  - start 
 *  - stop
 *  - recycle
 * 
 * A movement processor interface must be defined for movement processor 
 * implementers to define processing rules for a test method to determine if the 
 * current registered processor will modify data, call another gesture, or perform 
 * an action. Movement Processors are intended to be used for calibrating gesture 
 * movements.
 * 
 * Dynamic Control Gestures
 * 
 * Gesture Manager Prototype 2 will implement Dynamic Control Gestures. Dynamic 
 * Control Gestures add the Movement Processor functionality.
 * 
 * The actual Gesture execution script specification will be worked out during 
 * the prototyping of the Routine Manager Prototypes, and Routine Designer 
 * testing.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public class GestureManager 
{
    // TODO: Post GestureManager Prototype 2 implementations should all be implemented in a single GesturePool Array instead of multiple with grouping ranges. It is expected to perform at much higher efficiency then multiple GesturePool Object instatiations. 
    // GestureRegistry class is implemented by the GestureManager 
    // to manage registered Gestures by designated package name, gesture quantity, 
    // and associated robot function grouping for GesturePool initialization in the GestureManager.
    private final static GestureRegistry registry = GestureRegistry.getInstance();
    
    public GestureManager()
    {
        
    }
    //TODO: Determine best algorithm for Pre-generate Gesture Objects in an Array (Theoretical maximum number of Gestures should be: the number of maximum Gestures per second * timeout seconds, reality, is based on System memory usage and power consumption (Need to revisit this later on in performance testing)).
 
    public void add()
    {

    }

    public void remove()
    {

    }

    public void hold()
    {

    }

    // Wanted to call method continue but seems to be a reserved method name...
    public void continues()
    {
        
    }

    public void start()
    {

    }

    public void stop()
    {


    }

    public void recycle()
    {

    }
}