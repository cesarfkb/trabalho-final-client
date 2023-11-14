package com.example;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Gravador {
    private File arqFinal;
    private final AudioFileFormat.Type ARQUIVOTIPO = AudioFileFormat.Type.WAVE;
    private TargetDataLine linha;
    private String nome, nomeArq;
    private final TempFile arquivos;
    
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeBits = 16;
        int channel = 1;
        boolean signed = true;
        boolean bigEndian = true;

        return new AudioFormat(sampleRate, sampleSizeBits, channel, signed, bigEndian);
    }

    public Gravador(int id, String caminho, String nome) {
        arquivos = new TempFile(id);
        arqFinal = new File(caminho + "//" + nome + ".wav");
        nomeArq = nome;
    }

    public void inicio() {
        File arq = arquivos.criaArquivoTemp();
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                JOptionPane.showMessageDialog(null,"NAO EXISTEM MICROFONES DISPONIVEIS");
                return;
            }
            Mixer microfone = null;
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();
            for (Mixer.Info mixer : mixers) {
                if (mixer.getName().equals(nome)) {
                    microfone = AudioSystem.getMixer(mixer);
                }
            }

            if (microfone == null) {
                linha = (TargetDataLine) AudioSystem.getLine(info);
            } else {
                linha = (TargetDataLine) microfone.getLine(info);
            }
            linha.open(format);
            linha.start();

            //System.out.println("Iniciando gravação");

            AudioInputStream ais = new AudioInputStream(linha);

            AudioSystem.write(ais, ARQUIVOTIPO, arq);
            ais.close();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void terminar(int i) {
        if (linha.isRunning()) {
            //System.out.println("Pausando Gravação");
            linha.stop();
        }
        //System.out.println("Terminando Trecho de Gravação");
        linha.close();
        if (i == 1) {
            //System.out.println("Terminando Gravação");
            concatenar();
            arquivos.limpar();
        }
    }

    private void concatenar() {
        String[] caminhoArquivos = arquivos.getCaminho().toArray(new String[arquivos.getCaminho().size()]);
        AudioInputStream clipInicial = null;
        for (String caminho : caminhoArquivos) {
            try {
                if (clipInicial == null) {
                    clipInicial = AudioSystem.getAudioInputStream(new File(caminho));
                    //System.out.println("CLIP INICIAL CRIADO");
                    continue;
                }
                AudioInputStream clipAdicionar = AudioSystem.getAudioInputStream(new File(caminho));
                AudioInputStream clipConcatenado = new AudioInputStream(
                        new SequenceInputStream(clipInicial, clipAdicionar),
                        clipInicial.getFormat(),
                        clipInicial.getFrameLength() + clipAdicionar.getFrameLength()
                );
                clipInicial.close();
                clipInicial = clipConcatenado;
                clipConcatenado.close();
            } catch (UnsupportedAudioFileException | IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                continue;
            }
        }
        try {
            AudioSystem.write(clipInicial, ARQUIVOTIPO, arqFinal);
            clipInicial.close();
            arquivos.limpar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getMics() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        ArrayList<String> nomes = new ArrayList<>();
        for (Mixer.Info mixer : mixers) {
            if (mixer.getDescription().contains("Capture")) {
                nomes.add(mixer.getName());
            }
        }
        return nomes;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return this.nome;
    }

    public void setArqFinal(String caminho) {
        arqFinal = new File(caminho + "\\" + nomeArq + ".wav");
    }

    public void setNomeArq(String nome) {
        nomeArq = nome;
    }
}
