public class Image {
    private Pixel[][] pixels;
    private int width;
    private int height;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Pixel[width][height];
    }

    public void addPixel(Pixel p){
        pixels[p.getXCor()][p.getYCor()] = p;
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



}
