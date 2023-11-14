package com.example;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TelaAnotacao extends JFrame {
    private ArrayList<String> anotacoes;
    private HashMap<JLabel, String> labels;
    private int[] tempos, tempoTotal;
    private ResourceBundle idioma;
    private String labelString;
    private int id;

    public TelaAnotacao(int id, ResourceBundle idioma, SocketClient cliente) {
        super();

        this.idioma = idioma;
        this.id = id;
        labels = new HashMap<>();

        Object[] dados = pegarDadosGravacao(cliente);
        tempos = (int[]) dados[1];
        tempoTotal = RunnableContador.calculaTempo((Integer) dados[0]);
        anotacoes = (ArrayList<String>) dados[2];

        iniciarTela();
    }

    private Object[] pegarDadosGravacao(SocketClient cliente) {
        cliente.sendOneInt(2);
        cliente.sendOneInt(id);
        try {
            String tempoTotal = new String(cliente.getString());
            byte[] buffer = cliente.getData();
            
            byte[][] bufferSeparado = separarBuffer(buffer);
            String[] dados = new String[bufferSeparado.length];
            for (int i = 0; i < bufferSeparado.length; i++) {
                dados[i] = new String(bufferSeparado[i]);
            }
            int[] tempos = new int[dados.length];
            for (int i = 0; i < dados.length; i++) {
                tempos[i] = Integer.parseInt(dados[i]);
            }

            buffer = cliente.getData();
            bufferSeparado = separarBuffer(buffer);
            ArrayList<String> anotacoes = new ArrayList<>();
            for (byte[] bytes : bufferSeparado) {
                anotacoes.add(new String(bytes));
            }

            return new Object[] { Integer.parseInt(tempoTotal), tempos, anotacoes };

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[][] separarBuffer(byte[] buffer) {
        int tamanho = buffer[0];
        int indice = 1;
        if (tamanho == 1) {
            indice = 0;
        }
        byte[][] bufferSeparado = new byte[tamanho][];
        for (int j = 0; j < tamanho; j++) {
            int tamanhoArray = buffer[indice];
            bufferSeparado[j] = new byte[tamanhoArray];
            for (int k = 0; k < tamanhoArray; k++) {
                bufferSeparado[j][k] = buffer[indice + k + 1];
            }
            indice += tamanhoArray + 1;
        }
        System.out.println(bufferSeparado[0].toString());
        return bufferSeparado;
    }

    public void iniciarTela() {
        Container c = getContentPane();
        JPanel pTitulo = new JPanel(new GridLayout(2, 1));
        JPanel pAnotacoes = new JPanel(new GridLayout(tempos.length + 1, 1));

        JLabel labelGeral = new JLabel(idioma.getString("gravacao.nome") + " " + id);
        labelGeral.setHorizontalAlignment(JLabel.CENTER);
        labelGeral.setVerticalAlignment(JLabel.NORTH);
        pTitulo.add(labelGeral);

        String duracao = arrumarStringTempo(tempoTotal);
        labelGeral = new JLabel(idioma.getString("gravacao.duracao") + " " + duracao);
        labelGeral.setHorizontalAlignment(JLabel.CENTER);
        labelGeral.setVerticalAlignment(JLabel.NORTH);
        pTitulo.add(labelGeral);
        pAnotacoes.add(pTitulo);

        if (tempos.length == 1 && tempos[0] == -1) {
            labelGeral = new JLabel("NO NOTES");
            labelGeral.setHorizontalAlignment(JLabel.CENTER);
            pAnotacoes.add(labelGeral);
        } else {
            for (int j = 0; j < anotacoes.size(); j++) {
                duracao = arrumarStringTempo(RunnableContador.calculaTempo(tempos[j]));
                labelString = duracao + " - " + anotacoes.get(j);
                labelGeral = new JLabel(labelString);
                labels.put(labelGeral, labelString);
                labelGeral.setHorizontalAlignment(JLabel.CENTER);
                labelGeral.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(null, labelString);
                    }
                });
                pAnotacoes.add(labelGeral);
            }
        }

        JScrollPane jsp = new JScrollPane(pAnotacoes);
        c.setLayout(new GridLayout(1,1));
        c.add(jsp);
        this.setSize(400, 500);
        this.setVisible(true);

    }

    private String arrumarStringTempo(int[] tempo) {
        String duracao = "";
        if (tempo[0] >= 10) {
            duracao += tempo[0];
        } else {
            duracao += "0" + tempo[0];
        }
        duracao += " : ";
        if (tempo[1] >= 10) {
            duracao += tempo[1];
        } else {
            duracao += "0" + tempo[1];
        }
        duracao += " : ";
        if (tempo[2] >= 10) {
            duracao += tempo[2];
        } else {
            duracao += "0" + tempo[2];
        }
        return duracao;
    }
}
