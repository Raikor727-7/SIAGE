package br.prefeitura.consulta_alunos.service;

import br.prefeitura.consulta_alunos.model.AlunoHistorico;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlunoHistoricoService {

    public List<AlunoHistorico> carregarAlunosDeArquivo(String caminhoArquivo) throws Exception {
        List<AlunoHistorico> alunos = new ArrayList<>();

        FileInputStream file = new FileInputStream(new File(caminhoArquivo));
        Workbook workbook;

        if (caminhoArquivo.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file);
        } else {
            workbook = new HSSFWorkbook(file);
        }

        Sheet sheet = workbook.getSheetAt(0);
        String caixaAtual = "";
        String anoAtual = "";

        for (Row row : sheet) {
            // Pular linhas vazias
            if (row.getPhysicalNumberOfCells() == 0) continue;

            Cell primeiraCelula = row.getCell(0);
            if (primeiraCelula == null) continue;

            String valorCelula = getCellStringValue(primeiraCelula).trim();

            // Identificar cabeçalhos de caixa
            if (valorCelula.contains("CAIXA") || valorCelula.contains("ARQUIVO DO COLÉGIO")) {
                caixaAtual = valorCelula;
                // Tentar extrair ano do título da caixa
                anoAtual = extrairAnoDoTexto(valorCelula);
                continue;
            }

            // Pular linhas de cabeçalho
            if (valorCelula.equals("NOME") || valorCelula.contains("DT. NASCIMENTO")) {
                continue;
            }

            // Processar linha de aluno (tem nome na primeira coluna e data na segunda)
            if (row.getPhysicalNumberOfCells() >= 2 &&
                    !valorCelula.isEmpty() &&
                    !valorCelula.equals("ARQUIVO DO COLÉGIO JOÃO VIEIRA BEZERRA")) {

                String nome = valorCelula;
                String dataNascStr = getCellStringValue(row.getCell(1));
                String situacao = row.getPhysicalNumberOfCells() >= 3 ?
                        getCellStringValue(row.getCell(2)) : null;

                LocalDate dataNascimento = parseData(dataNascStr);

                AlunoHistorico aluno = new AlunoHistorico(
                        nome, dataNascimento, caixaAtual, anoAtual, situacao
                );
                alunos.add(aluno);
            }
        }

        workbook.close();
        file.close();
        return alunos;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private LocalDate parseData(String dataStr) {
        if (dataStr == null || dataStr.isEmpty()) return null;

        try {
            // Tenta parse no formato yyyy-MM-dd
            return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e1) {
            try {
                // Tenta outros formatos comuns
                return LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private String extrairAnoDoTexto(String texto) {
        // Extrai anos do texto (ex: "2007", "2013")
        if (texto.matches(".*\\b(19|20)\\d{2}\\b.*")) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b(19|20)\\d{2}\\b").matcher(texto);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return "";
    }
}