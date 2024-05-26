package org.happy.artist.rmdmia.instruction.utility;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * org.happy.artist.rmdmia.instruction.utility.InstructionImporter class reads 
 * .class files from an RCSM Provider associated directory, root directory defined in 
 * the instruction_manager.properties file. Future release may support JAR extension.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved. 
 */
public class InstructionImporter 
{
    public final static List getClassFileList(String rcsm_provider_name, final String file_extension, String instruction_folder_path) throws IOException, InterruptedException 
    {
        // Set the folder to search.
        final Path rootDir = Paths.get(instruction_folder_path,rcsm_provider_name);
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
    
    public static void main(String[] args)
    {
        try {
            System.out.println("File Array: ".concat(Arrays.toString(InstructionImporter.getClassFileList("ros", ".class", "classes").toArray())));
//            generateInstructionInstructions(InstructionImporter.getInstructionFileList("ros", "msg", "msg"));
        } catch (IOException ex) {
            Logger.getLogger(InstructionImporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(InstructionImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
