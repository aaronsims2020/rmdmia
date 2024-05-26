package org.happy.artist.rmdmia.utilities.ros.bag;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/** ByteArrayRecordQueue.java - A byte[] FIFO queue for writing records to a Stream. 
 * To use call checkout on each read, and upon Message completion call reset, to 
 * start new message read. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013-2014 Happy Artist. All rights reserved.
 */
public abstract class ByteArrayRecordQueue extends Thread
{
    // READ_BLOCKS The initial quantity of the byte[] Data blocks.
    private int READ_BLOCKS=12;
    // The queue is an array of instantiated and recycled byte[] Objects.	
    private ByteArrayRecordQueue.Data[] queue; 
    // recycleCount used for recycle loop counting.     
    private int recycleCount=0;  
    
    // Thread is running method.
    private boolean is_running=false;

    // add method variables.    
    // The current processing element position.
    private int current_position=0;
    // The number of queued elements waiting to be processed.
    private int queue_count=0;
    // Add current Loop count
//    private int loop_count=0;
    // Availability Array
    private boolean[] availability_array;
    // queue length divisor
    private int queue_length_divisor;    
    
// Complete.    
    /**
     * org.happy.artist.rmdmia.utilities.ros.bag.ByteArrayRecordQueue constructor.
     * 
     *  @param refresh_ms
     *         The refresh ms for each Thread run loop cycle. .
     */
    int refresh_ms;
    //private Thread writer_thread;
    public ByteArrayRecordQueue(int refresh_ms)
    {
        this.refresh_ms=refresh_ms;
        int loop_count=0;
        this.queue = new ByteArrayRecordQueue.Data[READ_BLOCKS];
        this.availability_array = new boolean[READ_BLOCKS];
        // Set all variables in availability_array to true.
        Arrays.fill(availability_array, true);
        // Populate all default values in queue with Data Objects.
        while(loop_count<queue.length)
        {
            // Core process            
            this.queue[loop_count]=new ByteArrayRecordQueue.Data();
            loop_count=loop_count + 1;
        }
//         this.writer_thread=new Thread(file_writer);
//         writer_thread.setName("ROS_BAG_FILE_WRITER_THREAD");
//         writer_thread.start();
    }
    
// Complete    
    /** Add record to queue. */
    public void add(byte[] record)
    {
        if(is_running==false)
        {
            return;
        }
// TODO: Fix loo[_count by performing the index position before the current if else statements to remove nested level of if else in multiple locations here.        
        //System.out.println("record:" + record.length);
        int loop_count=current_position+queue_count;
        if(queue_count==queue.length)
        {//System.out.println("Loop count + 1=" + (loop_count+1) + "current_position="+current_position + "queue_count:" + queue_count);
            // Test availability array for false half way into the array, and if it is false extend the Array length * 2 and populate the new Data elements.
            this.queue_length_divisor=queue.length/2;
            if(availability_array[queue_length_divisor]==false)
            {
                // Set availability_array to false, assign bytes to Data Object, and update counts...
                if(current_position+queue_count>=queue.length)
                {
                    // loop back to 0... Test to validate. May be off by 1.
//                    loop_count=loop_count-queue.length;
//                    availability_array[loop_count]=false;
//                    queue[loop_count].bytes=record;
                    // Double the Data and availability_array length.
                    this.queue=Arrays.copyOf(queue, this.READ_BLOCKS=(queue.length * 2));
                    this.availability_array=Arrays.copyOf(availability_array, READ_BLOCKS);

                    // Populate the default values in new elements.
                    for(int i=queue.length/2;i<queue.length;i++)
                    {
                        queue[i]=new ByteArrayRecordQueue.Data();  
                        availability_array[i]=true;        
                    }     
                    // Populate defaults
                    availability_array[loop_count]=false;
                    queue[loop_count].bytes=record;                    
                }   
                else
                {
                    // Use existing position.
                        availability_array[loop_count=current_position+queue_count]=false;
                        queue[loop_count].bytes=record;
                        // Double the Data and availability_array length.
                        this.queue=Arrays.copyOf(queue, this.READ_BLOCKS=(queue.length * 2));
                        this.availability_array=Arrays.copyOf(availability_array, READ_BLOCKS);
                        // Populate the default values in new elements.
                        for(int i=queue.length/2;i<queue.length;i++)
                        {
                            queue[i]=new ByteArrayRecordQueue.Data();  
                            availability_array[i]=true;        
                        }    
                        // Populate defaults
                        availability_array[loop_count]=false;
                        queue[loop_count].bytes=record;                         
                }
                this.queue_count=queue_count + 1;
            }
            else
            {
                // Set availability_array to false, assign bytes to Data Object, and update counts...            
                if(current_position+queue_count>=queue.length)
                {
                    // loop back to 0... Test to validate. May be off by 1.
                    ;
                    availability_array[loop_count=loop_count-queue.length]=false;
  //  System.out.println("record:" + record.length);
                    queue[loop_count].bytes=record;                 
                    this.queue_count=queue_count + 1;                      
                }
                else
                {
   //                  System.out.println("2loop_count: "+loop_count +", availability_array: " + availability_array.length + ", suggested loop_count: " + (current_position+queue_count));                     
                    availability_array[loop_count=current_position+queue_count]=false;
//    System.out.println("record:" + record.length);
                    queue[loop_count].bytes=record;                 
                    this.queue_count=queue_count + 1;                  
                }
            }
        }
        else
        {
            // Set availability_array to false, assign bytes to Data Object, and update counts...           
          //  System.out.println("availability_array.length:" + availability_array.length);     
    //        availability_array[loop_count]=false;
// TODO: Calculate current_position based on current position, may be somewhere starting after 0; 
            if(current_position+queue_count>=queue.length)
            {   
                availability_array[loop_count=loop_count-queue.length]=false;   
                queue[loop_count].bytes=record;  
            }
            else
            {
                availability_array[loop_count=current_position+queue_count]=false;   
                queue[loop_count].bytes=record;                  
            }
            this.queue_count=queue_count + 1;            
        }
    }

/* Runnable file_writer = n
 * w Runnable() {
        public void run() 
    {
// TODO: Something is up with the THreading... that seems to be the problem.... Thread starts and stops, the other code gets backed up, then on stop recording it all floods through...        
        // set count variable for processing.
       ByteArrayRecordQueue.this.is_running=true;
             System.out.println("STARTING THREAD RUN is_running: " + is_running);       
         while (is_running) 
         {
             try
             {
                // If queue is greater than 0, process each available element.
                    // process queue
                   // while(loop_count<queue.length)
                   //{
                    while(0<queue_count)
                    {

                        if(current_position+1==queue.length)
                        {
                            // reset current_position to 0 after process and substract 1 from queue_count
                            System.out.println("RESET");
                            process(queue[current_position].bytes); 
                            availability_array[current_position]=true;
                            // increment current_position, and decrement the queue_count.
                            ByteArrayRecordQueue.this.current_position=0;
                            ByteArrayRecordQueue.this.queue_count=queue_count - 1;
                        }
                        else
                        {
                            System.out.println("NEXT");
                            // process and substract 1 from queue_count
                            process(queue[current_position].bytes); 
                            availability_array[current_position]=true;
                            // increment current_position, and decrement the queue_count.
                            ByteArrayRecordQueue.this.current_position=current_position+1;
                            ByteArrayRecordQueue.this.queue_count=queue_count - 1;
                        }
                    }
                if(refresh_ms>0)
                {
                    Thread.sleep(5);
                }                
            } 
            catch (InterruptedException ex) 
            {
                // Logger.getLogger(ByteArrayRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
           }
            catch(Exception ex)
            {
               Logger.getLogger(ByteArrayRecordQueue.class.getName()).log(Level.SEVERE, null, ex);           
            }
         }
         System.out.println("EXITING THREAD RUN is_running: " + is_running);
    }      
 };
 
  */  

