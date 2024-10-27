package controller.ui.components;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.List;

import static controller.Controller.exit;

/**
 * Composite that implements additive color blending
 */
class AdditiveBlendComposite implements Composite, CompositeContext {

  static final AdditiveBlendComposite INSTANCE = new AdditiveBlendComposite();

  private AdditiveBlendComposite() {
  }

  static Color mix(List<Color> colors) {
    int c = 0;
    for (Color color : colors) {
      c = addPixels(c, color.getRGB());
    }
    return new Color(c);
  }

  private void checkRaster(Raster r) {
    if (r.getSampleModel().getDataType() != DataBuffer.TYPE_INT)
      exit(new IllegalStateException("Expected integer sample type"));
  }

  @Override
  public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
    checkRaster(src);
    checkRaster(dstIn);
    checkRaster(dstOut);

    int width = Math.min(src.getWidth(), dstIn.getWidth());
    int height = Math.min(src.getHeight(), dstIn.getHeight());
    int[] srcPixels = new int[width];
    int[] dstPixels = new int[width];

    for (int y = 0; y < height; y++) {
      src.getDataElements(0, y, width, 1, srcPixels);
      dstIn.getDataElements(0, y, width, 1, dstPixels);

      for (int x = 0; x < width; x++) {
        dstPixels[x] = addPixels(srcPixels[x], dstPixels[x]);
      }

      dstOut.setDataElements(0, y, width, 1, dstPixels);
    }
  }

  private static int addPixels(int x, int y) {
    int xb = (x) & 0xFF;
    int yb = (y) & 0xFF;
    int b = Math.min(255, xb + yb);

    int xg = (x >> 8) & 0xFF;
    int yg = (y >> 8) & 0xFF;
    int g = Math.min(255, xg + yg);

    int xr = (x >> 16) & 0xFF;
    int yr = (y >> 16) & 0xFF;
    int r = Math.min(255, xr + yr);

    int xa = (x >> 24) & 0xFF;
    int ya = (y >> 24) & 0xFF;
    int a = Math.min(255, xa + ya);

    return (b) | (g << 8) | (r << 16) | (a << 24);
  }

  @Override
  public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
    return this;
  }

  @Override
  public void dispose() {
  }

}
