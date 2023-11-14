package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
import java.util.ResourceBundle;

public class TelaLogin extends JDialog implements ActionListener {
    private JLabel lUser, lPassword;
    private JTextField tUser;
    private JPasswordField tPassword;
    private JButton bLogin, bRegister;
    private SocketClient client;
    private static final String ADDRESS = "localhost";
    private static final int PORT = 4000;
    public boolean loginSucesso = false;
    public String usuario = "";
    private int opt = 0;
    private ResourceBundle idioma;

    public TelaLogin() {
        super();

        Locale locale = Locale.getDefault();
        System.out.println(locale.getLanguage());
        for (int i = 0; i < Idioma.idiomasCodigos.length; i++) {
            System.out.println(Idioma.idiomasCodigos[i]);
            if (locale.getLanguage().equals(Idioma.idiomasCodigos[i])) {
                opt = i;
                break;
            }
        }
        idioma = Idioma.idiomasBundles[opt];

        iniciaCliente();

        iniciarElementos();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(DEFAULT_MODALITY_TYPE);
        setPreferredSize(new Dimension(300, 200));
        setSize(new Dimension(300, 200));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void iniciarElementos() {
        bLogin = new JButton(idioma.getString("login.entrar"));
        bRegister = new JButton(idioma.getString("login.registrar"));
        lUser = new JLabel(idioma.getString("login.usuario"));
        lPassword = new JLabel(idioma.getString("login.senha"));
        tUser = new JTextField(10);
        tPassword = new JPasswordField(10);

        bLogin.addActionListener(this);
        bRegister.addActionListener(this);

        definirLayout();
    }

    private void definirLayout() {
        Container c = getContentPane();
        c.setLayout(new BorderLayout(10, 10));

        JPanel pLogin = new JPanel(new GridLayout(2, 2, 10, 10));
        pLogin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pLogin.add(lUser);
        pLogin.add(tUser);
        pLogin.add(lPassword);
        pLogin.add(tPassword);

        JPanel pBotao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pBotao.add(bRegister);
        pBotao.add(bLogin);

        c.add(pLogin, BorderLayout.CENTER);
        c.add(pBotao, BorderLayout.SOUTH);
    }

    void iniciaCliente() {
        try {
            start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o cliente: " + e.getMessage());
        }
    }

    private void start() throws IOException {
        client = new SocketClient(new Socket(ADDRESS, PORT));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bLogin) {
            login();
        } else if (e.getSource() == bRegister) {
            registrar();
        }
    }

    private void login() {
        String user = tUser.getText();
        this.usuario = user;
        String password = new String(tPassword.getPassword());
        if (user.length() < 1 || password.length() < 1) {
            JOptionPane.showMessageDialog(null, idioma.getString("login.erro.vazio"));
            return;
        } else if (password.length() < 8) {
            JOptionPane.showMessageDialog(null, idioma.getString("login.erro.senha.tamanho"));
            return;
        }
        user = "3" + user + "+" + password;
        try {
            client.sendData(user.getBytes());
            respostaLogin();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void respostaLogin() {
        try {
            byte data = (byte) client.getOneByte();
            int bool = data - '0';
            if (bool == 1) {
                JOptionPane.showMessageDialog(null, idioma.getString("login.entrar.sucesso"));
                loginSucesso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, idioma.getString("login.erro.errado"));
                loginSucesso = false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registrar() {
        String user = tUser.getText();
        this.usuario = user;
        String password = new String(tPassword.getPassword());
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(null, idioma.getString("login.erro.senha.tamanho"));
            return;
        }
        if (user.matches("^[a-zA-Z0-9]*$")) {
            user = "4" + user + "+" + password;
            try {
                client.sendData(user.getBytes());
                respostaRegistro();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, idioma.getString("login.erro.caracteres"));
        }
    }

    private void respostaRegistro() {
        try {
            byte data = (byte) client.getOneByte();
            int bool = data - '0';
            if (bool == 1) {
                JOptionPane.showMessageDialog(null, idioma.getString("login.registrar.sucesso"));
                loginSucesso = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, idioma.getString("login.erro.registrar"));
                loginSucesso = false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
