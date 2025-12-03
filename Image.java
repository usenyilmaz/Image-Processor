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
}

