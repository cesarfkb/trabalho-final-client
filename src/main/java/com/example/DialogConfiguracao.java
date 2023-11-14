package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;

public class DialogConfiguracao extends JDialog implements ActionListener {

    private JComboBox<String> mics, idiomas;
    private JButton aplicar, cancelar, trocar;
    private JFileChooser local;
    private JLabel diretorioAtual;
    private String nome, caminho, caminhoAntigo;
    private int pos, optIdioma, idiomaInicial;
    private Gravador g;
    private boolean aplicado = false;
    private ResourceBundle idioma;
    private SocketClient cliente;

    public DialogConfiguracao(JFrame fr, Gravador g, int pos, String caminho, int optIdioma, ResourceBundle idioma, SocketClient cliente) {
        super(fr);
        this.caminho = caminho;
        this.pos = pos;
        this.g = g;
        this.idiomaInicial = optIdioma;
        this.optIdioma = optIdioma;
        this.idioma = idioma;
        this.cliente = cliente;
        idioma = Idioma.idiomasBundles[optIdioma];
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setTitle("Configurações");
        setSize(new Dimension(550, 200));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                aplicado = false;
                fechar();
            }
        });
        setLocationRelativeTo(null);
        iniciarElementos();
        pack();
    }

    private void iniciarElementos() {
        String[] microfones = g.getMics().toArray(new String[g.getMics().size()]);
        if (microfones.length == 0) {
            JOptionPane.showMessageDialog(null, "NÃO EXISTEM MICROFONES DISPONIVEIS");
            this.dispose();
            return;
        }
        mics = new JComboBox<>(microfones);
        mics.setSelectedIndex(pos);

        idiomas = new JComboBox<>(Idioma.idiomas);
        idiomas.setSelectedIndex(optIdioma);

        aplicar = new JButton(idioma.getString("config.aplicar"));
        aplicar.addActionListener(this);
        cancelar = new JButton(idioma.getString("config.cancelar"));
        cancelar.addActionListener(this);
        trocar = new JButton(idioma.getString("config.trocar"));
        trocar.addActionListener(this);

        local = new JFileChooser();
        local.setCurrentDirectory(new java.io.File(caminho));
        local.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        diretorioAtual = new JLabel(idioma.getString("config.diretorio") + local.getCurrentDirectory().getPath());
        diretorioAtual.setHorizontalAlignment(JLabel.CENTER);
        diretorioAtual.setFont(new Font(diretorioAtual.getFont().getName(), diretorioAtual.getFont().getStyle(), 11));
        caminhoAntigo = local.getCurrentDirectory().getPath();

        definirLayout();
    }

    private void definirLayout() {
        Container c = getContentPane();
        c.setLayout(new GridLayout(4, 1));

        JPanel painel1 = new JPanel(new FlowLayout());
        painel1.add(new JLabel(idioma.getString("config.idioma")));
        painel1.add(idiomas);
        c.add(painel1);

        JPanel painel2 = new JPanel(new FlowLayout());
        painel2.add(new JLabel(idioma.getString("config.microfone")));
        painel2.add(mics);
        c.add(painel2);

        JPanel painel3 = new JPanel(new GridLayout(2, 1));
        JPanel painel32 = new JPanel(new GridLayout(1, 1));
        painel3.add(diretorioAtual);
        painel32.add(trocar);
        painel32.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(
                2, 15, 2, 15),
                painel32.getBorder()));
        painel3.add(painel32);

        c.add(painel3);

        JPanel painel4 = new JPanel(new GridLayout(1, 2));
        JPanel painel41 = new JPanel(new GridLayout(1, 1));
        JPanel painel42 = new JPanel(new GridLayout(1, 1));
        painel41.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(
                5, 5, 5, 5),
                painel41.getBorder()));
        painel42.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(
                5, 5, 5, 5),
                painel42.getBorder()));
        painel41.add(aplicar);
        painel42.add(cancelar);

        painel4.add(painel41);
        painel4.add(painel42);

        c.add(painel4);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == trocar) {
            definirDiretorio(0);
        } else if (e.getSource() == aplicar) {
            g.setNome(mics.getItemAt(mics.getSelectedIndex()));
            pos = mics.getSelectedIndex();
            optIdioma = idiomas.getSelectedIndex();
            if (optIdioma != idiomaInicial) {
                JOptionPane.showMessageDialog(null, Idioma.idiomasBundles[optIdioma].getString("idioma.reiniciar"));
                mudarIdioma();
            }
            definirDiretorio(1);
            aplicado = true;
            fechar();
        } else if (e.getSource() == cancelar) {
            local.setCurrentDirectory(new File(caminhoAntigo));
            caminho = caminhoAntigo;
            fechar();
        }
    }

    private void mudarIdioma() {
        cliente.sendOneInt(7);
        cliente.sendOneInt(optIdioma);
    }

    private void definirDiretorio(int i) {
        if (i == 0) {
            int opt = local.showSaveDialog(this);
            if (opt == JFileChooser.APPROVE_OPTION) {
                diretorioAtual.setText(idioma.getString("config.diretorio") + local.getSelectedFile().getPath());
                caminhoAntigo = caminho;
                caminho = local.getSelectedFile().getPath();
            }
        }
        if (i == 1) {
            local.setCurrentDirectory(new File(caminho));
        }
    }

    public String getNome() {
        return nome;
    }

    public boolean isAplicado() {
        return aplicado;
    }

    public String getCaminho() {
        return caminho;
    }

    public int getPos() {
        return pos;
    }

    public void fechar() {
        this.dispose();
    }

    public int getOptIdioma() {
        return optIdioma;
    }
}
