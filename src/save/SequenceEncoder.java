package save;

import capture.multi.test;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 *
 * @author The JCodec project
 *
 */
public class SequenceEncoder {
    private SeekableByteChannel ch;
    private Picture toEncode;
    private Transform transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;
    private Rectangle dimensions;

    public SequenceEncoder(File out, Rectangle dimensions) throws IOException {
        this.dimensions = dimensions;
        this.ch = NIOUtils.writableFileChannel(out);

        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(ch, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

        // Allocate a buffer big enough to hold output frames
        _out = ByteBuffer.allocate(dimensions.width * dimensions.height * 6);

        // Create an instance of encoder
        encoder = new H264Encoder();

        // Transform to convert between RGB and YUV
        transform = ColorUtil.getTransform(ColorSpace.RGB, ColorSpace.YUV420);


        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        spsList = new ArrayList<ByteBuffer>();
        ppsList = new ArrayList<ByteBuffer>();
    }

    public void encodeNativeFrame(BufferedImage pic) throws IOException {
        if (toEncode == null) {
            toEncode = Picture.create(pic.getWidth(), pic.getHeight(), ColorSpace.YUV420);
        }


        for (int i = 0; i < 3; i++)
            Arrays.fill(toEncode.getData()[i], 0);
        transform.transform(AWTUtil.fromBufferedImage(pic), toEncode);

        //transform.transform(new Picture());


        // Encode image into H.264 frame, the result is stored in '_out' buffer
        _out.clear();
        ByteBuffer result = encoder.encodeFrame( _out, toEncode);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.encodeMOVPacket(result,spsList,ppsList);

        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, frameNo, 25, 1, frameNo, true, null, frameNo, 0));

        frameNo++;
    }

    public void encodeNativeFrame(int[] pic) throws IOException {
        if (toEncode == null) {
            toEncode = Picture.create(dimensions.width,dimensions.height, ColorSpace.YUV420);
        }
        int[][] picData = new int[1][];
        picData[0] = pic;
        Picture old = test.fromBufferedImage(pic, dimensions);

        for (int i = 0; i < 3; i++)
            Arrays.fill(toEncode.getData()[i], 0);
        transform.transform(old, toEncode);

        // Encode image into H.264 frame, the result is stored in '_out' buffer
        _out.clear();
        ByteBuffer result = encoder.encodeFrame( _out, toEncode);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.encodeMOVPacket(result,spsList,ppsList);

        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, frameNo, 25, 1, frameNo, true, null, frameNo, 0));

        frameNo++;
    }



    public void finish() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(ch);
    }
}