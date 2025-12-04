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
    public void WriteImage(Image image, String path) {
        File file = new  File(path);
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
    public boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    /**
     * Sıkıştırma kriterine göre Quadtree'yi özyinelemeli olarak inşa eder.
     anki düğümün temsil ettiği alt görüntü bölgesi.
     * @param threshold Sıkıştırma için kullanılan hata eşiği.
     * @return Yeni oluşturulan düğüm.
     */
    public QuadTree.Node<Image> BuildCompressedTreeRecursive(Image subImage, double threshold) {
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
            WriteImage(reconstructedImage, outputFileName); // WriteImage metodu dosya yolunu (outputImagePath) kullanmalı.
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

    /**
     * Verilen Quadtree'yi dolaşarak her yaprak düğümün sınırlarını çıktı görüntüsünün
     * üzerine çizer (Quadtree çerçevesi - '-t' bayrağı).
     * * @param outputImage Çizgilerin üzerine çizileceği Image nesnesi.
     * @param rootNode Quadtree'nin kök düğümü.
     */
    public void DrawQuadtreeOutline(Image outputImage, QuadTree.Node<Image> rootNode) {
        Pixel outlineColor = new Pixel(0, 0, 0); // Siyah renk
        DrawOutlineRecursive(outputImage, rootNode, 0, 0, outputImage.getWidth(), outlineColor);
    }

    /**
     * Çerçeveleme işleminin özyinelemeli yardımcı metodu.
     */
    private void DrawOutlineRecursive(Image outputImage, QuadTree.Node<Image> currentNode, int startX, int startY, int size, Pixel color) {
        if (currentNode == null) {
            return;
        }

        // A. Durma Koşulu: Yaprak Düğüm (Split burada durmuştur)
        if (currentNode.isLeaf()) {
            // Yaprak düğümün sınırlarını çiz

            // Üst yatay çizgi (startX'tan startX+size'a)
            for (int x = startX; x < startX + size; x++) {
                // outputImage.addPixel(color, x, startY); // Sadece üst çizgi
                outputImage.addPixel(color, x, startY);
                if (startY + size - 1 < outputImage.getHeight()) {
                    outputImage.addPixel(color, x, startY + size - 1); // Alt çizgi
                }
            }

            // Sol dikey çizgi (startY'den startY+size'a)
            for (int y = startY; y < startY + size; y++) {
                // outputImage.addPixel(color, startX, y); // Sadece sol çizgi
                outputImage.addPixel(color, startX, y);
                if (startX + size - 1 < outputImage.getWidth()) {
                    outputImage.addPixel(color, startX + size - 1, y); // Sağ çizgi
                }
            }
            return;
        }

        // B. Özyineleme Adımı: İç Düğüm (Bölmeye devam et)
        int halfSize = size / 2;

        // NW (Kuzey-Batı)
        DrawOutlineRecursive(outputImage, currentNode.getNorthWest(), startX, startY, halfSize, color);

        // NE (Kuzey-Doğu)
        DrawOutlineRecursive(outputImage, currentNode.getNorthEast(), startX + halfSize, startY, halfSize, color);

        // SW (Güney-Batı)
        DrawOutlineRecursive(outputImage, currentNode.getSouthWest(), startX, startY + halfSize, halfSize, color);

        // SE (Güney-Doğu)
        DrawOutlineRecursive(outputImage, currentNode.getSouthEast(), startX + halfSize, startY + halfSize, halfSize, color);
    }

    /**
     * 3x3 Edge Detection (Laplace) filtresini verilen bölgenin merkez pikseline uygular.
     * @param image Bölgeyi içeren Image nesnesi.
     * @param x Merkez pikselin X koordinatı.
     * @param y Merkez pikselin Y koordinatı.
     * @return Yeni, filtrelenmiş Pixel (RGB değerleri 0-255 arasına sıkıştırılmış).
     */
    private Pixel applyEdgeDetectionFilter(Image image, int x, int y) {
        // 3x3 Laplace Kenar Tespiti Çekirdeği
        int[] kernel = {
                -1, -1, -1,
                -1, 8, -1,
                -1, -1, -1
        };

        long newR = 0;
        long newG = 0;
        long newB = 0;
        int k = 0;

        // 3x3 komşuluğu dolaş
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int currentX = x + dx;
                int currentY = y + dy;

                // Sınır kontrolü: Görüntü sınırlarının dışındaysa, merkezi pikseli kullan (genellikle 0 veya merkez piksel kullanılır)
                // Basitlik için, sınır dışındaki pikselleri 0 (siyah) kabul edebiliriz
                // Veya ödevde belirtildiği gibi sadece filtrelenecek alanı kullanırız (daha sonra ele alınacak).
                if (currentX >= 0 && currentX < image.getWidth() && currentY >= 0 && currentY < image.getHeight()) {
                    Pixel p = image.getPixels()[currentX][currentY];
                    int weight = kernel[k];

                    newR += p.getRed() * weight;
                    newG += p.getGreen() * weight;
                    newB += p.getBlue() * weight;
                }
                k++;
            }
        }

        // Sonucu 0-255 arasına sıkıştır (Clamping)


        final double GAIN = 2.0; // Deneme amaçlı 2 katına çıkarın (Görüntüye göre ayarlanabilir)

        newR *= GAIN;
        newG *= GAIN;
        newB *= GAIN;

        // Sonucu 0-255 arasına sıkıştır (Clamping)
        int finalR = (int) Math.min(255, Math.max(0, newR));
        int finalG = (int) Math.min(255, Math.max(0, newG));
        int finalB = (int) Math.min(255, Math.max(0, newB));
        // ... (finalG ve finalB için aynı clamping işlemi)

        return new Pixel(finalR, finalG, finalB);

    }

    /**
     * Kenar tespiti filtresini Quadtree yapısına göre uygular.
     * Sadece yeterince küçük düğümlere (örneğin size <= 8) filtre uygular, diğerlerini siyah yapar.
     * * @param image Orijinal görüntü.
     * @param rootNode Kenar tespiti için kullanılan Quadtree'nin kökü (sıkıştırılmış ağaç).
     * @param minSize Filtrenin uygulanacağı en küçük bölge boyutu (örneğin 8).
     * @return Kenar tespiti uygulanmış Image nesnesi.
     */
    public Image ApplyEdgeDetection(Image originalImage, QuadTree.Node<Image> rootNode, int minSize) {
        Image resultImage = new Image(originalImage.getWidth(), originalImage.getHeight());
        ApplyEdgeDetectionRecursive(originalImage, resultImage, rootNode, 0, 0, originalImage.getWidth(), minSize);
        return resultImage;
    }

    private void ApplyEdgeDetectionRecursive(Image originalImage, Image outputImage, QuadTree.Node<Image> currentNode, int startX, int startY, int size, int minSize) {
        if (currentNode == null) {
            return;
        }

        if (currentNode.isLeaf() || size <= minSize) {
            // Yaprak düğümse veya boyutu filtrelenecek kadar küçükse (kriteri minSize):

            if (size <= minSize) {
                // Filtreyi bu bölgedeki piksellere uygula
                for (int y = startY; y < startY + size; y++) {
                    for (int x = startX; x < startX + size; x++) {

                        // Not: 3x3 filtre, sınır bölgelerde (kenarlarda) sorun çıkarır.
                        // Sınır pikselleri için basitlik adına kopyalama yapılabilir.
                        if (x > 0 && x < originalImage.getWidth() - 1 && y > 0 && y < originalImage.getHeight() - 1) {
                            Pixel filteredPixel = applyEdgeDetectionFilter(originalImage, x, y);
                            outputImage.addPixel(filteredPixel, x, y);
                        } else {
                            // Sınır piksellerini siyah yap
                            outputImage.addPixel(new Pixel(0, 0, 0), x, y);
                        }
                    }
                }
            } else {
                // Yaprak düğüm, ancak minSize'dan büyük (Threshold nedeniyle bölme durdu) -> Siyah yap
                Pixel black = new Pixel(0, 0, 0);
                for (int y = startY; y < startY + size; y++) {
                    for (int x = startX; x < startX + size; x++) {
                        outputImage.addPixel(black, x, y);
                    }
                }
            }
            return;
        }

        // İç düğümse, bölmeye devam et
        int halfSize = size / 2;
        ApplyEdgeDetectionRecursive(originalImage, outputImage, currentNode.getNorthWest(), startX, startY, halfSize, minSize);
        ApplyEdgeDetectionRecursive(originalImage, outputImage, currentNode.getNorthEast(), startX + halfSize, startY, halfSize, minSize);
        ApplyEdgeDetectionRecursive(originalImage, outputImage, currentNode.getSouthWest(), startX, startY + halfSize, halfSize, minSize);
        ApplyEdgeDetectionRecursive(originalImage, outputImage, currentNode.getSouthEast(), startX + halfSize, startY + halfSize, halfSize, minSize);
    }



}




