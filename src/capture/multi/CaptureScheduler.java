package capture.multi;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by denislavrov on 10/2/14.
 */
public class CaptureScheduler {
    private int THREADS = 6;
    private int FPS_PER_THREAD = 6;
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(THREADS);
    private Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    private ConcurrentLinkedQueue<BufferedImage> store = new ConcurrentLinkedQueue<>();
    final static long toNanos = 1000_000_000L;

    public CaptureScheduler(int THREADS, Rectangle captureSize) {
        this.THREADS = THREADS;
        this.captureSize = captureSize;
    }

    public CaptureScheduler(int THREADS, int FPS) {
        this.THREADS = THREADS;
        FPS_PER_THREAD = (int)((double) FPS / (double) THREADS);
    }

    public CaptureScheduler() {
    }

    private static long fractionToNanos(long a, long b){
        return a * toNanos / b;
    }

    public void init() throws Exception {
        for (int i = 0; i < THREADS; i++) {
            scheduledThreadPool.scheduleAtFixedRate(
                    new CaptureWorker(new Robot(),captureSize, store),
                    fractionToNanos(i,THREADS),
                    fractionToNanos(1,FPS_PER_THREAD),
                    TimeUnit.NANOSECONDS
                    );
        }
    }

    public void stop(){
        scheduledThreadPool.shutdown();
    }

    public ConcurrentLinkedQueue<BufferedImage> getStore(){
        return store;
    }

}
