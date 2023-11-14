package com.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

public class SocketClient {
    private final Socket socket;
    private InputStream entrada;
    private OutputStream saida;

    public SocketClient(final Socket socket) throws IOException {
        this.socket = socket;
        System.out.println("Conectado com: " + socket.getRemoteSocketAddress());
        entrada = new DataInputStream(socket.getInputStream());
        saida = new DataOutputStream(socket.getOutputStream());
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    public void close() {
        try {
            entrada.close();
            saida.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar socket: " + e.getMessage());
        }
    }

    public void sendData(byte[][] data, int length) throws IOException {
        saida.write(length);
        for (byte[] dataLinha : data ) {
            saida.write(dataLinha);
        }
    }

    public void sendData(byte[] data) throws IOException {
        saida.write((int) data[0] - '0');
        data = Arrays.copyOfRange(data, 1, data.length);
        saida.write(data.length);
        saida.write(data);
    }

    public void sendString(byte[] data) throws IOException {
        saida.write(data.length);
        saida.write(data);
    }

    public byte[] getString() throws IOException {
        int length = entrada.read();
        return entrada.readNBytes(length);
    }

    public void sendOneByte(byte data) throws IOException {
        saida.write(data);
    }

    public int getOneByte() throws IOException {
        return entrada.read();
    }

    public byte[] getData() throws IOException {
        int length = entrada.read();
        byte[] data = entrada.readNBytes(length);
        return data;
    }

    public int getOperation() {
        try {
            return entrada.read();
        } catch (IOException e) {
            System.out.println("Erro ao ler operacao: " + e.getMessage());
            return -1;
        }
    }

    public void sendOneInt(int i) {
        try {
            saida.write(i);
        } catch (IOException e) {
            System.out.println("Erro ao enviar inteiro: " + e.getMessage());
        }
    }

    public void limparInput() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void limparOutput() {
        try {
            saida = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
