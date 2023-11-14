package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DialogAbrirGravacao extends JDialog implements ActionListener {

    private final int qtd;
    private final HashMap<Integer, JButton> botoes;
    private ResourceBundle idioma;
    private SocketClient cliente;

    public DialogAbrirGravacao(JFrame fr, int qtd, ResourceBundle idioma, SocketClient cliente) {
        super(fr);
        this.qtd = qtd;
        this.cliente = cliente;
        botoes = new HashMap<>();
        this.idioma = idioma;
        
        iniciarElementos();
        definirLayout();
        setSize(new Dimension(150, 500));
        setVisible(true);
        setLocationRelativeTo(null);
        setTitle(idioma.getString("gravacao.titulo"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 1; i <= qtd; i++) {
            if (botoes.get(i).equals(e.getSource())) {
                pegarAnotacoes(i);
            }
        }
    }

    private void iniciarElementos() {
        for (int i = 1; i <= qtd; i++) {
            JButton botao = new JButton(idioma.getString("gravacao.nome") + " " + i);
            botao.addActionListener(this);
            botoes.put(i, botao);
        }
    }

    private void definirLayout() {
        Container c = getContentPane();
        c.setLayout(new GridLayout(1, 1));

        GridLayout layout = new GridLayout(qtd, 2);
        layout.setVgap(10);
        layout.setHgap(10);

        JPanel painel = new JPanel(layout);
        painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                painel.getBorder()));

        for (int i = 1; i < qtd; i++) {
            painel.add(botoes.get(i));
        }

        JScrollPane jsp = new JScrollPane(painel);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(jsp);
    }

    private void pegarAnotacoes(int i) {
        new TelaAnotacao(i, idioma, cliente);
    }
}
