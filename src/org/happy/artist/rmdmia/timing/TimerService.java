package org.happy.artist.rmdmia.timing;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** TimerService.java is a Timer Service to be used by configurable RMDMIA, that will keep time updates without overloading system with calls to System.currentTimeMillis(). 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013 Happy Artist. All rights reserved.
 */
public class TimerService 
{
    /** System time variable SYSTEM_TIME updated at least once per ms. */
    public static transient long SYSTEM_TIME;
    //
    private final ScheduledExecutorService scheduler;
    final Runnable timer = new Runnable() 
    {
        public void run() 
        { 
            TimerService.SYSTEM_TIME=System.currentTimeMillis(); 
           // System.out.println(TimerService.SYSTEM_TIME);
        }
    };
    final ScheduledFuture<?> timerHandle;
    
    /**
     *
     * @param startDelay
     * @param delay
     * @param unit
     */
    public TimerService(long startDelay,long delay,TimeUnit unit)
    {
        this.scheduler=Executors.newScheduledThreadPool(1);
        this.timerHandle = scheduler.scheduleAtFixedRate(timer, startDelay, delay, unit);
    }
    
    /** shut down this timer service. */
    public void shutdown()
    {
        timerHandle.cancel(true);
    }
    
    // Unit Test
    /**
     *
     * @param args
     */
    public static void main(String[] args)
    {
        // Update System Time every millasecond.
        new TimerService(1,1,TimeUnit.MILLISECONDS);
    }
}
