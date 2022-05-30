import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ICGA {
    int[] get();
    BufferedImage getImage();
    int[] getCom() throws IOException;
}
