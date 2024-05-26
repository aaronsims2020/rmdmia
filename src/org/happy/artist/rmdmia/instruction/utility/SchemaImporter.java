package org.happy.artist.rmdmia.instruction.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.ControllerManager;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageCompiler;

/**
 * org.happy.artist.rmdmia.instruction.utility.SchemaImporter class reads 
 * schema files from an RCSM Provider associated directory, root directory defined in 
 * the instruction_manager.properties file.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved. 
 */
public class SchemaImporter 
{
    public final static List getSchemaFileList(String rcsm_provider_name, final String file_extension, String schema_folder_path) throws IOException, InterruptedException 
    {
        // Set the folder to search.
        final Path rootDir = Paths.get(schema_folder_path,rcsm_provider_name);
        final List list= new ArrayList();
        if(!rootDir.toFile().exists())
        {
            // If Path does not exist, create it.
            rootDir.toFile().mkdirs();
        }
        
        // Walk thru mainDir directory
        Files.walkFileTree(rootDir, new FileVisitor<Path>() 
        {
            // First (minor) speed up. Compile regular expression pattern only one time.
            //(.*?)(?:\\.(file_extension))?
//            "([^\\s]+(\\.(?i)(txt|doc|csv|pdf))$)"
            private Pattern pattern = Pattern.compile("^(.*?)");
                    //"(?<path>(\\w+/)+)((?<name>\\w+|[*]))?([.](?<extension>\\w+|[*]))?");
                    //Pattern.compile("^(.*?)");

            @Override
                    public FileVisitResult preVisitDirectory(Path path,
                    BasicFileAttributes atts) throws IOException {

                boolean matches = pattern.matcher(path.toString()).matches();

                // TODO: Put here your business logic when matches equals true/false

                return (matches)? FileVisitResult.CONTINUE:FileVisitResult.SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts)
                    throws IOException {
                boolean matches = path.toString().endsWith(file_extension);
                // if matches is true add path to List
                if(matches)
                {
                    list.add(path.toString());
                }

                // TODO: Put here your business logic when matches equals true/false

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path path,
                    IOException exc) throws IOException {
                // TODO Auto-generated method stub
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path path, IOException exc)
                    throws IOException {
                exc.printStackTrace();

                // If the root directory has failed it makes no sense to continue
                return path.equals(rootDir)? FileVisitResult.TERMINATE:FileVisitResult.CONTINUE;
            }
        });
        return list;
    }
 
