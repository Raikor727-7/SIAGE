package br.prefeitura.consulta_alunos.controller;

import br.prefeitura.consulta_alunos.model.Aluno;
import br.prefeitura.consulta_alunos.model.Boletim;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class AlunoDetalhesController {

    @FXML private Label lblNome, lblMatricula, lblTurma, lblFaltas;
    @FXML private TableView<Boletim> tabelaBoletim;
    @FXML private TableColumn<Boletim, String> colDisciplina, colSituacao;
    @FXML private TableColumn<Boletim, Double> colB1, colB2, colB3, colB4, colMedia;

    private Aluno aluno;

    @FXML
    public void initialize() {
        colDisciplina.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDisciplina()));
        colB1.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getB1()));
        colB2.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getB2()));
        colB3.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getB3()));
        colB4.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getB4()));
        colMedia.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getMediaFinal()));
        colSituacao.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getSituacao()));
    }

    public void setAluno(Aluno aluno, List<Boletim> boletins, int totalFaltas) {
        this.aluno = aluno;
        lblNome.setText(aluno.getNome());
        lblMatricula.setText(String.valueOf(aluno.getMatricula()));
        lblTurma.setText(aluno.getTurma());
        lblFaltas.setText(String.valueOf(totalFaltas));
        tabelaBoletim.setItems(FXCollections.observableArrayList(boletins));
    }

    @FXML
    private void fecharJanela() {
        Stage stage = (Stage) lblNome.getScene().getWindow();
        stage.close();
    }
}
