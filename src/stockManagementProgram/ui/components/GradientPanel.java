package stockManagementProgram.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Custom JPanel implementation that renders a gradient background.
 * Used as a base panel for various sections of the application to maintain visual consistency.
 */
public class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 240, 245),
                0, getHeight(), new Color(220, 220, 225));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
