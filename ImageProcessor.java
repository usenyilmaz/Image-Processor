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
    /**
     * Belirtilen 8 farklı sıkıştırma seviyesine (compression level) ulaşmak için
     * Quadtree'yi inşa eder ve sıkıştırılmış görüntüleri diske yazar.
     * Eşik değeri, iteratif bir arama (binary search benzeri) ile bulunur.
     */
    public void GenerateCompressedImages(Image originalImage, String outputFileNameBase) {
        // Ödevde belirtilen hedef sıkıştırma seviyeleri
        final double[] TARGET_LEVELS = {0.002, 0.004, 0.01, 0.033, 0.077, 0.2, 0.5, 0.65};
        final int TOTAL_PIXELS = originalImage.getWidth() * originalImage.getHeight();
        final int MAX_ITERATIONS = 20; // Eşiği bulmak için maksimum arama adımı
        final double TOLERANCE = 0.0001; // Hedefe yakın sayılması için tolerans

        // QuadTree'nin boyutunu (size) hesaplamak için orijinal düğüm sayısını sıfırla.
        // NOTE: QuadTree.java'daki size değişkeni public/protected olmalı veya incrementSize() metodu kullanılmalı.
        // Eğer size değişkenine doğrudan erişiminiz yoksa, bu kısım hata verecektir.

        // Eşik değerinin alabileceği aralığı tanımla:
        // Minimum hata 0 (siyah/beyaz bölgeler), maksimum ~195075 (3 * 255^2)
        double globalMinThreshold = 0;
        double globalMaxThreshold = 100000; // Geniş bir üst limit

        System.out.println("--- Görüntü Sıkıştırma Başladı ---");

        for (int i = 0; i < TARGET_LEVELS.length; i++) {
            double targetLevel = TARGET_LEVELS[i];

            // Her hedef için arama aralığını sıfırla
            double minT = globalMinThreshold;
            double maxT = globalMaxThreshold;
            double currentThreshold = (minT + maxT) / 2;

            System.out.printf("\n[Hedef %d] Aranıyor: %.4f\n", (i + 1), targetLevel);

            for (int j = 0; j < MAX_ITERATIONS; j++) {
                // Ağacı inşa et ve size'ı sıfırla. QuadTree'nizin size'ını sıfırlama metodu yoksa manuel sıfırlayın.
                quadTree.size = 0; // Eğer QuadTree.size public ise

                quadTree.setRoot(BuildCompressedTreeRecursive(originalImage, currentThreshold));

                double currentLevel = (double) quadTree.size() / TOTAL_PIXELS;

                // Eğer tolerans içindeyseniz, başarılı
                if (Math.abs(currentLevel - targetLevel) <= TOLERANCE) {
                    break;
                }

                // İkili Arama Mantığı:
                if (currentLevel < targetLevel) {
                    // Yaprak sayısı az (çok sıkışmış). Daha az sıkıştırma için eşiği DÜŞÜR.
                    maxT = currentThreshold;
                } else {
                    // Yaprak sayısı fazla (yeterince sıkışmamış). Daha fazla sıkıştırma için eşiği YÜKSELT.
                    minT = currentThreshold;
                }
                // Yeni eşik tahmini
                currentThreshold = (minT + maxT) / 2;
            }

            // --- Görüntü Geri Oluşturma ve Çıktı ---
            Image reconstructedImage = ReconstructImageFromQuadtree(originalImage.getWidth(), originalImage.getHeight(), quadTree.getRoot());

            String outputFileName = outputFileNameBase + "-" + (i + 1) + ".ppm";
            WriteImage(reconstructedImage); // WriteImage metodu dosya yolunu (outputImagePath) kullanmalı.
            // Veya WriteImage(Image, String path) olarak güncelleyin.

            System.out.printf("   -> Bitti. Çıktı: %s\n", outputFileName);
            System.out.printf("      - Yaprak Sayısı: %d\n", quadTree.size());
            System.out.printf("      - Toplam Piksel: %d\n", TOTAL_PIXELS);
            System.out.printf("      - Elde Edilen Seviye: %.4f\n", ((double)quadTree.size() / TOTAL_PIXELS));
        }
    }

    /**
     * Quadtree'den sıkıştırılmış görüntüyü oluşturur.
     * Ağaçtaki yaprak düğümlerin (compressedImage) verilerini çıktı Image nesnesine yazar.
     */
    public Image ReconstructImageFromQuadtree(int width, int height, QuadTree.Node<Image> rootNode) {
        // Çıktı görüntüsünü (boş bir matris) oluştur
        Image reconstructedImage = new Image(width, height);

        // Geri oluşturmayı başlat (başlangıç koordinatları: x=0, y=0, boyut: width)
        ReconstructRecursive(reconstructedImage, rootNode, 0, 0, width);

        return reconstructedImage;
    }

    /**
     * Görüntü rekonstrüksiyonunun özyinelemeli yardımcı metodu.
     * @param outputImage Piksellerin yazılacağı çıktı Image nesnesi.
     * @param currentNode Şu anki Quadtree düğümü.
     * @param startX Current bölgenin başlangıç X koordinatı.
     * @param startY Current bölgenin başlangıç Y koordinatı.
     * @param size Current bölgenin boyutu (genişlik/yükseklik).
     */
    private void ReconstructRecursive(Image outputImage, QuadTree.Node<Image> currentNode, int startX, int startY, int size) {
        if (currentNode == null) {
            return;
        }

        // Eğer yaprak düğümse (sıkıştırma burada durmuşsa):
        if (currentNode.isLeaf()) {
            Image data = currentNode.getData();
            Pixel meanColor = data.getPixels()[0][0]; // Sıkıştırılmış yaprakta tüm pikseller aynı (meanColor)

            // Yaprak düğümün kapsadığı bölgeyi ortalama renk ile doldur.
            for (int y = startY; y < startY + size; y++) {
                for (int x = startX; x < startX + size; x++) {
                    outputImage.addPixel(meanColor, x, y);
                }
            }
            return;
        }

        // Eğer iç düğümse, 4 çocuğu özyinelemeli olarak dolaş
        int halfSize = size / 2;

        // NW (Kuzey-Batı)
        ReconstructRecursive(outputImage, currentNode.getNorthWest(), startX, startY, halfSize);

        // NE (Kuzey-Doğu)
        ReconstructRecursive(outputImage, currentNode.getNorthEast(), startX + halfSize, startY, halfSize);

        // SW (Güney-Batı)
        ReconstructRecursive(outputImage, currentNode.getSouthWest(), startX, startY + halfSize, halfSize);

        // SE (Güney-Doğu)
        ReconstructRecursive(outputImage, currentNode.getSouthEast(), startX + halfSize, startY + halfSize, halfSize);
    }





}




