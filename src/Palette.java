import java.awt.image.BufferedImage;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Palette {
    private int[] colors;
    Palette(int[] colors) {
        this.colors = colors;
    }

    public int getColor(int index) {
        return colors[index];
    }

    private double getDifferenceBetweenColors(int[] colorA, int colorB) {
        int[] B = Colors.splitColor(colorB);
        int[] diff = new int[3];
        for (int i = 0; i < 3; i++) {
            diff[i] = colorA[i] - B[i];
        }
        return sqrt(diff[0]*diff[0] + diff[1]*diff[1] + diff[2]*diff[2]);
    }

    public int getClosestPaletteIndex(int[] color) {
        int closestIndex = 0;
        double closestDiff = 0xFFFFFF;
        for (int index = 0; index < colors.length; index++) {
            double colorDiff = getDifferenceBetweenColors(color, colors[index]);
            if (colorDiff < closestDiff) {
                closestDiff = colorDiff;
                closestIndex = index;
            }
        }
        return closestIndex;
    }
}
