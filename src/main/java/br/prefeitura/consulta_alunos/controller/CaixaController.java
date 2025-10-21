package br.prefeitura.consulta_alunos.controller;

import br.prefeitura.consulta_alunos.model.AlunoCaixa;
import br.prefeitura.consulta_alunos.service.AlunoCaixaRepository;
import br.prefeitura.consulta_alunos.service.PlanilhaCaixaReader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class CaixaController {

    @FXML private TextField campoBusca;
    @FXML private TextField campoFiltroCaixa;
    @FXML private TableView<AlunoCaixa> tabelaResultados;
    @FXML private TableColumn<AlunoCaixa, String> colNome;
    @FXML private TableColumn<AlunoCaixa, String> colSerie;
    @FXML private TableColumn<AlunoCaixa, String> colCaixa;
    @FXML private TableColumn<AlunoCaixa, String> colObservacoes;

    @FXML private Label lblEstatisticas;

    private AlunoCaixaRepository repository = new AlunoCaixaRepository();
    private PlanilhaCaixaReader planilhaReader = new PlanilhaCaixaReader();

    @FXML
    public void initialize() {
        // Configurar colunas da tabela
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        colSerie.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSerie()));
        colCaixa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCaixa()));
        colObservacoes.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getObservacoes()));

        // Buscar automaticamente ao digitar
        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> filtrarAlunos());
        campoFiltroCaixa.textProperty().addListener((observable, oldValue, newValue) -> filtrarAlunos());

        // Carregar todos os alunos inicialmente
        carregarTodosAlunos();
        atualizarEstatisticas();
    }

    private void carregarTodosAlunos() {
        List<AlunoCaixa> todosAlunos = repository.listarTodos();
        tabelaResultados.setItems(FXCollections.observableArrayList(todosAlunos));
    }

    private void filtrarAlunos() {
        String termoNome = campoBusca.getText().trim().toLowerCase();
        String termoCaixa = campoFiltroCaixa.getText().trim().toLowerCase();

        List<AlunoCaixa> todosAlunos = repository.listarTodos();

        List<AlunoCaixa> resultados = todosAlunos.stream()
                .filter(aluno ->
                        (termoNome.isEmpty() || aluno.getNome().toLowerCase().contains(termoNome)) &&
                                (termoCaixa.isEmpty() || aluno.getCaixa().toLowerCase().contains(termoCaixa))
                )
                .collect(Collectors.toList());

        tabelaResultados.setItems(FXCollections.observableArrayList(resultados));
        atualizarEstatisticas();
    }

    @FXML
    private void importarPlanilha() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Planilha de Caixas");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Planilhas Excel", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
        );

        File arquivo = fileChooser.showOpenDialog(new Stage());
        if (arquivo != null) {
            try {
                System.out.println("📥 Importando planilha: " + arquivo.getName());

                List<AlunoCaixa> alunosImportados = planilhaReader.lerPlanilhaCaixas(arquivo);

                // Adicionar ao repositório
                for (AlunoCaixa aluno : alunosImportados) {
                    repository.adicionarAluno(aluno);
                }

                carregarTodosAlunos();
                atualizarEstatisticas();

                mostrarAlerta("✅ Importação concluída! \n" +
                        "Alunos importados: " + alunosImportados.size());

            } catch (Exception e) {
                mostrarAlerta("❌ Erro na importação: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void listarPorCaixa() {
        String numeroCaixa = campoFiltroCaixa.getText().trim();
        if (numeroCaixa.isEmpty()) {
            carregarTodosAlunos();
            return;
        }

        List<AlunoCaixa> todosAlunos = repository.listarTodos();
        List<AlunoCaixa> alunosCaixa = todosAlunos.stream()
                .filter(aluno -> aluno.getCaixa().toLowerCase().contains(numeroCaixa.toLowerCase()))
                .collect(Collectors.toList());

        tabelaResultados.setItems(FXCollections.observableArrayList(alunosCaixa));
        atualizarEstatisticas();
    }

    @FXML
    private void editarCaixa() {
        AlunoCaixa alunoSelecionado = tabelaResultados.getSelectionModel().getSelectedItem();
        if (alunoSelecionado == null) {
            mostrarAlerta("Selecione um aluno para editar a caixa!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(alunoSelecionado.getCaixa());
        dialog.setTitle("Editar Caixa");
        dialog.setHeaderText("Editando caixa de: " + alunoSelecionado.getNome());
        dialog.setContentText("Nova caixa:");

        dialog.showAndWait().ifPresent(novaCaixa -> {
            if (!novaCaixa.trim().isEmpty()) {
                repository.atualizarCaixa(alunoSelecionado.getId(), novaCaixa.trim());
                filtrarAlunos();
                mostrarAlerta("✅ Caixa atualizada com sucesso!");
            }
        });
    }

    @FXML
    private void adicionarAluno() {
        // (Mantido o mesmo código do anterior)
        Dialog<AlunoCaixa> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Aluno");
        dialog.setHeaderText("Cadastrar novo aluno no sistema de caixas");

        ButtonType btnAdicionar = new ButtonType("Adicionar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAdicionar, ButtonType.CANCEL);

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome completo");
        TextField campoSerie = new TextField();
        campoSerie.setPromptText("Série/Turma");
        TextField campoCaixa = new TextField();
        campoCaixa.setPromptText("Número da caixa");
        TextField campoObservacoes = new TextField();
        campoObservacoes.setPromptText("Observações");

        dialog.getDialogPane().setContent(new javafx.scene.layout.VBox(10,
                new Label("Nome:"), campoNome,
                new Label("Série:"), campoSerie,
                new Label("Caixa:"), campoCaixa,
                new Label("Observações:"), campoObservacoes
        ));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAdicionar) {
                return new AlunoCaixa(0, campoNome.getText(), campoSerie.getText(),
                        campoCaixa.getText(), campoObservacoes.getText(), 0);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(novoAluno -> {
            repository.adicionarAluno(novoAluno);
            filtrarAlunos();
            mostrarAlerta("✅ Aluno adicionado com sucesso!");
        });
    }

    @FXML
    private void exportarBackup() {
        try {
            List<AlunoCaixa> todosAlunos = repository.listarTodos();
            java.io.FileWriter writer = new java.io.FileWriter("backup_caixas_" +
                    java.time.LocalDate.now() + ".csv");

            writer.write("Nome;Série;Caixa;Observações\n");
            for (AlunoCaixa aluno : todosAlunos) {
                writer.write(String.format("%s;%s;%s;%s\n",
                        aluno.getNome(), aluno.getSerie(), aluno.getCaixa(),
                        aluno.getObservacoes()));
            }
            writer.close();

            mostrarAlerta("✅ Backup exportado com sucesso!\nArquivo: backup_caixas_" +
                    java.time.LocalDate.now() + ".csv");
        } catch (Exception e) {
            mostrarAlerta("❌ Erro ao exportar backup: " + e.getMessage());
        }
    }

    private void atualizarEstatisticas() {
        List<AlunoCaixa> alunos = tabelaResultados.getItems();
        long totalCaixas = alunos.stream()
                .map(AlunoCaixa::getCaixa)
                .distinct()
                .count();

        lblEstatisticas.setText(String.format("📊 Mostrando %d alunos | %d caixas diferentes",
                alunos.size(), totalCaixas));
    }

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SIAGE Caixas");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}