public class Image {
    private Pixel[][] pixels;
    private int width;
    private int height;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Pixel[width][height];
    }

    public void addPixel(Pixel p, int x, int y){
        pixels[x][y] = p;
    }

    public Pixel getPixel(int x, int y){
        return pixels[x][y];
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public Pixel[][] getPixels(){
        return pixels;

    }



    public void setPixel(int x, int y, Pixel p){
        pixels[x][y] = p;
    }

    // ... Image.java içindeki diğer metotlar ...

    // isPixel() metodu da boyut kontrolü yapmalı:
    public boolean isPixel(){
        // Pixels matrisi var mı ve 1x1 boyutunda mı?
        return this.width == 1 && this.height == 1;
    }

    // ... NorthWestSubImage metodu doğru (değiştirilmedi) ...
    public Image NorthWestSubImage(){
        Image result = new Image(this.width / 2, this.height / 2);
        for(int x = 0; x < this.width / 2; x++){
            for(int y = 0; y < this.height / 2; y++){
                result.addPixel(pixels[x][y], x, y);
            }
        }
        return result;
    }


    public Image NorthEastSubImage(){
        Image result = new Image(this.width / 2, this.height / 2);
        int halfW = this.width / 2;
        int halfH = this.height / 2;
        for(int x = 0; x < halfW; x++){
            for(int y = 0; y < halfH; y++){
                // Düzeltme: X koordinatı (halfW + x) olmalı, (width - x) değil.
                result.addPixel(pixels[halfW + x][y], x, y);
            }
        }
        return result;
    }
    public Image SouthWestSubImage(){
        Image result = new Image(this.width / 2, this.height / 2);
        int halfW = this.width / 2;
        int halfH = this.height / 2;
        for(int x = 0; x < halfW; x++){
            for(int y = 0; y < halfH; y++){
                // Düzeltme: Y koordinatı (halfH + y) olmalı, (height - y) değil.
                result.addPixel(pixels[x][halfH + y], x ,y);
            }
        }
        return result;
    }
    public Image SouthEastSubImage(){
        Image result = new Image(this.width / 2, this.height / 2);
        int halfW = this.width / 2;
        int halfH = this.height / 2;
        for(int x = 0; x < halfW; x++){
            for(int y = 0; y < halfH; y++){
                // Düzeltme: Hem X (halfW + x) hem de Y (halfH + y) ofsetlenmeli.
                result.addPixel(pixels[halfW + x][halfH + y], x, y);
            }
        }
        return result;
    }
    // Bir Image bölgesindeki tüm piksellerin ortalama rengini hesaplar.
    public Pixel calculateMeanColor() {
        long totalR = 0;
        long totalG = 0;
        long totalB = 0;
        int pixelCount = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel p = pixels[x][y];
                totalR += p.getRed();
                totalG += p.getGreen();
                totalB += p.getBlue();
            }
        }

        // Ortalama değerleri tamsayı olarak hesapla ve 0-255 arasına sıkıştır (clamping).
        // Ortalama 0 ile 255 arasında olacağı için clamping teorik olarak gerekmez,
        // ancak güvenli tamsayıya çevirme önemlidir.
        int meanR = (int) (totalR / pixelCount);
        int meanG = (int) (totalG / pixelCount);
        int meanB = (int) (totalB / pixelCount);

        // Yeni bir Pixel nesnesi (ortalama rengi temsil eden) döndür.
        return new Pixel(meanR, meanG, meanB);
    }
    /**
     * Bu Image bölgesinin Ortalama Karesel Hatasını (Mean Squared Error - E_i) hesaplar.
     * Hata, her pikselin rengi ile ortalama renk C_i arasındaki karesel Öklid mesafesinin ortalamasıdır.
     * @param meanColor Bölgenin daha önce hesaplanmış ortalama rengi (Pixel nesnesi).
     * @return Ortalama Karesel Hata değeri (double).
     */
    public double calculateMeanSquaredError(Pixel meanColor) {
        long cumulativeSquaredError = 0;
        int meanR = meanColor.getRed();
        int meanG = meanColor.getGreen();
        int meanB = meanColor.getBlue();
        int pixelCount = width * height;

        // Ödevde belirtilen formül: E_i = (1/N^2) * Σ |n_i(x,y) - C_i|^2 [cite: 52, 54]
        // |n_i(x,y) - C_i|^2 = (r - C_i.r)^2 + (g - C_i.g)^2 + (b - C_i.b)^2

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Pixel p = pixels[x][y];

                // Her renk bileşeni için karesel farkı hesapla
                long diffR = p.getRed() - meanR;
                long diffG = p.getGreen() - meanG;
                long diffB = p.getBlue() - meanB;

                // Karesel Öklid mesafesini topla (toplam karesel hata)
                cumulativeSquaredError += (diffR * diffR + diffG * diffG + diffB * diffB);
            }
        }

        // Ortalama Karesel Hatayı (E_i) bulmak için toplamı piksel sayısına böl.
        return (double) cumulativeSquaredError / pixelCount;
    }
    /**
     * Görüntü nesnesinin tüm piksellerini belirtilen renk ile doldurur.
     * Bu, sıkıştırma Quadtree'sinde bir alt bölgeyi ortalama renk ile temsil etmek için kullanılır.
     * * @param color Tüm piksel matrisini doldurmak için kullanılacak Pixel nesnesi (genellikle ortalama renk).
     */
    public void fillWithColor(Pixel color) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Önemli: Eğer Image nesnesi yeni oluşturulmuşsa (pixels matrisi boşsa),
                // buradaki atama sorun yaratmaz. Eğer matris zaten doluysa, üzerine yazar.
                // Image sınıfınızdaki Pixel[][] pixels matrisine erişiminiz olduğunu varsayıyoruz.

                // Yeni bir Pixel nesnesi oluşturarak atamak, referans yerine değer kopyalamayı sağlar.
                // Bu, aynı Pixel nesnesinin tüm matrisi referans alması yerine, her hücrede
                // bağımsız bir kopya olmasını sağlar, ancak bu basit senaryoda sadece atama da yeterlidir.
                this.pixels[x][y] = color;
            }
        }
    }


}

