import java.awt.image.BufferedImage;

public class ImageProcessor extends BufferedImage implements Readable, ImageWriter {
    BufferedImage rawimage;

    public ImageProcessor(BufferedImage image) {
        super(image.getWidth(), image.getHeight(), image.getType());
        rawimage = image;
    }

    @Override
    public void ReadImage(Image image) {
        for(int y = 0; y < rawimage.getHeight(); y++) {
            for(int x = 0; x < rawimage.getWidth(); x++) {
                int pixel = rawimage.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                Pixel p = new Pixel(red, green, blue, x, y);
                image.setPixel(x, y, p);
            }
        }
    }
    @Override
    public void WriteImage(Image image) {

    }



}
