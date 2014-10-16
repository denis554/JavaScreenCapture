package load;

import capture.multi.raw.RawFrame;
import encode.ToMov;
import save.MP4Encoder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by denislavrov on 10/11/14.
 */
public class LoadTest {
    public static void main(String[] args) throws IOException {
        DiskReader<RawFrame> loader = new DiskReader<>(new File("save.bin"));
        ToMov encode = new ToMov(loader, new File("test1.mp4"));

        /*

        MP4Encoder se = new MP4Encoder(new File("test.mp4"), new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        long sTime = System.nanoTime();
        loader.getStore().stream().sorted((c1,c2) -> c1.getFrameTime() < c2.getFrameTime() ? -1 : 1).forEach(cf ->{
            try{
                se.encodeNativeFrame(cf);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        System.out.println("Saving took: " + (System.nanoTime()-sTime)/1000_000L + "ms");
        se.finish();
        */
    }
}
