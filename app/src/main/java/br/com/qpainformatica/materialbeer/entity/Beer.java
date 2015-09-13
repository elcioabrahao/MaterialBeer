package br.com.qpainformatica.materialbeer.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by elcio on 01/08/15.
 */
public class Beer  implements Comparable<Beer>, Serializable {


    private String nome;
    private String estilo;
    private String cor;
    private String pais;
    private String imagem;
    private double preco;
    private Drawable icon = null;

    public Beer() {
    }

    public Beer(String nome, String estilo, String cor, String pais, String imagem, double preco) {
        this.nome = nome;
        this.estilo = estilo;
        this.cor = cor;
        this.pais = pais;
        this.imagem = imagem;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public int compareTo(Beer o) {
        Beer f = o;

        return getNome().compareTo(f.getNome());
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
