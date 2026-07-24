package com.sokoban.view;

import com.sokoban.controller.Controlador;
import com.sokoban.partida.EstadoJuego;
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * HUD lateral estilo "granja": una caja de pergamino con el nivel y las
 * estadisticas, y debajo una columna de botones de madera (deshacer, reiniciar,
 * opciones, salir al menu). Lee el estado a traves del controlador (fachada
 * Juego); las acciones de opciones/salir las recibe como callbacks porque son
 * responsabilidad de la ventana (dialogos, cambio de carta).
 */
public class PanelHud extends JPanel {

    private static final int ANCHO = 250;
    private static final int ALTO_BOTON = 52;

    private final transient Controlador controlador;

    private final JLabel etiquetaNivel = new JLabel();
    private final JLabel etiquetaMovimientos = new JLabel();
    private final JLabel etiquetaEmpujes = new JLabel();
    private final JLabel etiquetaUndos = new JLabel();
    private final JLabel etiquetaEnergia = new JLabel();
    private final JButton botonUndo;

    public PanelHud(Controlador controlador, Runnable alPedirOpciones, Runnable alSalirAlMenu) {
        this.controlador = controlador;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        setPreferredSize(new Dimension(ANCHO, 0));

        add(crearCajaTitulo());
        add(Box.createVerticalStrut(22));

        botonUndo = agregarBoton("↩ DESHACER", controlador::deshacer);
        agregarBoton("⟳ REINICIAR", controlador::reiniciar);
        agregarBoton("⚙ OPCIONES", alPedirOpciones);
        add(Box.createVerticalGlue());
        agregarBoton("✖ SALIR", alSalirAlMenu);

        refrescar();
    }

    private JButton agregarBoton(String texto, Runnable accion) {
        JButton boton = TemaStardew.crearBoton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(ANCHO - 28, ALTO_BOTON));
        boton.setPreferredSize(new Dimension(ANCHO - 28, ALTO_BOTON));
        boton.addActionListener(e -> accion.run());
        add(boton);
        add(Box.createVerticalStrut(12));
        return boton;
    }

    private JPanel crearCajaTitulo() {
        JPanel caja = new JPanel() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(TemaStardew.PERGAMINO_FONDO);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g.setColor(TemaStardew.DORADO);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        caja.setOpaque(false);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        caja.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.setMaximumSize(new Dimension(ANCHO - 28, 170));

        etiquetaNivel.setFont(new Font("SansSerif", Font.BOLD, 20));
        etiquetaNivel.setForeground(TemaStardew.DORADO);
        etiquetaNivel.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(etiquetaNivel);
        caja.add(Box.createVerticalStrut(10));

        Font fuenteStat = new Font("SansSerif", Font.PLAIN, 14);
        for (JLabel etiqueta : new JLabel[]{etiquetaMovimientos, etiquetaEmpujes, etiquetaUndos, etiquetaEnergia}) {
            etiqueta.setFont(fuenteStat);
            etiqueta.setForeground(TemaStardew.CREMA);
            etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
            caja.add(etiqueta);
        }
        return caja;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        TemaStardew.dibujarFondoMadera(g, getWidth(), getHeight());
        g.dispose();
    }

    public void refrescar() {
        if (controlador.getJuego() == null) {
            return;
        }
        EstadoJuego estado = controlador.getJuego().getEstadoJuego();
        etiquetaNivel.setText("Nivel " + controlador.getNumeroNivel());
        etiquetaMovimientos.setText("Movimientos: " + estado.getMovimientos());
        etiquetaEmpujes.setText("Empujes: " + estado.getEmpujes());
        etiquetaUndos.setText("Undos restantes: " + estado.getUndosDisponibles());

        // El boton refleja directamente el predicado del modelo (sin condicionales).
        botonUndo.setEnabled(estado.puedeUndo());

        var jugador = controlador.getJuego().getTablero().getJugador();
        etiquetaEnergia.setText("Energia: " + jugador.getEnergia() + "/" + jugador.getEnergiaMaxima());
    }
}
