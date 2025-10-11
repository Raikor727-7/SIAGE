package br.prefeitura.consulta_alunos.service;

import br.prefeitura.consulta_alunos.model.Aluno;
import br.prefeitura.consulta_alunos.model.Boletim;

import java.io.File;
import java.util.*;

public class AlunoDataService {

    private ExcelReader excelReader = new ExcelReader();

    public List<Aluno> carregarAlunosDePasta(String caminhoPasta) {
        List<Aluno> todosAlunos = new ArrayList<>();

        File pasta = new File(caminhoPasta);
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("❌ Pasta inválida: " + caminhoPasta);
            return todosAlunos;
        }

        // percorre todos os arquivos da pasta
        File[] arquivos = pasta.listFiles((dir, nome) ->
                nome.toLowerCase().endsWith(".xls") || nome.toLowerCase().endsWith(".xlsx"));

        if (arquivos == null || arquivos.length == 0) {
            System.out.println("⚠️ Nenhum arquivo Excel encontrado na pasta.");
            return todosAlunos;
        }

        for (File arquivo : arquivos) {
            System.out.println("📖 Lendo: " + arquivo.getName());
            List<Map<String, String>> linhas = excelReader.lerArquivo(arquivo);

            // tenta identificar se é uma planilha de alunos
            if (linhas.isEmpty()) continue;
            Map<String, String> primeira = linhas.get(0);
            if (!primeira.containsKey("Nome") || !primeira.containsKey("Turma")) {
                System.out.println("⏭️ Ignorando (não parece ser lista de alunos): " + arquivo.getName());
                continue;
            }

            // converte linhas em objetos Aluno
            for (Map<String, String> linha : linhas) {
                try {
                    int matricula = parseIntSafe(linha.get("Matricula"));
                    String nome = linha.getOrDefault("Nome", "");
                    String nomePai = linha.getOrDefault("Pai", "");
                    String nomeMae = linha.getOrDefault("Mãe", "");
                    int idade = parseIntSafe(linha.get("Idade"));
                    String turma = linha.getOrDefault("Turma", "");
                    String endereco = linha.getOrDefault("Endereço", "");
                    String telefone = linha.getOrDefault("Telefone", "");
                    String email = linha.getOrDefault("Email", "");

                    Aluno a = new Aluno(matricula, nome, nomePai, nomeMae, idade,
                            turma, endereco, telefone, email);
                    a.setPastaOrigem(caminhoPasta);
                    todosAlunos.add(a);
                } catch (Exception e) {
                    System.out.println("⚠️ Erro ao ler linha: " + e.getMessage());
                }
            }
        }

        System.out.println("✅ Total de alunos carregados: " + todosAlunos.size());
        return todosAlunos;
    }

    public List<Boletim> carregarBoletimDoAluno(Aluno aluno) {
        List<Boletim> boletins = new ArrayList<>();
        File pasta = new File(aluno.getPastaOrigem());
        if (!pasta.exists() || !pasta.isDirectory()) return boletins;

        File[] arquivos = pasta.listFiles((dir, nome) ->
                nome.toLowerCase().contains("boletim") && (nome.endsWith(".xls") || nome.endsWith(".xlsx")));

        if (arquivos == null || arquivos.length == 0) return boletins;

        for (File arquivo : arquivos) {
            List<Map<String, String>> linhas = excelReader.lerArquivo(arquivo);

            for (Map<String, String> linha : linhas) {
                int matricula = parseIntSafe(linha.get("Matricula"));
                if (matricula == aluno.getMatricula()) {
                    try {
                        String nomeAluno = linha.getOrDefault("Nome", "");
                        String disciplina = linha.getOrDefault("Disciplina", "");
                        double nota1 = parseDoubleSafe(linha.get("1º Bimestre"));
                        double nota2 = parseDoubleSafe(linha.get("2º Bimestre"));
                        double nota3 = parseDoubleSafe(linha.get("3º Bimestre"));
                        double nota4 = parseDoubleSafe(linha.get("4º Bimestre"));
                        double media = parseDoubleSafe(linha.get("Média Final"));
                        String situacao = linha.getOrDefault("Situação", "");

                        boletins.add(new Boletim(matricula, nomeAluno, disciplina,
                                nota1, nota2, nota3, nota4, media, situacao));
                    } catch (Exception e) {
                        System.out.println("Erro ao converter linha: " + e.getMessage());
                    }
                }
            }
        }
        return boletins;
    }

    private double parseDoubleSafe(String valor) {
        try {
            return (valor == null || valor.isBlank()) ? 0 : Double.parseDouble(valor.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public int contarFaltasDoAluno(Aluno aluno) {
        int faltas = 0;
        File pasta = new File(aluno.getPastaOrigem());
        if (!pasta.exists() || !pasta.isDirectory()) return faltas;

        File[] arquivos = pasta.listFiles((dir, nome) ->
                nome.toLowerCase().contains("frequencia") && (nome.endsWith(".xls") || nome.endsWith(".xlsx")));

        if (arquivos == null || arquivos.length == 0) return faltas;

        for (File arquivo : arquivos) {
            List<Map<String, String>> linhas = excelReader.lerArquivo(arquivo);

            int matriculaAtual = -1;
            for (Map<String, String> linha : linhas) {
                int matricula = parseIntSafe(linha.get("Matricula"));
                String status = linha.getOrDefault("Status", "");

                // só considera linhas do aluno
                if (matricula == aluno.getMatricula()) {
                    if ("Faltou".equalsIgnoreCase(status)) {
                        faltas++;
                    }
                }
            }
        }
        return faltas;
    }



    private int parseIntSafe(String valor) {
        try {
            return (valor == null || valor.isBlank()) ? 0 : Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
