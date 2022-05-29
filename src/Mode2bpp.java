import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Mode2bpp implements ICGAInterlaced {
    private Palette palette;
    private int width;
    private final int height;
    private final PaletteImage image;
    private PALETTE selectedPalette;

    enum PALETTE {
        LOW_0,
        HIGH_0,
        LOW_1,
        HIGH_1,
        LOW_5,
        HIGH_5,
    }

    Mode2bpp(BufferedImage image, PALETTE selectedPalette, boolean dither, boolean resize) {
        this.selectedPalette = selectedPalette;
        switch (selectedPalette) {
            case LOW_0:
                palette = new Palette(new int[]{0,0x00aa00,0xaa0000,0xaa5500});
                break;
            case HIGH_0:
                palette = new Palette(new int[]{0,0x55ff55,0xff5555,0xffff55});
                break;
            case LOW_1:
                palette = new Palette(new int[]{0,0x00aaaa,0xaa00aa,0xaaaaaa});
                break;
            case HIGH_1:
                palette = new Palette(new int[]{0,0x55ffff,0xff55ff,0xffffff});
                break;
            case LOW_5:
                palette = new Palette(new int[]{0,0x00aaaa,0xaa0000,0xaaaaaa});
                break;
            case HIGH_5:
                palette = new Palette(new int[]{0,0x55ffff,0xff5555,0xffffff});
                break;
        }
        if (resize)
            image = Mode.resizeImage(image, 320, 200);
        this.image = new PaletteImage(image, this.palette, dither);
        this.width = 4 * (this.image.getWidth() / 4);
        this.height = 4 * (this.image.getHeight() / 4);
    }

    private int[] getRow(int rowNum) {
        int[] row = new int[this.width / 4];
        for (int section = 0; section < this.width; section += 4) {
            int charByte = 0;
            for (int character = 0; character < 4; character++) {
                charByte = charByte << 2;
                charByte += image.getPixelIndex(section + character, rowNum);
            }
            row[section / 4] = charByte;
        }
        return row;
    }



    public int[] getEven() {
        // 0 2 ....
        int[] even = new int[(this.width / 4) * (this.height / 2)];
        for (int row = 0; row < this.height; row += 2) {
            Mode.mergeAtIndex(even, getRow(row), row / 2 * this.width / 8);
        }
        return even;
    }

    public int[] getOdd() {
        // 1 3 ....
        int[] odd = new int[(this.width / 4) * (this.height / 2)];
        for (int row = 1; row < this.height; row += 2) {
            Mode.mergeAtIndex(odd, getRow(row), row / 2 * this.width / 8);
        }
        return odd;
    }

    public int[] get() {
        int[] im = new int[(this.width / 4) * this.height];
        Mode.mergeAtIndex(im, getEven(), 0);
        Mode.mergeAtIndex(im, getOdd(), this.width / 4 * this.height / 2);
        return im;
    }

    @Override
    public BufferedImage getImage() {
        return image.toImage();
    }

    @Override
    public int[] getCom() {
        String comName = switch (selectedPalette) {
            case LOW_0 -> "2b0L.com";
            case HIGH_0 -> "2b0H.com";
            case LOW_1 -> "2b1L";
            case HIGH_1 -> "2b1H";
            case LOW_5 -> "2b5L";
            case HIGH_5 -> "2b5H";
        };
        try (InputStream in = getClass().getResourceAsStream("/com/"+comName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            int length = in.available();
            int[] outputData = new int[16000 + length];
            for (int i = 0; i < length; i++) {
                outputData[i] = reader.read();
            }
            Mode.mergeAtIndex(outputData, this.get(), length);
            return outputData;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}