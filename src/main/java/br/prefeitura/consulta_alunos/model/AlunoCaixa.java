package br.prefeitura.consulta_alunos.model;

public class AlunoCaixa {
    private int id;
    private String nome;
    private String serie;
    private String caixa;
    private String observacoes;
    private int matricula; // Link com sistema existente

    public AlunoCaixa(int id, String nome, String serie, String caixa, String observacoes, int matricula) {
        this.id = id;
        this.nome = nome;
        this.serie = serie;
        this.caixa = caixa;
        this.observacoes = observacoes;
        this.matricula = matricula;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getSerie() { return serie; }
    public String getCaixa() { return caixa; }
    public String getObservacoes() { return observacoes; }
    public int getMatricula() { return matricula; }

    public void setCaixa(String caixa) { this.caixa = caixa; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public void setSerie(String serie) { this.serie = serie; }
}