    public static void generateSchemaInstructions(int hid, String file_path, Controller controller, String rcsm_provider_name)
    {
 /*       ExecutorService executorService = Executors.newCachedThreadPool();
        String result=null;
        Set<Callable<String>> callables = new HashSet<Callable<String>>();
        RunnableInstructionBuilder builder;
        //System.out.println(file_paths.size());
        //for(int i=0;i<file_paths.size();i++)
        //{
        try
        {
//            executorService.execute(controller.getRCSM().getProviderByName(rcsm_provider_name).getInstructionDefinition().getRunnableInstructionBuilder().setFilePath(file_paths.get(i)));  
            builder=controller.getRCSM().getProviderByName(rcsm_provider_name).getInstructionDefinition().getRunnableInstructionBuilder();
            // Set the RunnableInstructionBuilder hid
            builder.hid=hid;
            executorService.execute(builder.setFilePath(file_path));
        }
        catch(Exception e)
        {
            Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, null, e);
        }
        //}
        executorService.shutdown();
        */
        //RunnableInstructionBuilder builder;
        ExecutorService executorService=null;
        try
        {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());        
    //ExecutorService es = Executors.newFixedThreadPool(2);
    List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(1);

            RunnableInstructionBuilder builder=controller.getRCSM().getProviderByName(rcsm_provider_name).getInstructionDefinition().getRunnableInstructionBuilder();
            // Set the RunnableInstructionBuilder hid
            builder.hid=hid;
            tasks.add(Executors.callable(builder.setFilePath(file_path))); 
            executorService.invokeAll(tasks);
            //List<Future<Object>> answers = es.invokeAll(todo);        
        }
        catch(Exception e)
        {
            Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, null, e);
        }
        if(executorService!=null)
        {
            executorService.shutdown();
        }
        /*      try 
        {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } 
        catch (InterruptedException e) 
        {
        // Thread Interupt Ignore.
        }
    */    
    }

    /** Generate Schema Instructions synchronously. Used to Generate uncompiled included classes in Message Definitions, before compilation of the parent definition. */
    public static void generateSchemaInstructionsSynchronously(int hid, List<String> file_paths, Controller controller, String rcsm_provider_name)
    {
        int last_folder_separator;
        String class_name;
        String source;
        String package_name="";
        String temp_string;       
        String file_path;
        final String[] extensions = controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).getSchemaFileExtensions();        
        for(int i=0;i<file_paths.size();i++)
        {
            file_path=file_paths.get(i);
            try 
            {
                // Work to do
                // Read Schema File
//                System.out.println("getFilePath(): " + file_path);
                source = new String(Files.readAllBytes(Paths.get(new File(file_path).toURI())));     
                
    // TODO: Determine the package_name & class_name  
                // using the complete Schema file path (the .msg, or .srv file path), and the RCSM Provide name that is the data/im/<rcsm_provider_name>

                package_name=getROSPackage(file_path,controller.getInstructionManager().getSchemaFolderPath(),rcsm_provider_name,extensions);    
                //System.out.println(schema_data);
    // convert schema data to Java source code.
//                System.out.println("File based Message definition: ");
                if((last_folder_separator=file_path.lastIndexOf("/"))!=-1)
                {
                    class_name=file_path.substring(last_folder_separator+1);
                }
                else
                {
                    class_name=file_path;
                }
                
                // remove the file extension from the path
                String extension;
                for(int extension_loop_count=0;extension_loop_count<extensions.length;extension_loop_count++)
                {
                    extension=".".concat(extensions[extension_loop_count]);
                    if(class_name.indexOf(extension)!=-1)
                    {
                        class_name=class_name.substring(0,class_name.indexOf(extension));
                    }                    
                }

    //            System.out.println("DefinitionToMessageGenerator.process("+class_name+", " + package_name + ", controller)");
                // Generate Java source code.
                source = controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).getRunnableInstructionBuilder().getDefinitionToMessageInstructionSourceCodeGeneratorInterface().process(hid, class_name, package_name, source,controller);
                Logger.getLogger(SchemaImporter.class.getName()).log(Level.FINE, source);
                
    // Build 
                String classFolderPath=null;
                try 
                {   
                    classFolderPath=controller.getInstructionManager().getClassFolderPath();
                    if(classFolderPath.endsWith("/")==false)
                    {
                        classFolderPath=classFolderPath.concat("/");
                    }
                    classFolderPath=classFolderPath.concat(rcsm_provider_name.concat("/"));
                  //  if(classFolderPath.startsWith("/")==false)
                   // {
                        //="/".concat(classFolderPath);
                  //  }
                    if(!ControllerManager.IS_ANDROID)
                    {
                        //System.out.println("CLASS_NAME: " + class_name + ", classFolderPath: " + classFolderPath);
                            // is Java
// TODO: UNCOMMENT
//                            MessageCompiler.compile(source, class_name, getFolder(classFolderPath));
//                        System.out.println("Instruction SOURCE CODE: SchemaImporter - "+source);
                        controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name,source));
                    }
                    else
                    {
                        // is Android
// TODO: UNCOMMENT
//                        MessageCompiler.compile(source, class_name, getFolder(controller.getControllerManager().context.getFilesDir() + classFolderPath));
                        controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hid).addDynamicSourceCodeObject(new MessageCompiler.DynamicSourceCodeObject(class_name,source));            
                    }                       
                } 
                catch (Exception ex) 
                {
                    Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, "class_name: " + class_name + ", classFolderPath: " + classFolderPath, ex);
                }                       

            } 
            catch(NullPointerException ex)
            {
                Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, "getFilePath(): " + file_path);
            }
            catch (IOException ex) {
                Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, null, ex);
            }            
         } 
    }

   /** Return Schema synchronously. Used to obtain nested Message Definitions 
    * for an undefined parent Object for generation of InstructionInputHandler, 
    * and InstructionOutputHandler
    */
    public static String getSchemaFile(String file_path, Controller controller, String rcsm_provider_name)
    {
        String class_name;
        String source;    
        try
        {
            // Read Schema File
            return source = new String(Files.readAllBytes(Paths.get(new File(file_path).toURI())));
        }
        catch (IOException ex)
        {
            Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
    /** Create folder if one does not exist. */
    private static String getFolder(String folder)
    {
        File file;
        if(folder!=null)
        {
            file = new File(folder);
            if(!file.exists())
            {
                file.mkdirs();
            }
            file=null;
        }
        return folder;
    }       
    
    /** Pass in a .msg, or a .srv, file path, and return the new package name. Input schema_rcsm_folder is the sum of InstructionManager.getSchemaFilePath() + child folder getRCSMProviderName(). */
    private static String getROSPackage(String message_path, String schema_folder,String rcsm_provider_name, String[] rcsm_provider_file_extensions)
    {
        String temp_substring;
        String temp_string;
        String temp_schema_folder;

        if(schema_folder.endsWith("/")==false)
        {
            temp_schema_folder=schema_folder.concat("/").concat(rcsm_provider_name);
        }
        else
        {
            temp_schema_folder=schema_folder.concat(rcsm_provider_name);
        }
        
        String extension;
        temp_string=temp_substring=message_path.substring(message_path.indexOf(temp_schema_folder)+temp_schema_folder.length()+1);
        for(int extension_loop_count=0;extension_loop_count<rcsm_provider_file_extensions.length;extension_loop_count++)
        {
            extension=".".concat(rcsm_provider_file_extensions[extension_loop_count]);
            temp_string=temp_substring=temp_substring.replaceAll("/".concat(extension).concat("/"),"/");
        }        
        
        return rcsm_provider_name.concat("/").concat(temp_substring.substring(0,temp_substring.lastIndexOf("/"))).replaceAll("/",".");
    }     
    
    public static void main(String[] args)
    {
        try {
            System.out.println("File Array: ".concat(Arrays.toString(SchemaImporter.getSchemaFileList("ros", "msg", "msg").toArray())));
//            generateSchemaInstructions(SchemaImporter.getSchemaFileList("ros", "msg", "msg"));
        } catch (IOException ex) {
            Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SchemaImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
