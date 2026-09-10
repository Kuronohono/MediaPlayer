package org.example.mediaplayer;

import javafx.scene.image.Image;
import org.bytedeco.javacv.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import java.io.*;
import java.util.Arrays;

public class Video implements MediaItem {

    private File file;
    private String title;
    private String format;
    private String frameRate;
    private String totalFrames;
    private String bitRates;
    private String audioChannels;
    private String videoCodec;
    private String audioCodec;
    private String sampleRate;
    private String resolution;
    private String duration;
    private Image image;
    private final MediaType mediaType = MediaType.Video;

    public Video(File file) {
        this.file = file;
        try {
            extractName();
            getInfo();
            extractThumbnail();
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
    }

    private void getInfo(){
        FFmpegFrameGrabber grabber = null;

        try {
            grabber = new FFmpegFrameGrabber(file.getPath());
            grabber.start();

            calculateTrackLength((int) grabber.getLengthInTime() / 1000000);
            format = grabber.getFormat();
            frameRate = ((int) grabber.getFrameRate()) + "FPS";
            resolution = grabber.getImageWidth() + "x" + grabber.getImageWidth();
            totalFrames = Integer.toString(grabber.getLengthInFrames());
            bitRates = (grabber.getVideoBitrate()/1000000)+"Mbps";
            audioChannels = Integer.toString(grabber.getAudioChannels());
            videoCodec = String.valueOf(grabber.getVideoCodec());
            audioCodec = Integer.toString(grabber.getAudioCodec());
            sampleRate = Integer.toString(grabber.getSampleRate());

            grabber.stop();
            grabber.release();
            grabber.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            if(grabber!=null)
            {
                try{
                    grabber.stop();
                    grabber.release();
                    grabber.close();
                }catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void extractName() {
        String name = file.getName();
        name = name.replace(".mp4", "");
        name = name.replace(".mov", "");
        name = name.replace(".avi", "");
        this.title = name;
    }

    private void extractThumbnail() {
        FFmpegFrameGrabber grabber = null;

        try {
            grabber = new FFmpegFrameGrabber(file.getPath());
            grabber.start();

            int totalFrames = grabber.getLengthInFrames();
            int middleFrame = totalFrames / 2;
            grabber.setFrameNumber(middleFrame);

            //Grab the frame
            Frame frame = grabber.grabImage();
            System.out.println(Arrays.toString(frame.image));

            //Convert to Buffered Image and save
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.convert(frame);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            image = new Image(bais);
            grabber.stop();
            grabber.release();

            converter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {

            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        }
    }

    private void calculateTrackLength(int trackLength) {
        int s = trackLength % 60; //Calculate the seconds
        int H = trackLength / 60; //Convert total seconds to minutes
        int M = H % 60; //Calculate the remaining minutes
        H = H / 60; //Convert total minutes to hours

        if (H > 0) {
            duration = String.format("%d:%02d:%02d", H, M, s);
        } else {
            duration = String.format("%02d:%02d", M, s);
        }
    }

    public String[][] getInformation()
    {
        String[][] info = {
                {"Title",title},
                {"Format",format},
                {"FrameRate",frameRate},
                {"Total Frames",totalFrames},
                {"Resolution",resolution},
                {"Duration",duration},
                {"BitRates",bitRates},
                {"Audio Channels",audioChannels},
                {"VideoCodec",videoCodec},
                {"AudioCodec",audioCodec},
                {"SampleRate",sampleRate},
        };
        return info;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
    }

    public String getTotalFrames() {
        return totalFrames;
    }

    public void setTotalFrames(String totalFrames) {
        this.totalFrames = totalFrames;
    }

    public String getBitRates() {
        return bitRates;
    }

    public void setBitRates(String bitRates) {
        this.bitRates = bitRates;
    }

    public String getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(String audioChannels) {
        this.audioChannels = audioChannels;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    public String getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(String sampleRate) {
        this.sampleRate = sampleRate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public MediaType getType() {
        return mediaType;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Video{" +
                "file=" + file +
                ", title='" + title + '\'' +
                ", format='" + format + '\'' +
                ", frameRate='" + frameRate + '\'' +
                ", resolution='" + resolution + '\'' +
                ", duration='" + duration + '\'' +
                ", image=" + image +
                '}';
    }
}

