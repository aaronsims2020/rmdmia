package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;

import org.happy.artist.rmdmia.rcsm.provider.message.MessageCompiler;
import org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.ControllerManager;
import org.happy.artist.rmdmia.rcsm.provider.message.DefinitionToMessageInstructionSourceCodeGeneratorInterface;

/**
 * org.happy.artist.rmdmia.instruction.utility.ROSRunnableInstructionBuilder
 * a threaded ROS message file reader, and compiler for .srv, and .msg files. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved. 
 */
public class ROSRunnableInstructionBuilder extends RunnableInstructionBuilder
{
    private int last_folder_separator;
    private String class_name;
    private String msg_source;
    private String java_source;
    private String package_name="";
    private String temp_string;
    private Map parent_map;
    private DefinitionToMessageGenerator sourceGenerator;
    
    public ROSRunnableInstructionBuilder(Controller controller, String rcsm_provider_name)
    {
        super(controller,rcsm_provider_name);
        this.sourceGenerator=new DefinitionToMessageGenerator();
    }

    @Override
    public void run() 
    {
        try 
        {
            // Work to do
            // Read Schema File
//            System.out.println("getFilePath(): " + getFilePath());
            this.msg_source = new String(Files.readAllBytes(Paths.get(new File(getFilePath()).toURI())));            
// TODO: Determine the package_name & class_name  
            // using the complete Schema file path (the .msg, or .srv file path), and the RCSM Provide name that is the data/im/<rcsm_provider_name>
            
            this.package_name=ROSRunnableInstructionBuilder.getROSPackage(getFilePath(),getController().getInstructionManager().getSchemaFolderPath(),getRCSMProviderName());    
            //System.out.println(schema_data);
// convert schema data to Java source code.
//            System.out.println("ROSRunnableInstructionBuilder Message definition: ");
            if((this.last_folder_separator=getFilePath().lastIndexOf("/"))!=-1)
            {
                this.class_name=getFilePath().substring(last_folder_separator+1);
            }
            else
            {
                this.class_name=getFilePath();
            }
            // remove the .msg, or .srv extension
            if(class_name.indexOf(".msg")!=-1)
            {
                class_name=class_name.substring(0,class_name.indexOf(".msg"));
            }
            else if(class_name.indexOf(".srv")!=-1)
            {
                class_name=class_name.substring(0,class_name.indexOf(".srv"));
            }
            
//            System.out.println("DefinitionToMessageGenerator.process("+class_name+", " + package_name + ", controller)");
            // Generate Java source code.
            if(parent_map==null)
            {
                this.parent_map=new HashMap();
            }
            this.java_source = sourceGenerator.process(ROSRunnableInstructionBuilder.this.hid,class_name, package_name, msg_source,getController());

      
// Build 
            String classFolderPath=null;
            try 
            {   
                classFolderPath=getController().getInstructionManager().getClassFolderPath();
                if(classFolderPath.endsWith("/")==false)
                {
                    classFolderPath=classFolderPath.concat("/");
                }
                classFolderPath=classFolderPath.concat("ros/");
              //  if(classFolderPath.startsWith("/")==false)
               // {
                    //="/".concat(classFolderPath);
              //  }
                if(!ControllerManager.IS_ANDROID)
                {
                    //System.out.println("CLASS_NAME: " + class_name + ", classFolderPath: " + classFolderPath);
                        // is Java
 // TODO: UNCOMMENT
//                    MessageCompiler.compile(java_source, class_name, getFolder(classFolderPath));
                    // Create a DynamicSourceCodeObject and append to the InstructionManager Provider InstructionDefinition DynamicSourceCodeObject List
//                    System.out.println("Instruction SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name,java_source));
                }
                else
                {
                    // is Android
// TODO: UNCOMMENT
//                    //MessageCompiler.compile(java_source, class_name, getFolder(getController().getControllerManager().context.getFilesDir() + classFolderPath));
//                    System.out.println("Instruction SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name,java_source));
                }   

// TODO: Build the SRC for InstructionInputHandler, and InstructionOutputHandler
// TODO: REplace -1 with actual hid if possible for updating the properties file with class_name and variables.               
                Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.FINE, "SOURCE: ".concat("class_name").concat(":\n").concat(java_source));

                this.java_source = sourceGenerator.getObjectPoolSRC(class_name,package_name);
                Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.FINE, "SOURCE: ".concat("class_name").concat("InstructionPool").concat(":\n").concat(java_source));                
                if(!ControllerManager.IS_ANDROID)
                {
                    //System.out.println("CLASS_NAME: " + class_name + "InstructionPool, classFolderPath: " + classFolderPath);
                        // is Java
// TODO: UNCOMMENT                    
//                        MessageCompiler.compile(java_source, class_name.concat("InstructionPool"), getFolder(classFolderPath));
//                    System.out.println("InstructionPool SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name.concat("InstructionPool"),java_source));
                }
                else
                {
                    // is Android
// TODO: UNCOMMENT
//                    MessageCompiler.compile(java_source, class_name.concat("InstructionPool"), getFolder(getController().getControllerManager().context.getFilesDir() + classFolderPath));
//                    System.out.println("InstructionPool SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name.concat("InstructionPool"),java_source));
                }   
//   l             
                //System.out.println("GENERATING ".concat(class_name).concat("InputHandler source code:"));                
                this.java_source = DefinitionToInputHandlerGenerator.process(hid,class_name, package_name, msg_source,getController());
                Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.FINE, "SOURCE ".concat("class_name").concat("InputHandler").concat(":\n").concat(java_source));                   
                // Compile the InputHandler
                if(!ControllerManager.IS_ANDROID)
                {
//                    System.out.println("CLASS_NAME: " + class_name + ", classFolderPath: " + classFolderPath);
                    // is Java
// TODO: UNCOMMENT
//                    MessageCompiler.compile(java_source, class_name.concat("InputHandler"), getFolder(classFolderPath));
//                    System.out.println("InputHandler SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name.concat("InputHandler"),java_source));
                    // Add new InputHandler/SensorProcessorFactory to the SensorProcessorManagerImpl
                    // TODO: Replace this cast when the framework expands outside of the ROS implementation.

                    
// TODO: UNCOMMENT
//                    ((SensorProcessorManagerImpl)ROSRunnableInstructionBuilder.this.getController().getPE().getProviderByName("ros_pe_provider").getSensorProcessorManager()).add_save(hid, (SensorProcessorFactory) Class.forName(package_name.concat(".").concat(class_name).concat("InputHandler")).getDeclaredConstructor(new Class[] {Controller.class, int.class}).newInstance(ROSRunnableInstructionBuilder.this.getController(),hid), false);
                }
                else
                {
                    // is Android
// TODO: UNCOMMENT
//                    MessageCompiler.compile(java_source, class_name.concat("InputHandler"), getFolder(getController().getControllerManager().context.getFilesDir() + classFolderPath));
//                    System.out.println("InputHandler SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name.concat("InputHandler"),java_source));
                    
                    // Add new InputHandler/SensorProcessorFactory to the SensorProcessorManagerImpl
                    // TODO: Replace this cast when the framework expands outside of the ROS implementation.
                    
// TODO: UNCOMMENT
//                    ((SensorProcessorManagerImpl)ROSRunnableInstructionBuilder.this.getController().getPE().getProviderByName("ros_pe_provider").getSensorProcessorManager()).add_save(hid, (SensorProcessorFactory) Class.forName(package_name.concat(".").concat(class_name).concat("InputHandler")).getDeclaredConstructor(new Class[] {Controller.class, int.class}).newInstance(ROSRunnableInstructionBuilder.this.getController(),hid), false);                    
                
                }   
//  Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.INFO, "TODO: Add code for InstructionOutputHandler.");               
                this.java_source = OutputHandlerToMessageGenerator.process(hid,class_name, package_name, msg_source,getController());
 //System.out.println("OutputHandler:\n"+java_source+"\n");
                Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.FINE, "SOURCE ".concat("class_name").concat("OutputHandler").concat(":\n").concat(java_source));                   
                // Compile the InputHandler
                if(!ControllerManager.IS_ANDROID)
                {
//                    System.out.println("CLASS_NAME: " + class_name + ", classFolderPath: " + classFolderPath);
                    // is Java
// TODO: UNCOMMENT
//                    MessageCompiler.compile(java_source, class_name.concat("OutputHandler"), getFolder(classFolderPath));
//                    System.out.println("OutputHandler SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name.concat("OutputHandler"),java_source));
                    // Add new InputHandler/SensorProcessorFactory to the SensorProcessorManagerImpl
                    // TODO: Replace this cast when the framework expands outside of the ROS implementation.

                    
// TODO: UNCOMMENT
//                    ((SensorProcessorManagerImpl)ROSRunnableInstructionBuilder.this.getController().getPE().getProviderByName("ros_pe_provider").getSensorProcessorManager()).add_save(hid, (SensorProcessorFactory) Class.forName(package_name.concat(".").concat(class_name).concat("OutputHandler")).getDeclaredConstructor(new Class[] {Controller.class, int.class}).newInstance(ROSRunnableInstructionBuilder.this.getController(),hid), false);
                }
                else
                {
                    // is Android
// TODO: UNCOMMENT
//                    MessageCompiler.compile(java_source, class_name.concat("OutputHandler"), getFolder(getController().getControllerManager().context.getFilesDir() + classFolderPath));
//                    System.out.println("OutputHandler SOURCE CODE: ROSRunnableInstructionBuiler - "+java_source);
                    ROSRunnableInstructionBuilder.this.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name.concat("OutputHandler"),java_source));
                    
                    // Add new InputHandler/SensorProcessorFactory to the SensorProcessorManagerImpl
                    // TODO: Replace this cast when the framework expands outside of the ROS implementation.
                    
