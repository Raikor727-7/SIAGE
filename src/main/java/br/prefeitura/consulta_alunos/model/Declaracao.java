package br.prefeitura.consulta_alunos.model;

import java.time.LocalDate;

public class Declaracao {
    private String nome;
    private LocalDate dataNascimento;
    private String pai;
    private String mae;
    private int ano;
    private String serie;

    public Declaracao(String nome, LocalDate dataNascimento, String pai, String mae, int ano, String serie) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.pai = pai != null ? pai : "";
        this.mae = mae != null ? mae : "";
        this.ano = ano;
        this.serie = serie != null ? serie : "";
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getPai() { return pai; }
    public void setPai(String pai) { this.pai = pai != null ? pai : ""; }

    public String getMae() { return mae; }
    public void setMae(String mae) { this.mae = mae != null ? mae : ""; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie != null ? serie : ""; }
}