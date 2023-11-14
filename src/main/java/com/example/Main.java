package com.example;


public class Main {
    public static void main(String[] args) {
        TelaLogin tl = new TelaLogin();
        System.out.println(tl.loginSucesso);
        if (tl.loginSucesso) {
            System.out.println("TESTE");
            TelaPrincipal tp = new TelaPrincipal(tl.usuario);
        } else {
            System.exit(0);
        }
    }
}
