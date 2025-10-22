package br.prefeitura.consulta_alunos.controller;

import br.prefeitura.consulta_alunos.model.AlunoHistorico;
import br.prefeitura.consulta_alunos.model.Declaracao;
import br.prefeitura.consulta_alunos.service.AlunoHistoricoService;
import br.prefeitura.consulta_alunos.service.BackupArquivo;
import br.prefeitura.consulta_alunos.service.DeclaracaoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private BackupArquivo backupService = new BackupArquivo();

    @FXML private TableView<AlunoHistorico> tabelaAlunos;
    @FXML private TableColumn<AlunoHistorico, String> colNome;
    @FXML private TableColumn<AlunoHistorico, String> colDataNascimento;
    @FXML private TableColumn<AlunoHistorico, String> colIdade;
    @FXML private TableColumn<AlunoHistorico, String> colCaixa;
    @FXML private TableColumn<AlunoHistorico, String> colAno;
    @FXML private TableColumn<AlunoHistorico, String> colSituacao;

    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> comboCaixa;
    @FXML private ComboBox<String> comboAno;
    @FXML private Label labelInfo;

    private ObservableList<AlunoHistorico> listaOriginal = FXCollections.observableArrayList();
    private ObservableList<AlunoHistorico> dadosFiltrados = FXCollections.observableArrayList();
    private AlunoHistoricoService service = new AlunoHistoricoService();

    @FXML
    public void initialize() {
        configurarTabela();
        configurarComboBoxes();
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colDataNascimento.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDataNascimento() != null ?
                        data.getValue().getDataNascimento().toString() : "N/A"
        ));
        colIdade.setCellValueFactory(data -> new SimpleStringProperty(calcularIdade(data.getValue().getDataNascimento())));
        colCaixa.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCaixa()));
        colAno.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnoReferencia()));
        colSituacao.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSituacao()));

        // Configurar double-click para detalhes
        tabelaAlunos.setRowFactory(tv -> {
            TableRow<AlunoHistorico> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    AlunoHistorico aluno = row.getItem();
                    abrirDetalhesAluno(aluno);
                }
            });
            return row;
        });
    }

    private void configurarComboBoxes() {
        comboCaixa.setItems(FXCollections.observableArrayList());
        comboAno.setItems(FXCollections.observableArrayList());
    }

    @FXML
    public void selecionarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo Excel Histórico");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos Excel", "*.xls", "*.xlsx")
        );

        File arquivo = fileChooser.showOpenDialog(new Stage());
        if (arquivo != null) {
            carregarDadosExcel(arquivo);
        }
    }

    private void carregarDadosExcel(File arquivo) {
        try {

            String caminhoBackup = backupService.fazerBackup(arquivo.getAbsolutePath());
            System.out.println("Backup criado: " + caminhoBackup);

            List<AlunoHistorico> alunos = service.carregarAlunosDeArquivo(arquivo.getAbsolutePath());
            listaOriginal.setAll(alunos);
            tabelaAlunos.setItems(listaOriginal);

            atualizarFiltros();
            labelInfo.setText("Carregados " + listaOriginal.size() + " registros históricos do arquivo: " + arquivo.getName());

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao carregar arquivo: " + e.getMessage());
        }
    }

    private void atualizarFiltros() {
        // Atualizar combobox de caixas
        List<String> caixas = listaOriginal.stream()
                .map(AlunoHistorico::getCaixa)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        comboCaixa.setItems(FXCollections.observableArrayList(caixas));

        // Atualizar combobox de anos
        List<String> anos = listaOriginal.stream()
                .map(AlunoHistorico::getAnoReferencia)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        comboAno.setItems(FXCollections.observableArrayList(anos));
    }

    @FXML
    public void filtrarAlunos() {
        aplicarFiltrosCombinados();
    }

    @FXML
    private void filtrarPorCaixa() {
        aplicarFiltrosCombinados();
    }

    @FXML
    private void filtrarPorAno() {
        aplicarFiltrosCombinados();
    }

    private void aplicarFiltrosCombinados() {
        String filtroTexto = campoBusca.getText().toLowerCase().trim();
        String caixaSelecionada = comboCaixa.getValue();
        String anoSelecionado = comboAno.getValue();

        dadosFiltrados = listaOriginal.filtered(aluno -> {
            boolean passaFiltroTexto = filtroTexto.isEmpty() ||
                    aluno.getNome().toLowerCase().contains(filtroTexto);

            boolean passaFiltroCaixa = caixaSelecionada == null ||
                    caixaSelecionada.isEmpty() ||
                    caixaSelecionada.equals(aluno.getCaixa());

            boolean passaFiltroAno = anoSelecionado == null ||
                    anoSelecionado.isEmpty() ||
                    anoSelecionado.equals(aluno.getAnoReferencia());

            return passaFiltroTexto && passaFiltroCaixa && passaFiltroAno;
        });

        tabelaAlunos.setItems(dadosFiltrados);
        labelInfo.setText("Mostrando " + dadosFiltrados.size() + " de " + listaOriginal.size() + " registros");
    }

    @FXML
    private void limparFiltros() {
        campoBusca.clear();
        comboCaixa.getSelectionModel().clearSelection();
        comboAno.getSelectionModel().clearSelection();
        tabelaAlunos.setItems(listaOriginal);
        labelInfo.setText("Carregados " + listaOriginal.size() + " registros históricos");
    }

    private String calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) return "N/A";
        try {
            int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
            return idade >= 0 ? String.valueOf(idade) : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private void abrirDetalhesAluno(AlunoHistorico aluno) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Detalhes do Aluno - " + aluno.getNome());

            VBox vbox = new VBox(10);
            vbox.setPadding(new javafx.geometry.Insets(15));

            // Área de texto com detalhes
            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setText(gerarDetalhesAluno(aluno));
            textArea.setPrefHeight(300);

            // Botão para gerar declaração
            Button btnGerarDeclaracao = new Button("📄 Gerar Declaração");
            btnGerarDeclaracao.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
            btnGerarDeclaracao.setOnAction(e -> abrirFormularioDeclaracao(aluno));

            vbox.getChildren().addAll(
                    new Label("DETALHES DO ALUNO:"),
                    textArea,
                    btnGerarDeclaracao
            );

            Scene scene = new Scene(vbox, 500, 450);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir detalhes do aluno");
        }
    }

    private void abrirFormularioDeclaracao(AlunoHistorico aluno) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Gerar Declaração - " + aluno.getNome());

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20));

            // Campos do formulário
            TextField campoPai = new TextField();
            campoPai.setPromptText("Nome completo do pai");

            TextField campoMae = new TextField();
            campoMae.setPromptText("Nome completo da mãe");

            // USAR DatePicker EM VEZ DE DataField
            DatePicker campoDataNascimento = new DatePicker();
            campoDataNascimento.setPromptText("Data de nascimento");

            // Preencher com a data do aluno se existir
            if (aluno.getDataNascimento() != null) {
                campoDataNascimento.setValue(aluno.getDataNascimento());
            }

            TextField campoAno = new TextField();
            campoAno.setPromptText("Ex: 2024");

            TextField campoSerie = new TextField();
            campoSerie.setPromptText("Ex: 5ª série");

            // Adicionar ao grid
            grid.add(new Label("Aluno: " + aluno.getNome()), 0, 0, 2, 1);
            grid.add(new Label("Pai:"), 0, 1);
            grid.add(campoPai, 1, 1);
            grid.add(new Label("Mãe:"), 0, 2);
            grid.add(campoMae, 1, 2);
            grid.add(new Label("Data Nascimento:"), 0, 3);
            grid.add(campoDataNascimento, 1, 3);
            grid.add(new Label("Ano:"), 0, 4);
            grid.add(campoAno, 1, 4);
            grid.add(new Label("Série:"), 0, 5);
            grid.add(campoSerie, 1, 5);

            // Botões
            Button btnGerar = new Button("Gerar Declaração");
            Button btnCancelar = new Button("Cancelar");

            HBox botoes = new HBox(10, btnGerar, btnCancelar);
            botoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            grid.add(botoes, 0, 6, 2, 1);

            // Ações dos botões
            btnGerar.setOnAction(e -> {
                if (validarFormulario(campoAno, campoPai, campoMae, campoSerie)) {
                    try {
                        Declaracao declaracao = new Declaracao(
                                aluno.getNome(),
                                campoDataNascimento.getValue(), // Pode ser null
                                campoPai.getText().trim(),
                                campoMae.getText().trim(),
                                Integer.parseInt(campoAno.getText().trim()),
                                campoSerie.getText().trim()
                        );

                        gerarDocumentoDeclaracao(declaracao);
                        stage.close();

                    } catch (NumberFormatException ex) {
                        mostrarErro("Ano deve ser um número válido!");
                    }
                }
            });

            btnCancelar.setOnAction(e -> stage.close());

            Scene scene = new Scene(grid);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir formulário de declaração");
        }
    }

    private boolean validarFormulario(TextField campoAno, TextField... outrosCampos) {
        // Validar ano (deve ser número)
        try {
            Integer.parseInt(campoAno.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErro("Ano deve ser um número válido!");
            return false;
        }

        // Validar outros campos (não podem estar vazios)
        for (TextField campo : outrosCampos) {
            if (campo.getText().trim().isEmpty()) {
                mostrarErro("Todos os campos são obrigatórios!");
                return false;
            }
        }

        return true;
    }

    private void gerarDocumentoDeclaracao(Declaracao declaracao) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar Declaração");
            fileChooser.setInitialFileName("declaracao_" + declaracao.getNome().replace(" ", "_") + ".docx");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Documentos Word", "*.docx")
            );

            File arquivo = fileChooser.showSaveDialog(null);
            if (arquivo != null) {
                DeclaracaoService service = new DeclaracaoService();
                service.gerarDeclaracaoWord(declaracao, arquivo.getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Declaração Gerada");
                alert.setHeaderText(null);
                alert.setContentText("Declaração gerada com sucesso!\n\nArquivo: " + arquivo.getName());
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao gerar declaração: " + e.getMessage());
        }
    }

    private String gerarDetalhesAluno(AlunoHistorico aluno) {
        StringBuilder sb = new StringBuilder();
        sb.append("NOME: ").append(aluno.getNome()).append("\n\n");
        sb.append("DATA DE NASCIMENTO: ").append(aluno.getDataNascimento()).append("\n");
        sb.append("IDADE ATUAL: ").append(calcularIdade(aluno.getDataNascimento())).append(" anos\n\n");
        sb.append("CAIXA/ARQUIVO: ").append(aluno.getCaixa()).append("\n");
        sb.append("ANO DE REFERÊNCIA: ").append(aluno.getAnoReferencia()).append("\n");
        sb.append("SITUAÇÃO: ").append(aluno.getSituacao() != null ? aluno.getSituacao() : "Não informada").append("\n");

        return sb.toString();
    }

    // Mantendo os métodos existentes para compatibilidade

    @FXML
    private void abrirControleCaixas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/caixas-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("SIAGE - Controle de Caixas");
            stage.setWidth(800);
            stage.setHeight(600);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir controle de caixas");
        }
    }

    private void mostrarErro(String mensagem) {
        new Alert(Alert.AlertType.ERROR, mensagem).show();
    }
}