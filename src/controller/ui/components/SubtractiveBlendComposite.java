package controller.ui.components;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import static controller.Controller.exit;

/**
 * Composite that implements subtractive color blending
 */
class SubtractiveBlendComposite implements Composite, CompositeContext {

  static final SubtractiveBlendComposite INSTANCE = new SubtractiveBlendComposite();

  private SubtractiveBlendComposite() {
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
        dstPixels[x] = subtractPixels(srcPixels[x], dstPixels[x]);
      }

      dstOut.setDataElements(0, y, width, 1, dstPixels);
    }
  }

  private static int subtractPixels(int x, int y) {
    int xa = (x >> 24) & 0xFF;
    int ya = (y >> 24) & 0xFF;
    int a = ya;

    int xb = (x) & 0xFF;
    int yb = (y) & 0xFF;
    int b = Math.max(0, yb - (int)(xb * xa/255f));

    int xg = (x >> 8) & 0xFF;
    int yg = (y >> 8) & 0xFF;
    int g = Math.max(0, yg - (int)(xg * xa/255f));

    int xr = (x >> 16) & 0xFF;
    int yr = (y >> 16) & 0xFF;
    int r = Math.max(0, yr - (int)(xr * xa/255f));

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
