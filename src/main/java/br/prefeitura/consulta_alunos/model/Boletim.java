package br.prefeitura.consulta_alunos.model;

public class Boletim {
    private int matricula;
    private String nome;
    private String disciplina;
    private double b1, b2, b3, b4, mediaFinal;
    private String situacao;



    public Boletim(int matricula, String nomeAluno, String disciplina,
                       double nota1, double nota2, double nota3, double nota4,
                       double mediaFinal, String situacao) {
        this.matricula = matricula;
        this.nome = nomeAluno;
        this.disciplina = disciplina;
        this.b1 = nota1;
        this.b2 = nota2;
        this.b3 = nota3;
        this.b4 = nota4;
        this.mediaFinal = mediaFinal;
        this.situacao = situacao;
    }

    // Getters
    public int getMatricula() { return matricula; }
    public String getNome() { return nome; }
    public String getDisciplina() { return disciplina; }
    public double getB1() { return b1; }
    public double getB2() { return b2; }
    public double getB3() { return b3; }
    public double getB4() { return b4; }
    public double getMediaFinal() { return mediaFinal; }
    public String getSituacao() { return situacao; }

    // Setters
    public void setMatricula(int matricula) { this.matricula = matricula; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
    public void setB1(double b1) { this.b1 = b1; }
    public void setB2(double b2) { this.b2 = b2; }
    public void setB3(double b3) { this.b3 = b3; }
    public void setB4(double b4) { this.b4 = b4; }
    public void setMediaFinal(double mediaFinal) { this.mediaFinal = mediaFinal; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}
