package br.prefeitura.consulta_alunos.model;

public class Aluno {
    private int matricula;
    private String nome;
    private String nomePai;
    private String nomeMae;
    private int idade;
    private String turma;
    private String endereco;
    private String telefone;
    private String email;
    private String pastaOrigem;

    public Aluno(int matricula, String nome, String nomePai, String nomeMae, int idade, String turma,
                 String endereco, String telefone, String email){
        this.matricula = matricula;
        this.nome = nome;
        this.nomePai = nomePai;
        this.nomeMae = nomeMae;
        this.idade = idade;
        this.turma = turma;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

    public String getPastaOrigem() {
        return pastaOrigem;
    }

    public void setPastaOrigem(String pastaOrigem) {
        this.pastaOrigem = pastaOrigem;
    }

    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public String getEndereco() {return endereco;}
    public String getNomeMae() {return nomeMae;}
    public String getNomePai() {return nomePai;}
    public String getTurma() {return turma;}
    public String getTelefone() {return telefone;}
    public int getIdade() {return idade;}
    public int getMatricula() {return matricula;}

    public void setNome(String nome) {this.nome = nome;}
    public void setEmail(String email) {this.email = email;}
    public void setEndereco(String endereco) {this.endereco = endereco;}
    public void setNomeMae(String nomeMae) {this.nomeMae = nomeMae;}
    public void setNomePai(String nomePai) {this.nomePai = nomePai;}
    public void setTurma(String turma) {this.turma = turma;}
    public void setTelefone(String telefone) {this.telefone = telefone;}
    public void setIdade(int idade) {this.idade = idade;}
    public void setMatricula(int matricula) {this.matricula = matricula;}
}


//matrícula, nome, CPF, responsáveis, idade, turma, endereço, telefone e e-mail.