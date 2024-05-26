package org.happy.artist.rmdmia.rcsm.providers.ros;

import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSSubscriberCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPROSSubscriberCommunicator;

/** ROSPublisher manages the data communications for sending data to a 
 * specific topic, and managing multiple connections/protocols for 
 * subscribers of defined topic.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 */
public class ROSPublisher 
{
    //TODO: Replace Communicator classes with Publisher classes with support for multiple incoming connections on a single port.    
    // Define the TCPROS Communicator array, and instantiate to 0 length.
    private TCPROSSubscriberCommunicator[] tcp_comm = new TCPROSSubscriberCommunicator[0];
    // Define the UDPROS Communicator array, and instantiate to 0 length.    
    private UDPROSSubscriberCommunicator[] udp_comm = new UDPROSSubscriberCommunicator[0];    
    // Define the publisher topic
    private String topic;
    
    /** Set the published topic associated with this instance of 
     * ROSPublisher. */
    public void setTopic(String topic)
    {
        this.topic=topic;
    }
    
    /** Return the topic associated with this instance of ROSPublisher. */
    public String getTopic()
    {
        return topic; 
    }    
/////////////////////////////////

/*public class DoSomethingWithInput implements Runnable {
   private final Socket clientSocket; //initialize in const'r
   public void run() {

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String nextline;
        while ((nextline = in.readLine())!=null) {
           System.out.println(nextline);
        } //... close socket, etc.
    }
}*/

//...
//ServerSocket serverSocket = null;
//serverSocket = new ServerSocket(5432);
public ROSPublisher()
{

}    

    public void run()
    {
        while (true) 
        {
//            Socket clientSocket = null;
//            clientSocket = serverSocket.accept();
            //delegate to new thread
//            new Thread(new DoSomethingWithInput(clientSocket)).start();    
        }
    }
    
}
