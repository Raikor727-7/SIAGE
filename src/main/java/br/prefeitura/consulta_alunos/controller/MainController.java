package br.prefeitura.consulta_alunos.controller;

import br.prefeitura.consulta_alunos.model.Aluno;
import br.prefeitura.consulta_alunos.model.Boletim;
import br.prefeitura.consulta_alunos.service.AlunoDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    @FXML private TableView<Aluno> tabelaAlunos;
    @FXML private TableColumn<Aluno, Integer> colMatricula;
    @FXML private TableColumn<Aluno, String> colNome;
    @FXML private TableColumn<Aluno, String> colTurma;
    @FXML private TableColumn<Aluno, Integer> colIdade;
    @FXML private TableColumn<Aluno, String> colTelefone;
    @FXML private TableColumn<Aluno, String> colEmail;
    @FXML private TextField campoBusca;

    private ObservableList<Aluno> listaOriginal = FXCollections.observableArrayList();
    private AlunoDataService service = new AlunoDataService();

    @FXML
    public void initialize() {
        colMatricula.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getMatricula()));
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        colTurma.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTurma()));
        colIdade.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getIdade()));
        colTelefone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTelefone()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        tabelaAlunos.setRowFactory(tv -> {
            TableRow<Aluno> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Aluno aluno = row.getItem();
                    abrirDetalhesAluno(aluno);
                }
            });
            return row;
        });

    }

    @FXML
    public void selecionarPasta() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Selecione a pasta com as planilhas de alunos");
        File pasta = chooser.showDialog(new Stage());

        if (pasta != null) {
            List<Aluno> alunos = service.carregarAlunosDePasta(pasta.getAbsolutePath());
            listaOriginal.setAll(alunos);
            tabelaAlunos.setItems(listaOriginal);
        }
    }

    @FXML
    public void filtrarAlunos() {
        String filtro = campoBusca.getText().toLowerCase().trim();
        if (filtro.isEmpty()) {
            tabelaAlunos.setItems(listaOriginal);
        } else {
            tabelaAlunos.setItems(listaOriginal.filtered(a ->
                    a.getNome().toLowerCase().contains(filtro)
                            || a.getTurma().toLowerCase().contains(filtro)
                            || a.getNomeMae().toLowerCase().contains(filtro)
                            || a.getNomePai().toLowerCase().contains(filtro)
            ));
        }
    }
    private void abrirDetalhesAluno(Aluno aluno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/aluno-detalhes.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Detalhes do Aluno");

            AlunoDetalhesController controller = loader.getController();

            List<Boletim> boletinsDoAluno = service.carregarBoletimDoAluno(aluno)
                    .stream()
                    .filter(b -> b.getMatricula() == aluno.getMatricula())
                    .collect(Collectors.toList());

            int totalFaltas = service.contarFaltasDoAluno(aluno);

            controller.setAluno(aluno, boletinsDoAluno, totalFaltas);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir detalhes do aluno").show();
        }
    }

}

