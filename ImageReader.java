import java.io.IOException;

public interface ImageReader {
    Image ReadImage(String filename) throws IOException;
}