// TODO: UNCOMMENT
//                    ((SensorProcessorManagerImpl)ROSRunnableInstructionBuilder.this.getController().getPE().getProviderByName("ros_pe_provider").getSensorProcessorManager()).add_save(hid, (SensorProcessorFactory) Class.forName(package_name.concat(".").concat(class_name).concat("OutputHandler")).getDeclaredConstructor(new Class[] {Controller.class, int.class}).newInstance(ROSRunnableInstructionBuilder.this.getController(),hid), false);                    
                
                }   

            
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.SEVERE, "class_name: " + class_name + ", classFolderPath: " + classFolderPath, ex);
            }                       
                       
        } 
        catch(NullPointerException ex)
        {
            Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.SEVERE, "getFilePath(): " + getFilePath());
        }
        catch (IOException ex) {
            Logger.getLogger(org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Pass in a .msg, or a .srv, file path, and return the new package name. Input schema_rcsm_folder is the sum of InstructionManager.getSchemaFilePath() + child folder getRCSMProviderName(). */
    private static String getROSPackage(String message_path, String schema_folder,String rcsm_provider_name)
    {
        String temp_substring;
        String temp_string;
        String temp_schema_folder;
        int last_index;

        if(schema_folder.endsWith("/")==false)
        {
            temp_schema_folder=schema_folder.concat("/").concat(rcsm_provider_name);
        }
        else
        {
            temp_schema_folder=schema_folder.concat(rcsm_provider_name);
        }
        temp_string=temp_substring=message_path.substring(message_path.indexOf(temp_schema_folder)+temp_schema_folder.length()+1).replaceAll("/msg/","/").replaceAll("/srv/","/");
        last_index=temp_substring.lastIndexOf("/");
        if(last_index==-1)
        {
            // No / found...
//            System.out.println("TEMP_SUBSTRING: " + rcsm_provider_name.replaceAll("/","."));
            return rcsm_provider_name.concat("/").concat(temp_substring).replaceAll("/",".");
        }
        else
        {
            // / found.
//            System.out.println("TEMP_SUBSTRING2: " + rcsm_provider_name.concat("/").concat(temp_substring.substring(0,temp_substring.lastIndexOf("/"))).replaceAll("/","."));            
            return rcsm_provider_name.concat("/").concat(temp_substring.substring(0,temp_substring.lastIndexOf("/"))).replaceAll("/",".");
        }
    } 
    
    /** Pass in a .msg, or a .srv, file path, and return the new Class package folder. Input schema_rcsm_folder is the sum of InstructionManager.getSchemaFilePath() + child folder getRCSMProviderName(). */
    private static String getROSPackageFolder(String package_name, String class_folder,String rcsm_provider_name)
    {
        String temp_string;
        if(ControllerManager.IS_ANDROID)
        {
            // Is Android
            if(class_folder.endsWith("/")==false)
            {
                temp_string=class_folder.concat("/");
            }
            else
            {
                temp_string=class_folder;
            }            
            temp_string=temp_string.concat(rcsm_provider_name).concat("/");        
            temp_string=temp_string.concat(package_name.replaceAll("\\.", "/"));
            if(temp_string.startsWith("/")==false)
            {
                temp_string="/".concat(temp_string);
            }
            return temp_string;
        }   
        else
        {
            // Is Java
            if(class_folder.endsWith("/")==false)
            {
                temp_string=class_folder.concat("/");
            }
            else
            {
                temp_string=class_folder;
            }            
            temp_string=temp_string.concat(rcsm_provider_name).concat("/");        
            return temp_string.concat(package_name.replaceAll("\\.", "/"));
        }
    }     
    
    private File file;
    /** Create folder if one does not exist. */
    private String getFolder(String folder)
    {
        if(folder!=null)
        {
            this.file = new File(folder);
            if(!file.exists())
            {
                file.mkdirs();
            }
            this.file=null;
        }
        return folder;
    }    

    @Override
    public
    DefinitionToMessageInstructionSourceCodeGeneratorInterface getDefinitionToMessageInstructionSourceCodeGeneratorInterface()
    {
        return sourceGenerator;
    }
}
