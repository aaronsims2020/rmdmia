package org.happy.artist.rmdmia.rcsm.provider.message;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.ControllerManager;
import org.happy.artist.rmdmia.instruction.Instruction;
import org.happy.artist.rmdmia.instruction.InstructionDefinition;
import org.happy.artist.rmdmia.instruction.InstructionInputHandler;
import org.happy.artist.rmdmia.instruction.InstructionOutputHandler;
import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;
import org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder;
import org.happy.artist.rmdmia.instruction.utility.SchemaImporter;
import org.happy.artist.rmdmia.rcsm.RCSMProvider;

/**
 * AbstractInstructionDefinition abstract class for implementing the InstructionDefinition.
 *
 * @author Happy Artist
 *
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved.
 *
 */
public abstract class AbstractInstructionDefinition extends InstructionDefinition
{
    private Instruction instruction;
    private InstructionInputHandler instructionInputHandler;
    private InstructionOutputHandler instructionOutputHandler;
    private InstructionObjectPool pool;
    private List<MessageCompiler.DynamicSourceCodeObject> sourceCode;
    private RCSMProvider rcsm_provider;
    
    public AbstractInstructionDefinition(RCSMProvider rcsm_provider)
    {
        super();                                                                        
        this.rcsm_provider = rcsm_provider;
    }
// The first package section needs to be rcsm providername then .instruction for generated instructions, all generated schema types will start with ros, then their own message types following.   
    private String package_name = provider_name.concat(".instruction");
    private String class_name;
    private String[] type_array;
    private String source;
    private String classFolderPath;
    private String msgfile;   

    @Override
    public Instruction getInstructionObject()
    {
        if (pool != null)
        {

            // TODO: Replace with pool from Instruction OutputHandler
            return pool.checkout();
        }
        else
        {
            return getInstructionObjectPool().checkout();
        }
    }

