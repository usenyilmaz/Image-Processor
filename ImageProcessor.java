import java.io.*;
import java.util.Scanner;

public class ImageProcessor  implements ImageReader, ImageWriter {
    String inputImagePath;
    String outputImagePath;
    QuadTree<Image> quadTree;

    public ImageProcessor(String imagePath, String outputImagePath) {
        this.inputImagePath = imagePath;
        this.outputImagePath = outputImagePath;
        quadTree = new QuadTree<>();
    }

    @Override
    public Image ReadImage() throws IOException {
        FileInputStream fis = null;
        Scanner scanner = null;

        File file = new File(inputImagePath);
        try{
            fis = new FileInputStream(file);
            scanner = new Scanner(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 1. Format Kontrolü (P3 veya P6)
        String format = scanner.nextLine();
        if (!format.equals("P3") && !format.equals("P6")) {
            throw new IOException("Desteklenmeyen PPM formatı: " + format);
        }


        // 2. Genişlik ve Yükseklik Oku
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        scanner.nextInt();


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

//                System.out.println("pixel: " + (y * width + x));
//                System.out.println(r + "," + g + "," + b);

                Pixel p = new Pixel(r,g,b,x,y);
                result.addPixel(p);

            }
        }
        return result;
    }

    @Override
    public void WriteImage(Image image) {
        File file = new  File(outputImagePath);
        try{
            PrintWriter writer = new PrintWriter(new FileWriter(file));

            writer.println("P3");
            writer.println(image.getWidth() + " " + image.getHeight());
            writer.println("255");

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Pixel p = image.getPixels()[x][y];

                    // Hata: Eğer piksel null ise, doğrudan bir hata fırlatmak daha tutarlı olabilir.
                    if (p == null) {
                        throw new IllegalStateException("Piksel matrisi tam olarak doldurulmamış: (" + x + "," + y + ")");
                    }

                    writer.println(p.getRed() + " " + p.getGreen() + " " + p.getBlue());
                }
            }
        }

        catch (IOException e){
            System.out.println("Error writing to file: WriteImage method");
            e.printStackTrace();
        }


    }
}




