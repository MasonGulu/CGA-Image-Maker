import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Mode1bpp implements ICGAInterlaced {
    private final Palette palette = new Palette(new int[]{0x000000, 0xFFFFFF});
    private final int width;
    private final int height;
    private final PaletteImage image;
    Mode1bpp(BufferedImage image, boolean dither, boolean resize) {
        if (resize)
            image = Mode.resizeImage(image, 640, 200);
        this.image = new PaletteImage(image, this.palette, dither);
        this.width = 8 * (this.image.getWidth() / 8);
        this.height = 8 * (this.image.getHeight() / 8);
    }

    private int[] getRow(int rowNum) {
        int[] row = new int[this.width / 8];
        for (int section = 0; section < this.width; section += 8) {
            int charByte = 0;
            for (int character = 0; character < 8; character++) {
                charByte = charByte << 1;
                charByte += image.getPixelIndex(section + character, rowNum);
            }
            row[section/8] = charByte;
        }
        return row;
    }

    public int[] getEven() {
        // 0 2 ....
        int[] even = new int[(this.width / 8) * (this.height / 2)];
        for (int row = 0; row < this.height; row += 2) {
            Mode.mergeAtIndex(even, getRow(row), row/2*this.width/8);
        }
        return even;
    }

    public int[] getOdd() {
        // 1 3 ....
        int[] odd = new int[(this.width / 8) * (this.height / 2)];
        for (int row = 1; row < this.height; row += 2) {
            Mode.mergeAtIndex(odd, getRow(row), (row-1)/2*this.width/8);
        }
        return odd;
    }

    public int[] get() {
        int[] im = new int[(this.width / 8) * this.height + 192];
        Mode.mergeAtIndex(im, getEven(), 0);
        Mode.mergeAtIndex(im, getOdd(), this.width/8 * this.height/2 + 192);
        return im;
    }

    public BufferedImage getImage() {
        return Mode.scaleImage(image.toImage(), 1, 2);
    }

    @Override
    public int[] getCom() throws IOException {
        String comName = "1b.com";
        InputStream in = getClass().getResourceAsStream("/com/"+comName);
        int length = in.available();
        int[] outputData = new int[16192 + length];
        for (int i = 0; i < length; i++) {
            outputData[i] = in.read();
        }
        in.close();
        Mode.mergeAtIndex(outputData, this.get(), length);
        return outputData;
    }
}
