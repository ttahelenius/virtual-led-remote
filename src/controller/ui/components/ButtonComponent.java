package controller.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import static controller.Controller.exit;
import static java.awt.AlphaComposite.SRC_OVER;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.geom.AffineTransform.getScaleInstance;

/**
 * A glossy JButton customized for use in conjunction with references to {@link controller.ui.Button} and
 * {@link ButtonComponentGlowPanel}. Optimized for the Nimbus Look and Feel.
 */
public class ButtonComponent extends JButton {

  final controller.ui.Button button;
  private final ButtonComponentGlowPanel container;

  public ButtonComponent(final controller.ui.Button button, final ButtonComponentGlowPanel container) {
    this.button = button;
    this.container = container;
    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    setFocusable(false);
    if (button.font != null)
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(button.font);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D)g;

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Shape originalClip = g.getClip();

    setClipForLightingEffects(g);
    if (container.isHeld(this)) {
      paintButtonBevel(g2d);
      g2d.translate(1.8, 3.0);
    } else if (isEnabled()) {
      paintButtonShadow(g2d);
    }

    setClipForErasingOriginalBorder(g);
    super.paintComponent(g);

    setClipForButton(g);
    paintButton(g2d, getWidth(), getHeight());
    paintButtonBorder(g2d);

    g.setClip(originalClip);

