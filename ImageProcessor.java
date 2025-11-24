import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ImageProcessor  implements ImageReader, ImageWriter {
    BufferedImage rawimage;

    public ImageProcessor(BufferedImage image) {
        rawimage = image;
    }

    @Override
    public Image ReadImage(String filePath) throws IOException {
        /*
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
        */
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        // 1. Format Kontrolü (P3 veya P6)
        String format = scanner.next();
        if (!format.equals("P3") && !format.equals("P6")) {
            throw new IOException("Desteklenmeyen PPM formatı: " + format);
        }

        // Yorum Satırlarını Atla (Opsiyonel ama iyi bir pratik)
        while (scanner.hasNext("#")) {
            scanner.nextLine();
        }

        // 2. Genişlik ve Yükseklik Oku
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        Image result = new Image(width, height);

        // 3. Maksimum Renk Değerini Oku (Genellikle 255)
        int maxColorValue = scanner.nextInt();

        // 4. BufferedImage Oluştur
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 5. Piksel Verilerini Oku
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // R, G, B değerlerini oku
                int r = scanner.nextInt();
                int g = scanner.nextInt();
                int b = scanner.nextInt();

                Pixel p = new Pixel(r,g,b,x,y);
                result.addPixel(p);

                // RGB değerlerini 0-255 aralığında varsayarak tek bir int'e dönüştür
                // 0xFF000000 | (R << 16) | (G << 8) | B
                int rgb = (r << 16) | (g << 8) | b;

                // Resmi ayarla
                image.setRGB(x, y, rgb);
            }
        }
        return result;
    }

    @Override
    public void WriteImage(Image image) {

    }
}




