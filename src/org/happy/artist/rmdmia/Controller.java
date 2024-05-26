package org.happy.artist.rmdmia;

import org.happy.artist.rmdmia.perception.engine.PEManager;
import org.happy.artist.rmdmia.rcsm.RCSMManager;
import org.happy.artist.rmdmia.roci.ROCIManager;
import org.happy.artist.rmdmia.decision.manager.DecisionManager;
import org.happy.artist.rmdmia.gesture.manager.GestureManager;
import org.happy.artist.rmdmia.task.manager.TaskManager;
import org.happy.artist.rmdmia.mission.manager.MissionManager;
import org.happy.artist.rmdmia.instruction.InstructionManager;

/** The Controller Object is the RMDMIA's implements dynamic 
 * access between RMDMIA Components (i.e. RCSM, ROCI, PE, DecisionManager, 
 * GestureManager, TaskManager, MissionManager, InstructionManager).
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2014 Happy Artist. All rights reserved.
 */
public class Controller 
{
    private RCSMManager rcsm;
    private ROCIManager roci;
    private PEManager pe;
    private InstructionManager im;
    private DecisionManager dm;
    private GestureManager gm;
    private TaskManager tm;
    private MissionManager mm;

    
    private ControllerManager controllerManager;
    // The Controller ID
    public int controller_id;
          
    /** Controller Constructor. */
    public Controller(ControllerManager controllerManager)
    {
        // Default controller_id to 0 for a single Controller.
        new Controller(0, controllerManager);
    }
    
    /** Controller Constructor.
     * 
     * @param controller_id int this id is assigned by the ControllerManager.
     */
    public Controller(int controller_id, ControllerManager controllerManager)
    {
        this.controllerManager = controllerManager;
        this.controller_id=controller_id;
        if(ControllerManager.IS_ANDROID)
        {
        	// Is Android
	        this.rcsm = new RCSMManager(controllerManager.context.getFilesDir() + "/conf/rcsm.properties", this);
   
	        this.im = new InstructionManager(controllerManager.context.getFilesDir() + "/conf/instruction_manager.properties", this);         
                
	        this.pe = new PEManager(controllerManager.context.getFilesDir() + "/conf/pe.properties", this);    
                this.gm = new GestureManager(controllerManager.context.getFilesDir() + "/conf/gesture_manager.properties", this);
 
                this.roci = new ROCIManager(controllerManager.context.getFilesDir() + "/conf/roci.properties", this);  
//	        this.gm = new GestureManager(controllerManager.context.getFilesDir() + "/conf/gesture_manager.properties", this);
	        this.dm = new DecisionManager(controllerManager.context.getFilesDir() + "/conf/decision_manager.properties", this);                
	        this.mm = new MissionManager(controllerManager.context.getFilesDir() + "/conf/mission_manager.properties", this);                  
	        this.tm = new TaskManager(controllerManager.context.getFilesDir() + "/conf/task_manager.properties", this);                  
                
        }
        else
        {
        	// Is Java
// TODO: Initialize Constructor, then call initialize to initialize both for purpose of fixing the null ROCIManager from a ROCI Plugin            
            this.rcsm = new RCSMManager("conf/rcsm.properties", this);
            this.im = new InstructionManager("conf/instruction_manager.properties", this);                 
            this.pe = new PEManager("conf/pe.properties", this);                
            this.roci = new ROCIManager("conf/roci.properties", this);
            this.gm = new GestureManager("conf/gesture_manager.properties", this);
            this.dm = new DecisionManager("conf/decision_manager.properties", this);
            this.mm = new MissionManager("conf/mission_manager.properties", this);
            this.tm = new TaskManager("conf/task_manager.properties", this);
               
        }
                
        rcsm.initialize();
        im.initialize();                
        pe.initialize();
        roci.initialize();
        gm.initialize();
        dm.initialize();
        mm.initialize();                
        tm.initialize();
        // rcsm.onInitialized();
       // roci.onInitialized();
    }
    
    /** Return a reference to the RCSMManager.
     * 
     * @return RCSMManager  
     */
    public RCSMManager getRCSM()
    {
        return rcsm;
    }    

    /** Return a reference to the ROCIManager.
     * 
     * @return ROCIManager  
     */    
    public ROCIManager getROCI()
    {
        return roci;
    }
    
    /** Return a reference to the PEManager.
     * 
     * @return PEManager  
     */    
    public PEManager getPE()
    {
        return pe;
    }    
    
    /** Return a reference to the ControllerManager.
     * 
     * @return ControllerManager  
     */    
    public ControllerManager getControllerManager()
    {
        return controllerManager;
    }    
    
    
    /** Return a reference to the GestureManager.
     * 
     * @return GestureManager  
     */
    public GestureManager getGestureManager()
    {
        return gm;
    }    

    /** Return a reference to the DecisionManager.
     * 
     * @return DecisionManager  
     */    
    public DecisionManager getDecisionManager()
    {
        return dm;
    }
    
    /** Return a reference to the MissionManager.
     * 
     * @return MissionManager 
     */    
    public MissionManager getMissionManager()
    {
        return mm;
    }     

    /** Return a reference to the TaskManager.
     * 
     * @return TaskManager  
     */    
    public TaskManager getTaskManager()
    {
        return tm;
    } 
    
    /** Return a reference to the InstructionManager.
     * 
     * @return InstructionManager  
     */    
    public InstructionManager getInstructionManager()
    {
        return im;
    }     
}
