import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // --- 1. Argümanları Ayrıştırma ---
        boolean useCompression = false; // -c
        boolean useEdgeDetection = false; // -e
        boolean outlineQuadTrees = false; // -t
        String inputPath = null;
        String outputPathBase = null;

        // Argüman sırasının önemli olmaması için basit bir döngü
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    useCompression = true;
                    break;
                case "-e":
                    useEdgeDetection = true;
                    break;
                case "-t":
                    outlineQuadTrees = true;
                    break;
                case "-i":
                    if (i + 1 < args.length) inputPath = args[i + 1];
                    break;
                case "-o":
                    if (i + 1 < args.length) outputPathBase = args[i + 1];
                    break;
            }
        }

        if (inputPath == null || outputPathBase == null) {
            System.err.println("Hata: Giriş (-i) ve/veya Çıktı (-o) dosyası belirtilmedi.");
            return;
        }

        // --- 2. İşlem Başlatma ---
        ImageProcessor processor = new ImageProcessor(inputPath, outputPathBase);

        try {
            Image originalImage = processor.ReadImage();

            if (originalImage.getWidth() != originalImage.getHeight() ||
                    !processor.isPowerOfTwo(originalImage.getWidth())) {
                System.err.println("Hata: Görüntü kare değil veya boyutu 2'nin kuvveti değil. İşlem iptal edildi.");
                return;
            }

            if (useCompression) {
                // --- Sıkıştırma Modu (-c) ---

                // outputNameBase (örneğin "out") -> out-1.ppm, out-2.ppm...
                processor.GenerateCompressedImages(originalImage, outputPathBase);

            } else if (useEdgeDetection) {
                // --- Kenar Tespiti Modu (-e) ---

                // 1. Kenar tespiti için kullanılacak Quadtree'yi inşa et.
                // Ödev, kenar tespiti için de uygun bir eşik seçilmesini istiyor.
                // Örneğin, yüksek kontrastı yakalamak için nispeten düşük bir eşik kullanabiliriz (deneysel değer).
                final double EDGE_THRESHOLD = 5000.0; // DENEYSEL DEĞER: Görüntüye göre ayarlanmalı
                final int MIN_FILTER_SIZE = 8; // Filtre uygulanacak maksimum bölge boyutu (8x8)

                System.out.println("Kenar Tespiti Quadtree'si oluşturuluyor...");
                processor.quadTree.resetSize();
                processor.quadTree.setRoot(processor.BuildCompressedTreeRecursive(originalImage, EDGE_THRESHOLD));

                // 2. Filtreyi Quadtree yapısına göre uygula
                Image resultImage = processor.ApplyEdgeDetection(
                        originalImage,
                        processor.quadTree.getRoot(),
                        MIN_FILTER_SIZE
                );

                // 3. Çerçeveleme (-t)
                if (outlineQuadTrees) {
                    System.out.println("Quadtree çerçeveleri çiziliyor...");
                    // Çizgileri, filtrelenmiş görüntünün üzerine çizer.
                    processor.DrawQuadtreeOutline(resultImage, processor.quadTree.getRoot());
                }

                // 4. Çıktı dosyasını yaz (Tek çıktı dosyası)
                processor.WriteImage(resultImage, outputPathBase + ".ppm");
                System.out.println("Kenar Tespiti tamamlandı. Çıktı: " + outputPathBase + ".ppm");

            } else {
                System.out.println("Hata: İşlem bayrağı (-c veya -e) belirtilmedi. Program sonlandırılıyor.");
            }

        } catch (ImageIsNotSquareShaped | IOException e) {
            System.err.println("Görüntü İşleme Hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