    @Override
    public void run() 
    {
// TODO: Something is up with the THreading... that seems to be the problem.... Thread starts and stops, the other code gets backed up, then on stop recording it all floods through...        
        // set count variable for processing.
       this.is_running=true;
//             System.out.println("STARTING THREAD RUN is_running: " + is_running);       
         while (is_running) 
         {
             try
             {
                // If queue is greater than 0, process each available element.
                    // process queue
                   // while(loop_count<queue.length)
                   //{
                    while(0<queue_count)
                    {
                        if(is_running==false)
                        {
                            break;
                        }
                        if(current_position+1==queue.length)
                        {
                            // reset current_position to 0 after process and substract 1 from queue_count
                            process(queue[current_position].bytes); 
                            availability_array[current_position]=true;
                            // increment current_position, and decrement the queue_count.
                            this.current_position=0;
                            this.queue_count=queue_count - 1;
                        }
                        else
                        {
                            // process and substract 1 from queue_count
                            process(queue[current_position].bytes); 
                            availability_array[current_position]=true;
                            // increment current_position, and decrement the queue_count.
                            this.current_position=current_position+1;
                            this.queue_count=queue_count - 1;
                        }
                    }
                if(refresh_ms>0)
                {
                    //Thread.sleep(5);
                    Thread.sleep(5);
                }                
            } 
            catch (InterruptedException ex) 
            {
                // Logger.getLogger(ByteArrayRecordQueue.class.getName()).log(Level.SEVERE, null, ex);
            //    ex.printStackTrace();
           }
            catch(Exception ex)
            {
               Logger.getLogger(ByteArrayRecordQueue.class.getName()).log(Level.SEVERE, null, ex);           
            }
//         System.out.println("FINALLY: " + is_running);
         }
 //        System.out.println("EXITING THREAD RUN is_running: " + is_running);
    }    
    
    /** The process method processing the data on thread.*/
    public abstract void process(byte[] bytes);

    /** Stop the Thread. */
    public synchronized void halt()
    {
        this.is_running=false;
        this.interrupt();
    }
    
    public synchronized void recycle()
    {
        while(recycleCount<queue.length)
        {
            queue[recycleCount].recycle();
            availability_array[recycleCount]=true;
            this.recycleCount=recycleCount + 1;
            this.queue_count=0;
            this.current_position=0;
        }
        this.recycleCount=0;
    }
    
    public synchronized void shutdown()
    {
        for(int i=0;i<queue.length;i++)
        {
            queue[i]=null;
        }
    }  
    
    public class Data
    {
        public byte[] bytes=new byte[0];
        public void recycle()
        {
            this.bytes=null;
        }
    }    
}
