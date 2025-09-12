package com.newpix.client.gui.animations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe utilitária para animações e transições na interface do NewPix.
 */
public class AnimationUtils {
    
    /**
     * Anima o fade in de um componente
     */
    public static void fadeIn(Component component, int duration) {
        fadeComponent(component, 0.0f, 1.0f, duration);
    }
    
    /**
     * Anima o fade out de um componente
     */
    public static void fadeOut(Component component, int duration) {
        fadeComponent(component, 1.0f, 0.0f, duration);
    }
    
    /**
     * Anima a opacidade de um componente
     */
    private static void fadeComponent(Component component, float startOpacity, float endOpacity, int duration) {
        Timer timer = new Timer(50, null);
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                
                float currentOpacity = startOpacity + (endOpacity - startOpacity) * progress;
                
                if (component instanceof JComponent) {
                    ((JComponent) component).putClientProperty("JComponent.alpha", currentOpacity);
                    component.repaint();
                }
                
                if (progress >= 1.0f) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Anima o movimento suave de um componente
     */
    public static void slideComponent(Component component, Point start, Point end, int duration) {
        Timer timer = new Timer(16, null); // ~60 FPS
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                progress = easeInOutCubic(progress); // Aplica easing
                
                int currentX = (int) (start.x + (end.x - start.x) * progress);
                int currentY = (int) (start.y + (end.y - start.y) * progress);
                
                component.setLocation(currentX, currentY);
                
                if (progress >= 1.0f) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Anima a mudança de cor de fundo de um componente
     */
    public static void animateBackgroundColor(JComponent component, Color startColor, Color endColor, int duration) {
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        final Color originalColor = component.getBackground();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                
                int red = (int) (startColor.getRed() + (endColor.getRed() - startColor.getRed()) * progress);
                int green = (int) (startColor.getGreen() + (endColor.getGreen() - startColor.getGreen()) * progress);
                int blue = (int) (startColor.getBlue() + (endColor.getBlue() - startColor.getBlue()) * progress);
                
                Color currentColor = new Color(red, green, blue);
                component.setBackground(currentColor);
                component.repaint();
                
                if (progress >= 1.0f) {
                    timer.stop();
                    // Restaurar cor original após um tempo
                    Timer restoreTimer = new Timer(1000, evt -> {
                        animateBackgroundColor(component, currentColor, originalColor, 300);
                        ((Timer) evt.getSource()).stop();
                    });
                    restoreTimer.setRepeats(false);
                    restoreTimer.start();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Cria um efeito de "pulse" em um componente
     */
    public static void pulseComponent(JComponent component, int duration, int cycles) {
        Color originalColor = component.getBackground();
        Color pulseColor = originalColor.brighter();
        
        Timer timer = new Timer(duration / (cycles * 2), null);
        final int[] cycleCount = {0};
        final boolean[] increasing = {true};
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (increasing[0]) {
                    component.setBackground(pulseColor);
                } else {
                    component.setBackground(originalColor);
                }
                
                increasing[0] = !increasing[0];
                
                if (!increasing[0]) {
                    cycleCount[0]++;
                }
                
                if (cycleCount[0] >= cycles) {
                    component.setBackground(originalColor);
                    timer.stop();
                }
                
                component.repaint();
            }
        });
        
        timer.start();
    }
    
    /**
     * Anima a escala de um componente (zoom in/out)
     */
    public static void scaleComponent(JComponent component, float startScale, float endScale, int duration) {
        Timer timer = new Timer(16, null);
        final long startTime = System.currentTimeMillis();
        final Dimension originalSize = component.getSize();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                progress = easeInOutCubic(progress);
                
                float currentScale = startScale + (endScale - startScale) * progress;
                
                int newWidth = (int) (originalSize.width * currentScale);
                int newHeight = (int) (originalSize.height * currentScale);
                
                component.setSize(newWidth, newHeight);
                component.revalidate();
                component.repaint();
                
                if (progress >= 1.0f) {
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Mostra uma notificação toast
     */
    public static void showToast(JFrame parent, String message, int duration, Color backgroundColor) {
        JWindow toast = new JWindow(parent);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        
        label.setOpaque(true);
        label.setBackground(backgroundColor);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        toast.add(label);
        toast.pack();
        
        // Posicionar no topo da janela pai
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();
        Dimension toastSize = toast.getSize();
        
        int x = parentLocation.x + (parentSize.width - toastSize.width) / 2;
        int y = parentLocation.y + 50;
        
        toast.setLocation(x, y);
        toast.setVisible(true);
        
        // Fade in
        fadeIn(toast, 300);
        
        // Auto-hide after duration
        Timer hideTimer = new Timer(duration, e -> {
            fadeOut(toast, 300);
            Timer disposeTimer = new Timer(300, evt -> {
                toast.dispose();
                ((Timer) evt.getSource()).stop();
            });
            disposeTimer.setRepeats(false);
            disposeTimer.start();
            ((Timer) e.getSource()).stop();
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
    
    /**
     * Função de easing cubic para animações mais suaves
     */
    private static float easeInOutCubic(float t) {
        return t < 0.5 ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }
    
    /**
     * Cria um efeito de loading spinner
     */
    public static JPanel createLoadingSpinner() {
        JPanel panel = new JPanel() {
            private int angle = 0;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int size = Math.min(width, height) - 20;
                int x = (width - size) / 2;
                int y = (height - size) / 2;
                
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                for (int i = 0; i < 8; i++) {
                    float alpha = (i + 1) / 8.0f;
                    g2.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
                    
                    int startAngle = angle + i * 45;
                    g2.rotate(Math.toRadians(startAngle), x + size / 2, y + size / 2);
                    g2.drawLine(x + size / 2, y + 5, x + size / 2, y + 15);
                    g2.rotate(-Math.toRadians(startAngle), x + size / 2, y + size / 2);
                }
                
                g2.dispose();
            }
        };
        
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(50, 50));
        
        // Animar o spinner
        Timer timer = new Timer(100, e -> {
            panel.putClientProperty("angle", 
                ((Integer) panel.getClientProperty("angle") == null ? 0 : 
                 (Integer) panel.getClientProperty("angle")) + 45);
            panel.repaint();
        });
        timer.start();
        
        // Parar o timer quando o componente for removido
        panel.addPropertyChangeListener("ancestor", evt -> {
            if (evt.getNewValue() == null) {
                timer.stop();
            }
        });
        
        return panel;
    }
}
