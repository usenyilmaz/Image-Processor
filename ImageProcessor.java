import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ImageProcessor  implements ImageReader, ImageWriter {
    String imagePath;

    public ImageProcessor(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public Image ReadImage() throws IOException {
        FileInputStream fis = null;
        Scanner scanner = null;

        File file = new File(imagePath);
        try{
            fis = new FileInputStream(file);
            scanner = new Scanner(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 1. Format Kontrolü (P3 veya P6)
        String format = scanner.next();
        if (!format.equals("P3") && !format.equals("P6")) {
            throw new IOException("Desteklenmeyen PPM formatı: " + format);
        }


        // 2. Genişlik ve Yükseklik Oku
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        if(width != height){
            throw new ImageIsNotSquareShaped("Image is not square shaped");
        }

        Image result = new Image(width, height);


        // 5. Piksel Verilerini Oku
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // R, G, B değerlerini oku
                int r = scanner.nextInt();
                int g = scanner.nextInt();
                int b = scanner.nextInt();

                System.out.println("pixel: " + (x + y));
                System.out.println(r + "," + g + "," + b);

                Pixel p = new Pixel(r,g,b,x,y);
                result.addPixel(p);

            }
        }
        return result;
    }

    @Override
    public void WriteImage(Image image) {

    }
}




