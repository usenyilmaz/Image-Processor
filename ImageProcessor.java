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

                Pixel p = new Pixel(r,g,b);
                result.addPixel(p, x, y);

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


    public void BuildTree (Image image){
        //image.getPixels();

        QuadTree.Node<Image> curr = new QuadTree.Node<>(image);

        QuadTree.Node<Image> currNW = new QuadTree.Node<>(image.NorthWestSubImage());
        QuadTree.Node<Image> currNE = new QuadTree.Node<>(image.NorthEastSubImage());
        QuadTree.Node<Image> currSW = new QuadTree.Node<>(image.SouthWestSubImage());
        QuadTree.Node<Image> currSE = new QuadTree.Node<>(image.SouthWestSubImage());



        quadTree.insertRoot(curr);

        quadTree.insert(curr, currNW, 1);
        quadTree.insert(curr, currNE, 2);
        quadTree.insert(curr, currSW, 3);
        quadTree.insert(curr, currSE, 4);

        do{
            curr = curr.getNorthWest();
            currNW = new QuadTree.Node<>(currNW.getData().NorthWestSubImage());
            quadTree.insert(curr, currNW, 1);

        }while(!curr.getNorthWest().getData().isPixel());

        curr = quadTree.getRoot();
        do{
            curr = curr.getNorthEast();
            currNE = new QuadTree.Node<>(currNE.getData().NorthEastSubImage());
            quadTree.insert(curr, currNE, 2);

        }while(!curr.getNorthEast().getData().isPixel());

        curr = quadTree.getRoot();
        do{
            curr = curr.getSouthWest();
            currSW = new QuadTree.Node<>(currSW.getData().SouthWestSubImage());
            quadTree.insert(curr, currSW, 3);

        }while(!curr.getSouthWest().getData().isPixel());

        curr = quadTree.getRoot();
        do{
            curr = curr.getNorthWest();
            currSE = new QuadTree.Node<>(currSE.getData().SouthEastSubImage());
            quadTree.insert(curr, currSE, 4);

        }while(!curr.getSouthEast().getData().isPixel());
        System.out.println(quadTree.size());

    }




}




