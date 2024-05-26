package org.happy.artist.rmdmia.rcsm.provider.message;

 /** This interface returns a suggested default message byte[] block size 
  * for processing incoming data communications. A Tuner message handler 
  * will implement MessageHandlerInterface and this interface. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public interface MessageHandlerTunerInterface 
{
    /** Return the length of the largest received message byte[] block 
     * size for processing incoming data communications.
     * 
     * @return int largest received message byte[] block size for processing incoming 
     * data communications.
     */
    public int getLargestReceivedBlockSize();
}