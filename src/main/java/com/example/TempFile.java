package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class TempFile {
    private int id, narq;
    private Path tempDir;

    public TempFile(int id) {
        this.id = id;
        narq = 1;
        String nome = "gravacao" + id + "-";
        try {
            tempDir = Files.createTempDirectory(nome);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File criaArquivoTemp(){
        try {
            Path arq = Files.createTempFile(tempDir, "gravacao" + id + "N" + narq + "-", ".wav");
            narq++;
            return arq.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNArq() {
        return narq;
    }

    public ArrayList<String> getCaminho() {
        ArrayList<String> nomeTempFileHandler = new ArrayList<>();
        File[] arquivos = tempDir.toFile().listFiles();
        for (File arquivo : arquivos) {
            nomeTempFileHandler.add(arquivo.getPath());
        }
        return nomeTempFileHandler;
    }

    public void limpar() {
        File arquivos[] = tempDir.toFile().listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                arquivo.delete();
            }
        }
        tempDir.toFile().delete();
    }
}
