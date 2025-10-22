package br.prefeitura.consulta_alunos.controller;

import br.prefeitura.consulta_alunos.model.AlunoHistorico;
import br.prefeitura.consulta_alunos.service.AlunoHistoricoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

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
            // Criar uma janela simples com os detalhes do aluno histórico
            Stage stage = new Stage();
            stage.setTitle("Detalhes do Aluno - " + aluno.getNome());

            TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setText(gerarDetalhesAluno(aluno));

            Scene scene = new Scene(new ScrollPane(textArea), 500, 400);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao abrir detalhes do aluno");
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