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

//rezalet.
//    public void BuildTree (Image image){
//        //image.getPixels();
//
//        QuadTree.Node<Image> curr = new QuadTree.Node<>(image);
//
//        QuadTree.Node<Image> currNW = new QuadTree.Node<>(image.NorthWestSubImage());
//        QuadTree.Node<Image> currNE = new QuadTree.Node<>(image.NorthEastSubImage());
//        QuadTree.Node<Image> currSW = new QuadTree.Node<>(image.SouthWestSubImage());
//        QuadTree.Node<Image> currSE = new QuadTree.Node<>(image.SouthWestSubImage());
//
//
//
//        quadTree.insertRoot(curr);
//
//        quadTree.insert(curr, currNW, 1);
//        quadTree.insert(curr, currNE, 2);
//        quadTree.insert(curr, currSW, 3);
//        quadTree.insert(curr, currSE, 4);
//
//        do{
//            curr = curr.getNorthWest();
//            currNW = new QuadTree.Node<>(currNW.getData().NorthWestSubImage());
//            quadTree.insert(curr, currNW, 1);
//
//        }while(!curr.getNorthWest().getData().isPixel());
//
//        curr = quadTree.getRoot();
//        do{
//            curr = curr.getNorthEast();
//            currNE = new QuadTree.Node<>(currNE.getData().NorthEastSubImage());
//            quadTree.insert(curr, currNE, 2);
//
//        }while(!curr.getNorthEast().getData().isPixel());
//
//        curr = quadTree.getRoot();
//        do{
//            curr = curr.getSouthWest();
//            currSW = new QuadTree.Node<>(currSW.getData().SouthWestSubImage());
//            quadTree.insert(curr, currSW, 3);
//
//        }while(!curr.getSouthWest().getData().isPixel());
//
//        curr = quadTree.getRoot();
//        do{
//            curr = curr.getNorthWest();
//            currSE = new QuadTree.Node<>(currSE.getData().SouthEastSubImage());
//            quadTree.insert(curr, currSE, 4);
//
//        }while(!curr.getSouthEast().getData().isPixel());
//        System.out.println(quadTree.size());
//
//    }
public void BuildTree(Image image) {
    if (image.getWidth() != image.getHeight() || !isPowerOfTwo(image.getWidth())) {
        System.err.println("Hata: Quadtree sadece kare ve 2'nin kuvveti boyutundaki görüntüler için oluşturulabilir.");
        return;
    }

    // Ağacın kökünü, tüm görüntüyü temsil eden düğüm olarak ayarla
    quadTree.insertRoot(BuildTreeRecursive(image));

    System.out.println("Quadtree başarıyla oluşturuldu.");
    System.out.println("Toplam Düğüm Sayısı (Beklenen 349525): " + quadTree.size());
}

    /**
     * Quadtree'nin asıl özyinelemeli inşa mantığını yürüten yardımcı metot.
     * @param subImage Şu anki düğümün temsil ettiği alt görüntü bölgesi.
     * @return Yeni oluşturulan düğüm.
     */
    private QuadTree.Node<Image> BuildTreeRecursive(Image subImage) {
        // Adım 1: Durma Koşulu (Base Case)
        // Eğer alt bölge tek bir pikselden oluşuyorsa (1x1), özyinelemeyi sonlandır.
        if (subImage.isPixel()) {
            // Bu bir yaprak düğümdür, çocukları olmayacak.
            // QuadTree size++ işlemi, QuadTree'deki insertRoot metodu içinde otomatik olarak yapılıyor.
            // Bu durumda size'ı kendimiz artırmalıyız ya da farklı bir insert metodu kullanmalıyız.
            quadTree.size++; // Boyutu manuel artır (En temiz çözüm, QuadTree size() metodunun doğru sonuç vermesi için)
            return new QuadTree.Node<>(subImage);
        }

        // Adım 2: Özyineleme Adımı
        // Yeni bir iç düğüm oluştur (parent)
        QuadTree.Node<Image> parentNode = new QuadTree.Node<>(subImage);
        quadTree.size++; // İç düğüm eklendiği için boyutu artır

        // Alt bölgeleri hesapla
        Image nw = subImage.NorthWestSubImage();
        Image ne = subImage.NorthEastSubImage();
        Image sw = subImage.SouthWestSubImage();
        Image se = subImage.SouthEastSubImage();

        // Çocuk düğümleri özyinelemeli olarak inşa et ve ebeveyne bağla

        // NW: KuzeyBatı
        parentNode.setNorthWest(BuildTreeRecursive(nw));

        // NE: KuzeyDoğu
        parentNode.setNorthEast(BuildTreeRecursive(ne));

        // SW: GüneyBatı
        parentNode.setSouthWest(BuildTreeRecursive(sw));

        // SE: GüneyDoğu
        parentNode.setSouthEast(BuildTreeRecursive(se));

        return parentNode;
    }

    // Gerekli yardımcı metot
    private boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    /**
     * Sıkıştırma kriterine göre Quadtree'yi özyinelemeli olarak inşa eder.
     * @param subImage Şu anki düğümün temsil ettiği alt görüntü bölgesi.
     * @param threshold Sıkıştırma için kullanılan hata eşiği.
     * @return Yeni oluşturulan düğüm.
     */
    private QuadTree.Node<Image> BuildCompressedTreeRecursive(Image subImage, double threshold) {
        // Adım 1: Durma Koşulları (Base Cases)

        // A. Tek piksele ulaşıldı (Quadtree'de daha fazla bölme yapılamaz.)
        if (subImage.isPixel()) {
            quadTree.size++; // Düğüm sayısını artır
            // Düğümün verisi olarak Orijinal Piksel kullanılır.
            return new QuadTree.Node<>(subImage);
        }

        // B. Ortalama Hata Eşiğin Altında (Sıkıştırma Kriteri)

        // 1. Ortalama Rengi Hesapla (C_i)
        Pixel meanColor = subImage.calculateMeanColor();
        // 2. Ortalama Karesel Hatayı Hesapla (E_i)
        double error = subImage.calculateMeanSquaredError(meanColor);

        // Eğer hata eşiğin altındaysa, bölmeyi durdur.
        if (error <= threshold) {
            quadTree.size++; // Düğüm sayısını artır

            // Bu, sıkıştırılmış görüntünün rengini saklayacak yaprak düğümdür.
            // Ödev yönergesine göre düğüm, alt bölgenin ortalama rengini saklamalıdır. [cite: 37, 46]
            Image compressedImage = new Image(subImage.getWidth(), subImage.getHeight());
            compressedImage.fillWithColor(meanColor); // Eğer Image sınıfınızda böyle bir metot yoksa Image nesnesini meanColor ile oluşturun.

            return new QuadTree.Node<>(compressedImage);
        }

        // Adım 2: Özyineleme Adımı (Hata Eşiğin Üzerinde)

        // Yeni bir iç düğüm oluştur (parent)
        QuadTree.Node<Image> parentNode = new QuadTree.Node<>(subImage); // Buraya meanColor'u da atayabilirsiniz.
        quadTree.size++;

        // Çocuk düğümleri özyinelemeli olarak inşa et ve ebeveyne bağla
        parentNode.setNorthWest(BuildCompressedTreeRecursive(subImage.NorthWestSubImage(), threshold));
        parentNode.setNorthEast(BuildCompressedTreeRecursive(subImage.NorthEastSubImage(), threshold));
        parentNode.setSouthWest(BuildCompressedTreeRecursive(subImage.SouthWestSubImage(), threshold));
        parentNode.setSouthEast(BuildCompressedTreeRecursive(subImage.SouthEastSubImage(), threshold));

        return parentNode;
    }


    // Ana Build metodu, komut satırı ile çağrılacak.
    public void BuildCompressedTree(Image image, double threshold) {
        if (image.getWidth() != image.getHeight() || !isPowerOfTwo(image.getWidth())) {
            System.err.println("Hata: Quadtree sadece kare ve 2'nin kuvveti boyutundaki görüntüler için oluşturulabilir.");
            return;
        }

        // Önceki sıkıştırma ağacının boyutunu sıfırla
        quadTree.size = 0;

        // Ağacın kökünü ayarla
        quadTree.insertRoot(BuildCompressedTreeRecursive(image, threshold));

        // quadTree.insertRoot() metodu da size'ı 1 artırdığı için,
        // root node için fazladan bir size artışı olabilir.
        // Bu durumu QuadTree.java'daki insertRoot metodu kontrol ederek çözebilirsiniz.
    }





}




