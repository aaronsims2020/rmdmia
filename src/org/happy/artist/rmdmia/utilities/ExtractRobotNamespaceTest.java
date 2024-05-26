/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.happy.artist.rmdmia.utilities;
import java.io.*; 
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.utilities.properties.LinuxSystemProperties;
/**
 *
 * @author aaron
 */
public class ExtractRobotNamespaceTest 
{
    private static LinuxSystemProperties properties;
    public static void main(String args[]) 
    {

        try 
        {
            if(args.length==0)
            {
                System.out.println("Specify the file name of the output of calling env >> filename, for command line system properties used by gazebo/ros simulator. i.e. - java org.happy.artist.utilities.ExtractRobotNamespaceTest /usr/share/drc/system.properties");
            }
            else
            {
                System.out.println("File name: " + args[0]);
                LinuxSystemProperties.setFileName(args[0]);
            }
            ExtractRobotNamespaceTest.properties = LinuxSystemProperties.getInstance();
            // Set the Java System environment to the Linux Terminal Bash Properties.
            setEnv(((Map)properties.getProperties()));
            Map map = System.getenv();
            
            Object[] outNames = map.keySet().toArray();
  for (int pos = 0; pos < outNames.length; ++pos) {  
            String name = (String) outNames[pos];  
            Object value = map.get(name);  
   System.out.println("key: " + name + ", value: " + value);
        }              
            
            
            String arg[] = {"gztopic", "list"};
            Process p = new ProcessBuilder(arg).start();
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);        
                        String line = reader.readLine();
                        while (line != null) {
                            System.out.println(line);
                            line = reader.readLine();
                        }   
                        System.out.println("done");
            /*        try { 
            //            Process p = Runtime.getRuntime().exec("bash -c 'source /usr/share/drcsim/setup.sh';gztopic list");
                        //Process p = Runtime.getRuntime().exec("bash -c 'gztopic list'");
                        Process p = Runtime.getRuntime().exec("gztopic info /r_foot_contact");
                        p.waitFor();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = reader.readLine();
                        while (line != null) {
                            System.out.println(line);
                            line = reader.readLine();
                        }
            System.out.println("test complete");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Done");
                    */
        } catch (IOException ex) {
            Logger.getLogger(ExtractRobotNamespaceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

// From Stackoverflow for setting system environment variables. http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java    
protected static void setEnv(Map<String, String> newenv)
{
  try
    {
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.putAll(newenv);
        Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
        cienv.putAll(newenv);
    }
    catch (NoSuchFieldException e)
    {
      try {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for(Class cl : classes) {
            if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                Field field = cl.getDeclaredField("m");
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    } catch (Exception e1) {
        e1.printStackTrace();
    } 
}    
}
