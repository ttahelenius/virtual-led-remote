package controller.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * A JPanel that paints glow around the {@link ButtonComponent} currently under the mouse cursor. Additional
 * components under which to search for {@link ButtonComponent}s may be registered using
 * {@link #registerAddedComponent(Component)}.
 */
public class ButtonComponentGlowPanel extends JPanel {

  private ButtonComponent hovered;
  private ButtonComponent held;

  public ButtonComponentGlowPanel() {
    super();
  }

  public ButtonComponentGlowPanel(LayoutManager layout) {
    super(layout);
  }

  final void registerAddedComponent(Component comp) {
    List<Component> components = ComponentUtil.gatherComponents(comp);
    for (Component panelComponent : components) {
      if (panelComponent instanceof ButtonComponent) {
        registedAddedButton(panelComponent);
      }
    }
  }

  private void registedAddedButton(Component comp) {
    comp.addMouseListener(new ButtonMouseListener(comp));
  }

  boolean isHeld(ButtonComponent button) {
    return held == button && button.isEnabled() && hovered == button;
  }

  boolean isGlowing(ButtonComponent button) {
    return hovered == button && button.isEnabled() && !isHeld(button);
  }

  @Override
  public Component add(Component comp) {
    registerAddedComponent(comp);
    return super.add(comp);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (hovered == null || !hovered.isEnabled())
      return;

    Graphics2D g2d = (Graphics2D)g;
    g2d.setComposite(AdditiveBlendComposite.INSTANCE);

    Point position = SwingUtilities.convertPoint(hovered, hovered.getWidth() / 2, hovered.getHeight() / 2, this);
    int r = (int)(hovered.getWidth() * 1.8f);
    Color color = AdditiveBlendComposite.mix(hovered.button.background.colors);

    // Rougly exponential decay for light of color balanced by perceived luminance of the color:
    float luminance = 0.2126f*color.getRed() + 0.7152f*color.getGreen() + 0.0722f*color.getBlue();
    float balancingFactor = Math.min(120f / luminance, 1f) * (isGlowing(hovered) ? 1f : .3f);
    float f  = balancingFactor;
    float f2 = balancingFactor * .35f;
    float f3 = balancingFactor * .12f;
    Color darkened =  new Color((int) (color.getRed() * f),  (int) (color.getGreen() * f),  (int) (color.getBlue() * f));
    Color darkened2 = new Color((int) (color.getRed() * f2), (int) (color.getGreen() * f2), (int) (color.getBlue() * f2));
    Color darkened3 = new Color((int) (color.getRed() * f3), (int) (color.getGreen() * f3), (int) (color.getBlue() * f3));
    Color[] colors = {darkened, darkened, darkened2, darkened3, Color.BLACK};
    float[] fractions = {0f, .15f, .3f, .6f, 1f};

    g2d.setPaint(new RadialGradientPaint(position.x, position.y, r, fractions, colors));

    g2d.fillRect(position.x - r, position.y - r, 2 * r, 2 * r);

    g2d.setComposite(AlphaComposite.SrcOver);
  }

  private class ButtonMouseListener extends MouseAdapter {
    private final Component comp;

    public ButtonMouseListener(Component comp) {
      this.comp = comp;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      super.mouseEntered(e);
      if (held == null || held == comp)
        hovered = (ButtonComponent) comp;
      repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
      super.mouseExited(e);
      if (hovered == comp)
        hovered = null;
      repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      if (hovered == comp)
        held = hovered;
      repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      super.mouseReleased(e);
      held = null;
      repaint();
    }
  }
}
