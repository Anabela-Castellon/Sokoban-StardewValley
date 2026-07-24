package com.sokoban.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * Paleta y helpers de dibujo compartidos por la vista (madera, piedra,
 * pergamino) para lograr la estetica "granja" de las pantallas de menu y
 * juego. Es el unico lugar que conoce estos colores; los paneles solo llaman
 * a estos metodos.
 */
final class TemaStardew {

    static final Color MADERA_CLARA = new Color(139, 98, 57);
    static final Color MADERA_OSCURA = new Color(94, 69, 48);
    static final Color MADERA_VETA = new Color(74, 52, 32);

    static final Color PIEDRA_CLARA = new Color(150, 150, 163);
    static final Color PIEDRA_OSCURA = new Color(96, 96, 110);
    static final Color MORTERO = new Color(70, 68, 78);

    static final Color PERGAMINO_FONDO = new Color(58, 42, 27, 235);
    static final Color DORADO = new Color(217, 164, 65);
    static final Color CREMA = new Color(245, 235, 216);

    static final Color BOTON_FONDO = new Color(91, 70, 50);
    static final Color BOTON_FONDO_HOVER = new Color(112, 87, 62);
    static final Color BOTON_FONDO_DESACTIVADO = new Color(60, 48, 38);

    private TemaStardew() {
    }

    /** Boton redondeado con relleno de madera oscura y borde dorado (estilo granja). */
    static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean activo = isEnabled();
                Color fondo = !activo ? BOTON_FONDO_DESACTIVADO
                        : getModel().isRollover() ? BOTON_FONDO_HOVER : BOTON_FONDO;
                g.setColor(fondo);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g.setColor(activo ? DORADO : PIEDRA_OSCURA);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        boton.setFont(new Font("SansSerif", Font.BOLD, 15));
        boton.setForeground(CREMA);
        boton.setHorizontalAlignment(SwingConstants.CENTER);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /** Fondo de tablones de madera vertical, con vetas horizontales. */
    static void dibujarFondoMadera(Graphics2D g, int w, int h) {
        g.setPaint(new GradientPaint(0, 0, MADERA_CLARA, 0, h, MADERA_OSCURA));
        g.fillRect(0, 0, w, h);
        g.setColor(MADERA_VETA);
        for (int y = 22; y < h; y += 26) {
            g.drawLine(0, y, w, y);
        }
    }

    /**
     * Marco de piedra (estilo mazmorra) alrededor de un hueco rectangular: solo
     * pinta el anillo entre el rectangulo exterior y el interior, con un patron
     * de ladrillos y un borde mas oscuro en ambos bordes.
     */
    static void dibujarMarcoPiedra(Graphics2D g, int x, int y, int w, int h, int grosor) {
        Rectangle exterior = new Rectangle(x, y, w, h);
        Rectangle interior = new Rectangle(x + grosor, y + grosor, w - 2 * grosor, h - 2 * grosor);
        Area marco = new Area(exterior);
        marco.subtract(new Area(interior));

        g.setColor(PIEDRA_CLARA);
        g.fill(marco);

        Shape clipPrevio = g.getClip();
        g.clip(marco);
        g.setColor(MORTERO);
        g.setStroke(new BasicStroke(1.5f));
        int bloque = Math.max(16, grosor);
        int fila = 0;
        for (int yy = y - bloque; yy < y + h + bloque; yy += bloque, fila++) {
            int desplazamiento = (fila % 2 == 0) ? 0 : bloque / 2;
            for (int xx = x - bloque + desplazamiento; xx < x + w + bloque; xx += bloque) {
                g.drawRect(xx, yy, bloque, bloque);
            }
        }
        g.setClip(clipPrevio);

        g.setColor(PIEDRA_OSCURA);
        g.setStroke(new BasicStroke(2f));
        g.draw(exterior);
        g.draw(interior);
    }
}
