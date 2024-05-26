package org.happy.artist.rmdmia.rcsm.provider.message;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale; 
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * MessageCompiler - Compiler for dynamically generated Java source code. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014-2015 Happy Artist. All rights reserved.
 */
public class MessageCompiler
{
    // Class Logger define & instantiation
    private final static Logger logger = Logger.getLogger(MessageCompiler.class.getName()); 
    
    private static class MessageDiagnosticListener implements DiagnosticListener<JavaFileObject>
    {
        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic)
        {
            final StringBuilder sb=new StringBuilder();
            sb.append("Line Number->");
            sb.append(diagnostic.getLineNumber());
            sb.append("\ncode->");
            sb.append(diagnostic.getCode());
            sb.append("\nMessage->");
            sb.append(diagnostic.getMessage(Locale.ENGLISH));
            sb.append("\nLine Number->");
            sb.append(diagnostic.getLineNumber());
            sb.append("\nSource->");
            sb.append(diagnostic.getSource());
            sb.append("\n ");
            logger.log(Level.WARNING, sb.toString()); 
        }
    }
 
    /** Java source file is in memory to avoid putting source file on hard disk. */
    public static class DynamicSourceCodeObject extends SimpleJavaFileObject
    {
        public String contents = null;
 
        public DynamicSourceCodeObject(String class_name, String contents) throws Exception
        {
            super(URI.create("string:///" + class_name.replace('.', '/')
                             + Kind.SOURCE.extension), Kind.SOURCE);
            this.contents = contents;
        }
 
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException
        {
            return contents;
        }
    }


    /** Return a DynamicSourceCodeObject array with no duplicates. Duplicates are not allowed by the compiler. */
    private static MessageCompiler.DynamicSourceCodeObject[] getNonDuplicatedSourceCodeObjectArray(List<MessageCompiler.DynamicSourceCodeObject> dynamicSourceCodeObjectList)
    {
        final Map<String, MessageCompiler.DynamicSourceCodeObject> linkedHashMap = new LinkedHashMap<String, MessageCompiler.DynamicSourceCodeObject>();
        final Iterator itr = dynamicSourceCodeObjectList.iterator();
        while(itr.hasNext()) 
        {
            MessageCompiler.DynamicSourceCodeObject element = (MessageCompiler.DynamicSourceCodeObject)itr.next();
            linkedHashMap.put(element.contents, element);
        }
        final Collection<MessageCompiler.DynamicSourceCodeObject> values = linkedHashMap.values();
        final MessageCompiler.DynamicSourceCodeObject[] dynamic_source_code_object_array = new MessageCompiler.DynamicSourceCodeObject[values.size()];
        return values.toArray(dynamic_source_code_object_array);
    }    
    
    
    
    /** Compile Java Source. Input parameter elements in dynamic_source_code_object_array. */
    public static boolean compile(List<MessageCompiler.DynamicSourceCodeObject> dynamic_source_code_object_list, String class_output_folder) throws Exception
    {
        final MessageCompiler.DynamicSourceCodeObject[] dynamic_source_code_object_array=getNonDuplicatedSourceCodeObjectArray(dynamic_source_code_object_list);
        logger.log(Level.FINEST, "Compiling List<MessageCompiler.DynamicSourceCodeObject>: ".concat(Arrays.deepToString(dynamic_source_code_object_array)));
        // If input parameters are null return false.
        if(dynamic_source_code_object_array==null||class_output_folder==null)
        {
            return false;
        }
        
        Iterable<? extends JavaFileObject> files = Arrays.asList(dynamic_source_code_object_array);
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MessageDiagnosticListener c = new MessageDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", class_output_folder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        return task.call();
    }      
    
    /** Compile Java Source. Input parameter elements in dynamic_source_code_object_array. */
    public static boolean compile(JavaFileObject[] dynamic_source_code_object_array, String class_output_folder) throws Exception
    {
        // If input parameters are null return false.
        if(dynamic_source_code_object_array==null||class_output_folder==null)
        {
            return false;
        }
        
        Iterable<? extends JavaFileObject> files = Arrays.asList(dynamic_source_code_object_array);
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MessageDiagnosticListener c = new MessageDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", class_output_folder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        return task.call();
    }    
    
    /** Compile Java Source. Input parameter elements in source_code_object_array, and class_names must match up. */
    public static boolean compile(String[] source_code_object_array, String[] class_names, String class_output_folder) throws Exception
    {
        // If input parameters are null return false.
        if(source_code_object_array==null||class_output_folder==null||class_names==null)
        {
            return false;
        }
        // convert source code string array to JavaFileObject array.
        JavaFileObject[] jfo_array=new JavaFileObject[source_code_object_array.length];
        for(int i=0;i<jfo_array.length;i++)
        {
            jfo_array[i]=new DynamicSourceCodeObject(class_names[i],source_code_object_array[i]);
        }
        
        Iterable<? extends JavaFileObject> files = Arrays.asList(jfo_array);
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MessageDiagnosticListener c = new MessageDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", class_output_folder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        return task.call();
    }
    
    /** Compile Java Source. Input parameter elements in source_code_object_array, and class_names must match up. */
    public static boolean compile(String source_code, String class_name, String class_output_folder) throws Exception
    {
        // If input parameters are null return false.
        if(source_code==null||class_output_folder==null||class_name==null)  
        {
            return false;
        }
        //System.out.println(source_code);
        // convert source code string array to JavaFileObject array.
        JavaFileObject jfo=new DynamicSourceCodeObject(class_name,source_code);        
        Iterable<? extends JavaFileObject> files = Arrays.asList(jfo);
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MessageDiagnosticListener c = new MessageDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", class_output_folder, "-cp", System.getProperty("java.class.path").concat(System.getProperty("path.separator")).concat(class_output_folder));


        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        return task.call();
    }    
 
    public static void main(String[] args) throws Exception
    {
        // Compile Java source code by String.
        String[] sourceCode = {"class HelloWorld{"
        + "public static void main (String args[]){"
        + "System.out.println (\"Hello world!\")"
        + "}"
        + "}"};
        String[] class_names={"org.test.HelloWorld"};
        // Test Java Compiler
  //      compile(sourceCode,class_names,"./");
        // Compile Java source code by String.
        // Test Java Compiler
        compile(sourceCode[0],class_names[0],"./");        
      }
}