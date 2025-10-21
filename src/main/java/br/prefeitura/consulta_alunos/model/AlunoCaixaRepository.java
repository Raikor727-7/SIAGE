package br.prefeitura.consulta_alunos.service;

import br.prefeitura.consulta_alunos.model.AlunoCaixa;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlunoCaixaRepository {
    private static final String ARQUIVO_DADOS = "dados/alunos_caixas.json";
    private List<AlunoCaixa> alunos;
    private Gson gson = new Gson();

    public AlunoCaixaRepository() {
        carregarDados();
    }

    public List<AlunoCaixa> listarTodos() {
        return new ArrayList<>(alunos);
    }

    public List<AlunoCaixa> buscarPorNome(String nome) {
        List<AlunoCaixa> resultados = new ArrayList<>();
        String nomeLower = nome.toLowerCase();

        for (AlunoCaixa aluno : alunos) {
            if (aluno.getNome().toLowerCase().contains(nomeLower)) {
                resultados.add(aluno);
            }
        }
        return resultados;
    }

    public AlunoCaixa buscarPorMatricula(int matricula) {
        for (AlunoCaixa aluno : alunos) {
            if (aluno.getMatricula() == matricula) {
                return aluno;
            }
        }
        return null;
    }

    public void atualizarCaixa(int id, String novaCaixa) {
        for (AlunoCaixa aluno : alunos) {
            if (aluno.getId() == id) {
                aluno.setCaixa(novaCaixa);
                salvarDados();
                break;
            }
        }
    }

    public void adicionarAluno(AlunoCaixa novoAluno) {
        // Gerar ID único
        int novoId = alunos.stream().mapToInt(AlunoCaixa::getId).max().orElse(0) + 1;
        novoAluno = new AlunoCaixa(novoId, novoAluno.getNome(), novoAluno.getSerie(),
                novoAluno.getCaixa(), novoAluno.getObservacoes(),
                novoAluno.getMatricula());
        alunos.add(novoAluno);
        salvarDados();
    }

    private void carregarDados() {
        try {
            File arquivo = new File(ARQUIVO_DADOS);
            if (!arquivo.exists()) {
                alunos = new ArrayList<>();
                // Criar dados iniciais se não existir
                inicializarDadosExemplo();
                return;
            }

            Reader reader = new FileReader(arquivo);
            Type listType = new TypeToken<ArrayList<AlunoCaixa>>(){}.getType();
            alunos = gson.fromJson(reader, listType);
            reader.close();

            if (alunos == null) {
                alunos = new ArrayList<>();
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao carregar dados: " + e.getMessage());
            alunos = new ArrayList<>();
        }
    }

    private void salvarDados() {
        try {
            File pasta = new File("dados");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            Writer writer = new FileWriter(ARQUIVO_DADOS);
            gson.toJson(alunos, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar dados: " + e.getMessage());
        }
    }

    private void inicializarDadosExemplo() {
        // Dados de exemplo para teste
        alunos.add(new AlunoCaixa(1, "Ana Silva", "7ºA", "Caixa 3", "Documentos completos", 1001));
        alunos.add(new AlunoCaixa(2, "João Santos", "8ºB", "Caixa 1", "Faltando histórico", 1002));
        alunos.add(new AlunoCaixa(3, "Maria Oliveira", "6ºC", "Caixa 2", "Todos documentos OK", 1003));
        salvarDados();
    }
}