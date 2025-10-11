package br.prefeitura.consulta_alunos.model;

public class Frequencia {
    private int matricula;
    private String data;
    private String status; // "Presente" ou "Faltou"

    // construtor + getters

    public int getMatricula() {return matricula;}
    public String getData() {return data;}
    public String getStatus() {return status;}

    public void setData(String data) {this.data = data;}
    public void setMatricula(int matricula) {this.matricula = matricula;}
    public void setStatus(String status) {this.status = status;}
}