    private void updateInstructionSource()
    {
        this.package_name = provider_name.concat(".instruction");
        //this.package_name="";

        try
        {
            // TODO: Check if Class is generated, and if not attempt to automatically generate, and if fail generate a generic Instruction Object that contains the ROS Message byte[]. All handling of this type will need to manually read data from ROS Message format. Up to implementors to handle...

            // If compiling Non-existant instruction...

// TODO: Fix the Schema Lookup based on trmd message definition, by setting the trmd definition to null commenting it out and setting to null, to test if it is source of InputHandlers not being generated for Pose and Color               
            // Get Message Definition from Subscriber Message Manager 
//           ROSTopicRegistryMessageDefinition trmd = (ROSTopicRegistryMessageDefinition)ROSSubscriberMessageManager.getInstance(rosNode.getCallerID(), rosNode).getTopicDefinitions()[hid]; 
           TopicRegistryMessageDefinition trmd = rcsm_provider.getSubscriberMessageManager().getTopicDefinitions()[hid]; 

            // TODO: remove null
            trmd.definition = null;
            if ((trmd.definition == null || trmd.definition.isEmpty()) && trmd.type != null && !trmd.type.isEmpty())
            {
                // Type exists, and if a .msg file exists compile
       //         System.out.println("Found Type: No Message definition available for source generation, need to create a ROS_Message Instruction as workaround!!!!!!");

                this.msgfile = rcsm_provider.getController().getInstructionManager().getSchemaLookupMap().get(provider_name.concat("/").concat(trmd.type).toLowerCase());
     //           System.out.println("Schema Map Key: ".concat(provider_name.concat("/").concat(trmd.type).toLowerCase()));
     //           System.out.println("Could not find :" + msgfile);
                if (msgfile == null)
                {
                    // this.msgfile=
                    if (trmd.type.lastIndexOf("/") != -1)
                    {
                        //this.msgfile = trmd.type.substring(trmd.type.lastIndexOf("/") + 1);
                        this.msgfile=provider_name.concat("/").concat(trmd.type).toLowerCase();
                        if (msgfile != null)
                        {
                            //this.msgfile = rosNode.getController().getInstructionManager().getSchemaLookupMap().get(provider_name.concat("/").concat(msgfile.toLowerCase()));
                            this.msgfile = rcsm_provider.getController().getInstructionManager().getSchemaLookupMap().get(msgfile);                            
                            // System.out.println("Schema message file found: " + msgfile + ", trmd.type: " + trmd.type);
                            //                           System.out.println("SCHEMA LOOKUP MAP: " + Arrays.deepToString(rosNode.getController().getInstructionManager().getSchemaLookupMap().keySet().toArray()));
                            //ArrayList<String> list= new ArrayList<String>(1);
                            //list.add(msgfile);
                            SchemaImporter.generateSchemaInstructions(hid, msgfile, rcsm_provider.getController(), provider_name);


//                                System.out.println("CLASS MAP KEYS: " + Arrays.deepToString(rosNode.getController().getInstructionManager().getClassLookupMap().keySet().toArray()));                                   
                        }
                    }
                    else
                    {
                        System.out.println("SCHEMA MESSAGE FILE NOT FOUND FOR " + trmd.type);
                    }
                }
                else
                {
                    //System.out.println("Schema message file found: " + msgfile);
                    SchemaImporter.generateSchemaInstructions(hid, msgfile, rcsm_provider.getController(), provider_name);
                }
            }
            else if (trmd.definition == null || trmd.definition.isEmpty())
            {
                // TODO: No definition available - Use ROS Message Instruction
                System.out.println("No Message definition available for topic ".concat(trmd.getTopicName()).concat(", type:".concat(trmd.getTopicType()).concat("  source generation, need to create an Instruction as workaround!!!!!!")));
                /*               if(trmd.service==null)
                {
                    // is topic
                    System.out.println("No Message definition available for topic ".concat(trmd.getTopicName()).concat(" source generation, need to create a ROS_Message Instruction as workaround!!!!!!"));
                }
                else
                {
                    // is service or action
                    System.out.println("No Message definition available for service ".concat(trmd.service).concat(" source generation, need to create a ROS_Message Instruction as workaround!!!!!!"));
                    
                }
                */
            }
            else
            {
                if (trmd.topic != null && trmd.type != null)
                {
                    // Is topic
                    // File name is topic Type
                    this.type_array = trmd.type.split("/");
                    if (type_array.length > 0)
                    {
                        this.class_name = type_array[type_array.length - 1];
                        if (trmd.type != null && trmd.type.split("/").length > 1)
                        {
                            this.package_name = provider_name.concat(".").concat(trmd.type.substring(0, trmd.type.lastIndexOf("/")).replaceAll("/", "\\."));
                        }
                        else if (type_array.length > 1)
                        {
                            // has a extended package name.
                            for (int i = 0; i < type_array.length - 2; i++)
                            {
                                this.package_name = package_name.concat(".").concat(type_array[i]);
                            }
                        }
                    }
                    // Set type_array back to null.
                    this.type_array = null;
                }
  /*              else if (trmd.service != null)
                {
                    // TODO: Is service
                    System.out.println("?????????????????????????????????????");
                }*/

                // Definition available to process
                // process definition to class
                // generate source & compile class 
 /*               if (trmd.type != null)
                {
                    // File name is type post the last / 
                    // type dir is String preceding last /, subdirs split on /
                }
                else
                {
                    return;
                }
*/
                if (trmd.type == null)
                {
                    return;
                }
                
//                    System.out.println("Message definition: " + trmd.definition);
                this.source = getRunnableInstructionBuilder().getDefinitionToMessageInstructionSourceCodeGeneratorInterface().process(hid, class_name, package_name, trmd.definition, rcsm_provider.getController());
                // BEGIN: Add the 
                // Add message_definition to schema field in InstructionManagerHardwareRegistry 
 /*       if(hid!=-1)
                 {   
                 // Instruction Class name
                 InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".2"), package_name.concat(".").concat(class_name));
        
                 // InstructionInputHandler Class name
                 InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".3"), package_name.concat(".").concat(class_name).concat("InputHandler"));
                 // InstructionOutputHandler Class name
                 InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".4"), package_name.concat(".").concat(class_name).concat("OutputHandler"));            
                 // InstructionObjectPool Class name
                 InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".8"), package_name.concat(".").concat(class_name).concat("InstructionPool"));               
                 try
                 {
  
                
                
                 // TODO: WARNING THIS MAY NEED TO BE UPDATED INSIDE MESSAGETODEFINITIONGENERATOR IF TRND IS NOT YET SET. NEED TO VERIFY                
                
                
                 /// a              
                 // Message Definition
                 InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".7"), HexStringConverter.getHexStringConverterInstance().stringToHex(trmd.definition));
                 }
                 catch (UnsupportedEncodingException ex)
                 {
                 Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 }                
                
                 // END: 
                 InstructionManagerHardwareRegistry.put(String.valueOf(this.getHID()).concat(".6"),HexStringConverter.getHexStringConverterInstance().stringToHex(trmd.definition));
                 */

                // assign classFolderPath
                this.classFolderPath = rcsm_provider.getController().getInstructionManager().getClassFolderPath();

                if (classFolderPath.endsWith("/"))
                {
                    this.classFolderPath = classFolderPath.concat(provider_name);
                }
                else
                {
                    this.classFolderPath = classFolderPath.concat("/".concat(provider_name));
                }
                
                if (!ControllerManager.IS_ANDROID)
                {
                    //   System.out.println("COMPILE PATH: " + getFolder(classFolderPath.concat("/").concat(package_name.replaceAll("\\.", "/"))) + ", class_name: " + class_name);
// TODO: UNCOMMENT
//                      MessageCompiler.compile(source, class_name, getFolder(classFolderPath.concat("/")));                           
//                    System.out.println("InputHandler SOURCE CODE: AbstractInstructionDefinition - "+source);  
                    // addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name,source));
                }
                else
                {
                    // is Android
                    if (classFolderPath.indexOf("/") != 0)
                    {
                        this.classFolderPath = classFolderPath.substring(1);
                    }
// TODO: UNCOMMENT
//                        MessageCompiler.compile(source, class_name, getFolder(rosNode.getController().getControllerManager().context.getFilesDir() + classFolderPath.concat("/")));
                    // addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name,source));

                    //                    System.out.println("InputHandler SOURCE CODE: AbstractInstructionDefinition - "+source); 
                }
                // attempt to load class using ControllerManager addProvider

                // On success instantiate new class and return Instruction Object.

                // set to instruction

                // On failure instantiate new ROSMessage based generic class.

                //set to instruction


            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
        }
        // set source to null.
        this.source = null;
    }
    private
            File file;

    /**
     * Create folder if one does not exist.
     */
    private
            String getFolder(String folder)
    {
        if (folder != null)
        {
            this.file = new File(folder);
            if (!file.exists())
            {
                file.mkdirs();
            }
            this.file = null;
        }
        return folder;
    }

    /**
     * Return the ROS InstructionInputHandler. Return null, if it could not be
     * found.
     */
    @Override
    public
            InstructionInputHandler getInstructionInputHandler()
    {
        if (instructionInputHandler != null)
        {
            return instructionInputHandler;
        }
        else if (name == null || name.isEmpty())
        {
            return null;
        }

        if (getInstructionInputHandlerClassName() != null && getInstructionInputHandlerClassName().isEmpty() == false)
        {
            try
            {
                System.out.println("INFINITE LOOP IS IN THE InputHandler Constructor!!!!!!!!!!!!!!!!");
                System.out.println("1InputHandler: " + getInstructionInputHandlerClassName());
                System.out.println("1!!!!!!!!!!!!!!!!!!!!!!!!!1class_name: " + getInstructionInputHandlerClassName() + ", package: " + package_name + ", schema: " + getSchema() + ", hid: " + getHID());
                return this.instructionInputHandler = (InstructionInputHandler) Class.forName(getInstructionInputHandlerClassName()).getDeclaredConstructor(new Class[]
                {
                    Controller.class, int.class
                }).newInstance(rcsm_provider.getController(), hid);
            }
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (NoSuchMethodException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (SecurityException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (InstantiationException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (IllegalAccessException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (IllegalArgumentException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (InvocationTargetException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }

        }
        else
        {
            //DefinitionToInputHandlerGenerator.process(hid,class_name, package_name, getSchema(), rosNode.getController());
            // Generate the InstructionInputHandler class
            updateInstructionSource();
            System.out.println("2InputHandler: " + getInstructionInputHandlerClassName());
            System.out.println("2!!!!!!!!!!!!!!!!!!!!!!!!!1class_name: " + getInstructionInputHandlerClassName() + ", package: " + package_name + ", schema: " + getSchema() + ", hid: " + getHID());

            try
            {
                System.out.println("getInstructionInputHandlerClassName(): " + getInstructionInputHandlerClassName());
                this.instructionInputHandler = (InstructionInputHandler) Class.forName(getInstructionInputHandlerClassName()).getDeclaredConstructor(new Class[]
                {
                    Controller.class, int.class
                }).newInstance(rcsm_provider.getController(), hid);
// TODO: Determine why the instructionInputHandler is not returned.
            }
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (NoSuchMethodException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (SecurityException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (InstantiationException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (IllegalAccessException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (IllegalArgumentException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
            catch (InvocationTargetException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, "hid: ".concat(String.valueOf(hid)), ex);
            }
        }

//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!1class_name: "+class_name + ", package: "+package_name+", schema: " + getSchema() + ", hid: " + getHID());
//            System.out.println("SRC:\n" + DefinitionToInputHandlerGenerator.process(this.getHID(),class_name, package_name, getSchema(), rosNode.getController()));

        return null;
    }

    @Override
    public
            InstructionOutputHandler getInstructionOutputHandler()
    {
        if (instructionOutputHandler != null)
        {
            return instructionOutputHandler;
        }
        if (getInstructionOutputHandlerClassName() != null && getInstructionOutputHandlerClassName().isEmpty() == false)
        {
            try
            {
                return this.instructionOutputHandler = (InstructionOutputHandler) Class.forName(getInstructionOutputHandlerClassName()).getDeclaredConstructor(new Class[]
                {
                    Controller.class, int.class
                }).newInstance(rcsm_provider.getController(), hid);
            }
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (NoSuchMethodException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SecurityException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InstantiationException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IllegalAccessException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IllegalArgumentException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InvocationTargetException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        else
        {
            //DefinitionToInputHandlerGenerator.process(hid,class_name, package_name, getSchema(), rosNode.getController());
            // Generate the InstructionOutputHandler class
            updateInstructionSource();
            try
            {
                this.instructionOutputHandler = (InstructionOutputHandler) Class.forName(getInstructionOutputHandlerClassName()).getDeclaredConstructor(new Class[]
                {
                    Controller.class, int.class
                }).newInstance(rcsm_provider.getController(), hid);

            }
            catch (ClassNotFoundException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (NoSuchMethodException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SecurityException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InstantiationException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IllegalAccessException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IllegalArgumentException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (InvocationTargetException ex)
            {
                Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Return all associated Schema File Extensions for RCSM Provider Messages.
     * @return String[]
     */
    @Override
    public abstract String[] getSchemaFileExtensions();

    /**
     * Return the RCSM Provider specific RunnableInstructionBuilder
     * implementation.
     *
     * @return RunnableInstructionBuilder
     */
    @Override
    public abstract RunnableInstructionBuilder getRunnableInstructionBuilder();

    /**
     * Return the InstructionObjectPool associated with the
     * InstructionInputHandler implementation. Return null, if it is not
     * implemented in the InstructionInputHandler.
     */
    @Override
    public InstructionObjectPool getInstructionObjectPool()
    {
        if (pool != null)
        {
            return pool;
        }
        if (instructionInputHandler != null)
        {
            this.pool = instructionInputHandler.getInstructionObjectPool();
            return pool;
        }
        else
        {
            if (getInstructionObjectPoolClassName() != null && getInstructionObjectPoolClassName().isEmpty() == false)
            {
                try
                {
                    // Instantiate the class       
                    return this.pool = (InstructionObjectPool) Class.forName(getInstructionObjectPoolClassName()).getDeclaredConstructor(new Class[]
                    {
                        int.class
                    }).newInstance(hid);
                }
                catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (NoSuchMethodException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (SecurityException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (InstantiationException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IllegalAccessException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (InvocationTargetException ex)
                {
                    Logger.getLogger(AbstractInstructionDefinition.class.getName()).log(Level.SEVERE, null, ex);
                }
                return pool;
            }
            else
            {
                // is null
                return null;
            }
        }
    }

    @Override
    /**
     * Return a List of DynamicSourceCodeObject for the Message Compiler to
     * build.
     *
     * @return List<DynamicSourceCodeObject> DynamicSourceCodeObject List of
     * Source Code, and Class Names for the Java Compiler.
     */
    public List<MessageCompiler.DynamicSourceCodeObject> getSource()
    {
        if (sourceCode == null)
        {
            this.sourceCode = new ArrayList<MessageCompiler.DynamicSourceCodeObject>();
            // Generate Source Code      
          //  System.out.println("CALLING UPDATE INSTRUCTION SOURCE...");
            updateInstructionSource();
          //  System.out.println("CALLED UPDATE INSTRUCTION SOURCE...");
        }
        return sourceCode;
    }

    /**
     * Internal method: Add a DynamicSourceCodeObject to the Source Code List.
     * Used by source code generator to add generated Objects to the
     * InstructionDefinition Source Code getInstructionSource method call.
     */
    @Override
    public void addDynamicSourceCodeObject(MessageCompiler.DynamicSourceCodeObject source)
    {
//        System.out.println("ADDING DYNAMIC SOURCE CODE OBJECT..." + source.getName() +", "+source.toString());
        if (sourceCode == null)
        {
            this.sourceCode = new ArrayList<MessageCompiler.DynamicSourceCodeObject>();
        }
        else
        {
            sourceCode.add(source);
            //System.out.println("ADD SOURCE. sourceCode.size():" + sourceCode.size() + ", SOURCE: " + source.contents);
        }
    }
    
    /** Return the InstructionDefinition's RCSMProvider. */
    public RCSMProvider getRCSMProvider()
    {
        return rcsm_provider;
    }
}