    paintButtonBevelBorder(g2d);
  }

  private void setClipForLightingEffects(Graphics g) {
    if (container.isHeld(this))
      setClipForHeldButton(g);
    else
      g.setClip(0, 0, getWidth() + 4, getHeight() + 4);
  }

  private void setClipForErasingOriginalBorder(Graphics g) {
    if (container.isHeld(this))
      setClipForHeldButton(g);
    else
      g.setClip(new RoundRectangle2D.Double(4, 3, getWidth() - 8, getHeight() - 7, 6, 6));
  }

  private void setClipForButton(Graphics g) {
    if (container.isHeld(this))
      setClipForHeldButton(g);
    else
      g.setClip(0, 0, getWidth(), getHeight());
  }

  private void setClipForHeldButton(Graphics g) {
    g.setClip(new RoundRectangle2D.Double(1, 2, getWidth() - 5, getHeight() - 8, 12, 12));
  }

  @Override
  public void addActionListener(ActionListener l) {
    super.addActionListener(e -> {
      Cursor normal = getCursor();
      try {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        l.actionPerformed(e);
      } finally {
        setCursor(normal);
      }
    });
  }

  private void paintButtonBevel(Graphics2D g) {
    g.setStroke(new BasicStroke(3));
    g.setPaint(getBevelColor());
    g.fillRoundRect(4, 4, getWidth() - 7, getHeight() - 7, 10, 10);
  }

  private void paintButtonShadow(Graphics2D g) {
    g.setStroke(new BasicStroke(2));
    g.setPaint(new Color(0, 0, 0, 20));
    g.fillRoundRect(5, 6, getWidth() - 5, getHeight() - 5, 8, 8);
  }

  private void paintButtonBorder(Graphics2D g) {
    if (container.isGlowing(this)) {
      g.setStroke(new BasicStroke(3));
      g.setPaint(Color.WHITE);
      g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 6, 8, 8);
    } else if (container.isHeld(this)) {
      g.setStroke(new BasicStroke(3));
      g.setPaint(getBevelColor());
      g.drawRoundRect(4, 3, getWidth() - 7, getHeight() - 7, 8, 8);
    } else if (!isEnabled()) {
      g.setStroke(new BasicStroke(2));
      g.setPaint(getDisabledBorderColor());
      g.drawRoundRect(4, 3, getWidth() - 8, getHeight() - 7, 6, 6);
    } else {
      g.setStroke(new BasicStroke(1));
      g.setPaint(Color.WHITE);
      g.drawRoundRect(4, 3, getWidth() - 8, getHeight() - 7, 6, 6);
    }
  }

  private void paintButtonBevelBorder(Graphics2D g) {
    if (container.isHeld(this)) {
      g.setStroke(new BasicStroke(2));
      g.setPaint(mix(.5f, getBevelColor(), container.getBackground()));
      g.drawRoundRect(2, 1, getWidth() - 6, getHeight() - 7, 8, 8);
    }
  }

  private void paintButton(Graphics2D g, int width, int height) {
    int border_w = 5,  border_h = 4;
    int x = border_w,  y = border_h,  w = width - border_w*2,  h = height - border_h*2;

    paintGleam(g, x, y, w, h);

    g.setComposite(ButtonBlendComposite.INSTANCE);

    controller.ui.Button.Gradient bg = button.background;
    int n = bg.colors.size();
    if (!bg.discrete)
      n--;
    for (int i = 0; i < n; i++) {
      Paint paint;
      if (bg.discrete)
        paint = modify(bg.colors.get(i));
      else
        paint = new GradientPaint((float)(x + w * i/n), 0f, modify(bg.colors.get(i)),
                                  (float)(x + w * (i+1)/n), 0f, modify(bg.colors.get(i+1)));
      g.setPaint(paint);
      g.fillRect(x + w * i/n, y, w*(i+1)/n - w*i/n, h); // Mathematically w(i+1)/n - wi/n = w/n but this accounts for rounding
    }

    if (button.label != null) {
      g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
      g.setComposite(AlphaComposite.getInstance(SRC_OVER, container.isGlowing(this) ? 0.2f : 0.6f));
      g.setFont(button.font);
      AffineTransform original = g.getTransform();
      try {
        double sc = width / 60d;
        double tr = -width / 2d;
        AffineTransform transform = (AffineTransform)original.clone();
        transform.translate(-tr, -tr);
        transform.scale(sc, sc);
        transform.translate(tr, tr);
        g.setTransform(transform);
        FontMetrics m = g.getFontMetrics();
        Rectangle bounds = m.getStringBounds(button.label, g).getBounds();
        g.setPaint(new GradientPaint(0f, 0f, new Color(70, 70, 70),
                                     0f, height, new Color(218, 218, 218)));
        g.drawString(button.label,
            (width - bounds.width) / 2,
            (height - bounds.height) / 2 + m.getAscent() - m.getLeading());
      } finally {
        g.setTransform(original);
      }
    }

    g.setComposite(AlphaComposite.SrcOver);
  }

  private Color modify(Color color) {
    return isEnabled() ? color : color.darker();
  }

  private Color getBevelColor() {
    return mix(0.7f, AdditiveBlendComposite.mix(button.background.colors), container.getBackground().darker().darker());
  }

  private Color getDisabledBorderColor() {
    return mix(0.5f, AdditiveBlendComposite.mix(button.background.colors), container.getBackground().darker());
  }

  public static Color mix(float f, Color color1, Color color2) {
    float r = color1.getRed() * (1f - f)   + color2.getRed() * f;
    float g = color1.getGreen() * (1f - f) + color2.getGreen() * f;
    float b = color1.getBlue() * (1f - f)  + color2.getBlue() * f;
    return new Color((int)r, (int)g, (int)b);
  }

  private static final float[] GLEAM_LOWER_FRAC = {0f, 0.8f, 1f};
  private static final Color[] GLEAM_LOWER_COLORS = {
      new Color(0, 0, 0, 0), new Color(220, 220, 220, 0), new Color(255, 255, 255, 75)
  };
  private static final AffineTransform GLEAM_LOWER_TRANSFORM = getScaleInstance(.8, .4);

  private static final float[] GLEAM_OVER_FRAC = {0f, 0.995f, 1f};
  private static final Color[] GLEAM_OVER_COLORS = {
      new Color(255, 255, 255, 255), new Color(255, 255, 255, 0), new Color(0, 0, 0, 0)
  };
  private static final AffineTransform GLEAM_OVER_TRANSFORM = getScaleInstance(.7, .5);

  private static final float[] GLEAM_LEFT_FRAC = {0f, 0.85f, 1f};
  private static final Color[] GLEAM_LEFT_COLORS = {
      new Color(255, 255, 255, 255), new Color(255, 255, 255, 0), new Color(0, 0, 0, 0)
  };
  private static final AffineTransform GLEAM_LEFT_TRANSFORM = getScaleInstance(.05, 1.7);

  private static final float[] GLEAM_UPPER_FRAC = {0f, 0.85f, 1f};
  private static final Color[] GLEAM_UPPER_COLORS = {
      new Color(255, 255, 255, 155), new Color(255, 255, 255, 0), new Color(0, 0, 0, 0)
  };
  private static final AffineTransform GLEAM_UPPER_TRANSFORM = getScaleInstance(1.2, .05);

  private void paintGleam(Graphics2D g, int x, int y, int w, int h) {
    g.setComposite(AlphaComposite.getInstance(SRC_OVER, container.isHeld(this) ? .6f : 1f));

    g.setPaint(new RadialGradientPaint(
        new Point2D.Float(x + w * .4f, y + h * .2f),
        w * 1.85f,
        new Point2D.Float(x + w * .4f, y + h * .2f),
        GLEAM_LOWER_FRAC,
        GLEAM_LOWER_COLORS,
        MultipleGradientPaint.CycleMethod.NO_CYCLE,
        MultipleGradientPaint.ColorSpaceType.SRGB,
        GLEAM_LOWER_TRANSFORM));
    g.fillRect(x, y, w, h);

    g.setPaint(new RadialGradientPaint(
        new Point2D.Float(x + w * .3f, y - h * 1.2f),
        w * 2.2f,
        new Point2D.Float(x - w * .1f, y - h * 1.2f),
        GLEAM_OVER_FRAC,
        GLEAM_OVER_COLORS,
        MultipleGradientPaint.CycleMethod.NO_CYCLE,
        MultipleGradientPaint.ColorSpaceType.SRGB,
        GLEAM_OVER_TRANSFORM));
    g.fillRect(x, y, w, h);

    g.setPaint(new RadialGradientPaint(
        new Point2D.Float(x + w * 3.3f, y - h * 1f),
        w * 1.8f,
        new Point2D.Float(x + w * 0.2f, y - h * .5f),
        GLEAM_LEFT_FRAC,
        GLEAM_LEFT_COLORS,
        MultipleGradientPaint.CycleMethod.NO_CYCLE,
        MultipleGradientPaint.ColorSpaceType.SRGB,
        GLEAM_LEFT_TRANSFORM));
    g.fillRect(x, y, w, h);

    g.setPaint(new RadialGradientPaint(
        new Point2D.Float(x - w * 1f, y + h * 3.5f),
        w * 1.8f,
        new Point2D.Float(x - w * .6f, y + h * 3.2f),
        GLEAM_UPPER_FRAC,
        GLEAM_UPPER_COLORS,
        MultipleGradientPaint.CycleMethod.NO_CYCLE,
        MultipleGradientPaint.ColorSpaceType.SRGB,
        GLEAM_UPPER_TRANSFORM));

    g.fillRect(x, y, w, h);
  }

  private static class ButtonBlendComposite implements Composite, CompositeContext {

    private static final ButtonBlendComposite INSTANCE = new ButtonBlendComposite();

    private ButtonBlendComposite() {}

    private void checkRaster(Raster r) {
      if (r.getSampleModel().getDataType() != DataBuffer.TYPE_INT)
        exit(new IllegalStateException("Expected integer sample type"));
    }

    @Override
    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
      checkRaster(src);
      checkRaster(dstIn);
      checkRaster(dstOut);

      int width  = Math.min(src.getWidth(), dstIn.getWidth());
      int height = Math.min(src.getHeight(), dstIn.getHeight());
      int[] srcPixels = new int[width];
      int[] dstPixels = new int[width];

      for (int y = 0; y < height; y++) {
        src.getDataElements(0, y, width, 1, srcPixels);
        dstIn.getDataElements(0, y, width, 1, dstPixels);

        for (int x = 0; x < width; x++) {
          dstPixels[x] = processPixels(srcPixels[x], dstPixels[x]);
        }

        dstOut.setDataElements(0, y, width, 1, dstPixels);
      }
    }

    private static int processPixels(int x, int y) {
      int xb = (x) & 0xFF;
      int yb = (y) & 0xFF;

      int xg = (x >> 8) & 0xFF;
      int yg = (y >> 8) & 0xFF;

      int xr = (x >> 16) & 0xFF;
      int yr = (y >> 16) & 0xFF;

      int a = 255;

      double ff = 3_500_000_000d;
      int r = processChannel((int)Math.min(255d, Math.pow(yr, 5d)/ff), xr);
      int g = processChannel((int)Math.min(255d, Math.pow(yg, 5d)/ff), xg);
      int b = processChannel((int)Math.min(255d, Math.pow(yb, 5d)/ff), xb);

      return (b) | (g << 8) | (r << 16) | (a << 24);
    }

    private static int processChannel(int src, int dest) {
      double srcPercentage = src / 255d;
      double destPercentage = dest / 255d;
      double result;

      if (srcPercentage < .5)
        result = (destPercentage * srcPercentage * 2d) * 255d;
      else
        result = (1d - (2d * (1d - srcPercentage) * (1d - destPercentage))) * 255d;

      return Math.min(255, (int)result);
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
      return this;
    }

    @Override
    public void dispose() {
    }

  }
}
