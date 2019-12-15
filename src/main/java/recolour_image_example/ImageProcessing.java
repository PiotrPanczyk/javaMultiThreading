package recolour_image_example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {
    public static final String sourceFile = "./resources/many-flowers.jpg";
    public static final String destinationFile = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage origImage = ImageIO.read(new File(sourceFile));
        BufferedImage resultImage = new BufferedImage(origImage.getWidth(),
                origImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();

        //recolorSingleThreaded(origImage, resultImage);
        int numberOfThreads = 4;
        recolorMultiThreaded(origImage, resultImage, numberOfThreads);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        File outptImage = new File(destinationFile);
        ImageIO.write(resultImage, "jpg", outptImage);

        System.out.println(String.valueOf(duration));

    }

    public static void recolorMultiThreaded(BufferedImage origImage,
                                            BufferedImage resultImage,
                                            int numberOfThreads){
        System.out.println("Running multithreaded using "+ numberOfThreads + " threads.");
        List<Thread> threads = new ArrayList<>();
        int width = origImage.getWidth();
        int height = origImage.getHeight() / numberOfThreads;

        for(int i = 0; i < numberOfThreads; i++){
            final int threadMultiplier = i;

            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;

                recolorImage(origImage, resultImage,
                        leftCorner, topCorner, width, height);
            });

            threads.add(thread);
        }

        for(Thread t : threads){
            t.start();
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void recolorSingleThreaded(BufferedImage origImage,
                                             BufferedImage resultImage){
        recolorImage(origImage, resultImage, 0, 0,
        origImage.getWidth(), origImage.getHeight());
    }

    public static void recolorImage(BufferedImage origImage,
                                    BufferedImage resultImage,
                                    int leftCorner, int topCorner,
                                    int width, int height){
        for(int x =leftCorner; x < leftCorner+width; x++){
            for(int y=topCorner; y < topCorner+height; y++){
                recolorPixel(origImage, resultImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage origImage,
                               BufferedImage resultImage, int x, int y){
        int rgb = origImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if(isShadeOfGray(red, green, blue)){
            newRed = Math.min(255, red+10);
            newGreen = Math.max(0, green-80);
            newBlue = Math.max(0, blue-20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRgb = createRGBFromColors(newRed, newGreen, newBlue);
        setRgb(resultImage, x, y, newRgb);
    }


    public static void setRgb(BufferedImage image, int x, int y, int rgb){
        image.getRaster().setDataElements(x, y,
                image.getColorModel().getDataElements(rgb, null));
    }

    public static boolean isShadeOfGray(int red, int green, int blue){
        return Math.abs(red - green) < 30 && Math.abs(red -blue) < 30 &&
                Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue){
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb){
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb){
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb){
        return (rgb & 0x000000FF);
    }
}
