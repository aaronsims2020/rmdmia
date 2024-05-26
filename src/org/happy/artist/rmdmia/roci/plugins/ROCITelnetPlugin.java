package org.happy.artist.rmdmia.roci.plugins;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.roci.ROCIProvider;

/**
 * org.happy.artist.rmdmia.roci.plugins.ROCITelnetServerPlugin class is an example 
 * ROCI plugin implementation. The ROCITelnetServerPlugin is a TelnetServer based command 
 * interface to the RMDMIA System.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public class ROCITelnetPlugin extends ROCIProvider
{
    private final static String name="telnet"; 
    private final static double version = 1.0;       
    private int port=7777;
    private int max_connections = 1;
    private Properties properties;
    
    private ServerSocket listener;
    private Socket server;
    private Thread t;
    private OutputStream os;
    private InputStream is;
    // initialize method variables
    private boolean isInitialized=false;    
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(ROCITelnetPlugin.class.getName());    
    
    /** Initialize the ROCI TelnetServer Plugin. */
    public void initialize() 
    {
        // Set isInitialized to true.
        this.isInitialized=true;          
        logger.log(Level.INFO, "Initializing telnet.");
        // Set the ROCI Provider Data Transfer method to streaming (TelnetServer uses streaming).
        setDataTransferMethod(this.STREAMING_DATA_TRANSFER_METHOD);        
        // Setup the ROCI Plugin properties.
        this.properties=getProperties();
        try
        {
            this.port=Integer.parseInt((String)properties.get("port"));
        }
        catch(NullPointerException e)
        {
            logger.log(Level.SEVERE, "A telnet \"port\" property key was not specified for ROCI Plugin Properties. Defaulting to port: " + port);
        }
        catch(NumberFormatException e)
        {
            logger.log(Level.SEVERE, "A telnet port was not specified for ROCI Plugin Properties. Defaulting to port: " + port);
        }
        try
        {
            this.max_connections=Integer.parseInt((String)properties.get("max_connections"));
        }
        catch(NullPointerException e)
        {
            logger.log(Level.WARNING, "A telnet \"maxConnections\" property key was not specified for ROCI Plugin Properties. Defaulting to unlimited connections.");
        }
        catch(NumberFormatException e)
        {
            logger.log(Level.WARNING, "A telnet \"maxConnections\" int property value was not specified for ROCI Plugin Properties. Defaulting to unlimited connections.");
        }        
logger.log(Level.INFO, "Opening TelnetServer sockets.");        
        int i = 0;

        try {
            this.listener = new ServerSocket(port);

            while ((i++ < max_connections) || (max_connections == 0)) 
            {
                this.server = listener.accept();
                TelnetServer connection = new TelnetServer(this.server);
                this.t = new Thread(connection);
                t.start();
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }        
        
        logger.log(Level.INFO, "TelnetServer initialized");
    }
    
    /** Return the plugin version number. A version number of 0 is default of no version number. */
    public double getVersion() 
    {
        return version;
    }
 
    /** Return the ROCI Plugin name. */ 
    public String getName() 
    {
        return name;
    }
    
    @Override
    public void setOutputStream(OutputStream os) {
    this.os=os;
        }

    @Override
    public void setInputStream(InputStream is) {
        this.is=is;
    }

    @Override
    public InputStream getInputStream() {
        return is;
    }

    @Override
    public OutputStream getOutputStream() 
    {
        return os;    
    }

    @Override
    public void processIncoming(byte[] data) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processOutgoing(byte[] data) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean recycle() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shutdown() 
    {
        // Set isInitialized to false.
        this.isInitialized=false; 
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Return boolean is initialized on ROCI Provider. 
     * @return boolean returns false if ROCI Provider is not initialized. 
     */   
    public boolean isInitialized() 
    {
        return isInitialized;
    }    
    
    // Listen for incoming connections and handle them
    public static void main(String[] args) 
    {
        ROCITelnetPlugin plugin = new ROCITelnetPlugin();
        plugin.initialize();
        plugin.shutdown();
    }

class TelnetServer implements Runnable 
{
    private DataInputStream in;
    private PrintStream out;
    private Socket server;
    private String line, input;

    TelnetServer(Socket server) 
    {
        this.server = server;
    }

    public void run() 
    {
        this.input = "";
        try 
        {
            // Get input from the client
            this.in = new DataInputStream(server.getInputStream());
            this.out = new PrintStream(server.getOutputStream());
// TODO: manage stream in parent class.            
            // Set the ROCI OutputStream
            ROCITelnetPlugin.this.setOutputStream(out);            
            // Set the ROCI InputStream
            ROCITelnetPlugin.this.setInputStream(in);
            
            while ((line = in.readLine()) != null && !line.equals(".")) 
            {
                this.input = input + line;
                out.println("I got:" + line);
            }

            // Now write to the client

            System.out.println("Overall message is:" + input);
            out.println("Overall message is:" + input);

            server.close();
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }
}
}
