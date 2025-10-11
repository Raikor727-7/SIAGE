package br.prefeitura.consulta_alunos;

import br.prefeitura.consulta_alunos.model.Aluno;
import br.prefeitura.consulta_alunos.service.AlunoDataService;
import java.util.List;

public class TesteLeitura {
    public static void main(String[] args) {
        AlunoDataService service = new AlunoDataService();
        List<Aluno> alunos = service.carregarAlunosDePasta("SIAGE\\escola_A");
        alunos.forEach(a -> System.out.println(a.getNome() + " - " + a.getTurma()));
    }
}
