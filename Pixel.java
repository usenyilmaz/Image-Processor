public class Pixel {
    private int red;
    private int green;
    private int blue;

    private int xCor;
    private int yCor;

    public Pixel(int red, int green, int blue, int xCor, int yCor) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.xCor = xCor;
        this.yCor = yCor;

        if(red > 255 || green > 255 || blue > 255) {
            throw new WrongPixelValue("Pixel values must be between 0 and 255");
        }
        if(red < 0 || green < 0 || blue < 0) {
            throw new WrongPixelValue("Pixel values must be between 0 and 255");
        }

    }
    //getters
    int getRed() {
        return red;
    }
    int getGreen() {
        return green;
    }
    int getBlue() {
        return blue;
    }
    int getXCor() {
        return xCor;
    }
    int getYCor() {
        return yCor;
    }



}
