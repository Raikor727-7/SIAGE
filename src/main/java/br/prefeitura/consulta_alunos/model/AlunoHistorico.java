package br.prefeitura.consulta_alunos.model;

import java.time.LocalDate;

public class AlunoHistorico {
    private String nome;
    private LocalDate dataNascimento;
    private String caixa;
    private String anoReferencia;
    private String situacao;

    public AlunoHistorico() {}

    public AlunoHistorico(String nome, LocalDate dataNascimento, String caixa, String anoReferencia, String situacao) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.caixa = caixa;
        this.anoReferencia = anoReferencia;
        this.situacao = situacao;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCaixa() { return caixa; }
    public void setCaixa(String caixa) { this.caixa = caixa; }

    public String getAnoReferencia() { return anoReferencia; }
    public void setAnoReferencia(String anoReferencia) { this.anoReferencia = anoReferencia; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}