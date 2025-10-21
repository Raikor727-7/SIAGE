package br.prefeitura.consulta_alunos.service;

import br.prefeitura.consulta_alunos.model.AlunoCaixa;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class PlanilhaCaixaReader {

    public List<AlunoCaixa> lerPlanilhaCaixas(File arquivoPlanilha) {
        List<AlunoCaixa> alunos = new ArrayList<>();
        String caixaAtual = "";
        int numeroCaixaAtual = 0;

        try (FileInputStream fis = new FileInputStream(arquivoPlanilha);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell primeiraCelula = row.getCell(0);

                if (primeiraCelula == null) continue;

                String valor = obterValorCelula(primeiraCelula).trim();

                // Detecta se é linha de caixa: "caixa [numero]"
                if (valor.toLowerCase().startsWith("caixa")) {
                    caixaAtual = extrairNumeroCaixa(valor);
                    numeroCaixaAtual = extrairApenasNumero(caixaAtual);
                    System.out.println("📦 Encontrada: " + caixaAtual);
                    continue;
                }

                // Detecta se é linha de aluno: "nome ano"
                if (!caixaAtual.isEmpty() && !valor.isEmpty() && !valor.toLowerCase().startsWith("caixa")) {
                    String[] partes = valor.split("\\s+", 2); // Divide no primeiro espaço
                    if (partes.length >= 2) {
                        String nome = partes[0].trim();
                        String anoSerie = partes[1].trim();

                        AlunoCaixa aluno = new AlunoCaixa(
                                gerarIdUnico(),
                                nome,
                                anoSerie,
                                caixaAtual,
                                "Migrado da planilha",
                                numeroCaixaAtual * 1000 + alunos.size() // ID temporário
                        );

                        alunos.add(aluno);
                        System.out.println("👤 " + nome + " | " + anoSerie + " | " + caixaAtual);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao ler planilha: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("✅ Total de alunos importados: " + alunos.size());
        return alunos;
    }

    private String extrairNumeroCaixa(String textoCaixa) {
        // Extrai "Caixa 1" de "caixa [1]" ou variações
        textoCaixa = textoCaixa.replace("[", "").replace("]", "").replace("(", "").replace(")", "");
        String[] partes = textoCaixa.split("\\s+");

        for (int i = 0; i < partes.length; i++) {
            if (partes[i].matches("\\d+")) {
                return "Caixa " + partes[i];
            }
        }

        // Fallback: pega qualquer número no texto
        String numero = textoCaixa.replaceAll("\\D+", "");
        return numero.isEmpty() ? "Caixa Desconhecida" : "Caixa " + numero;
    }

    private int extrairApenasNumero(String textoCaixa) {
        String numero = textoCaixa.replaceAll("\\D+", "");
        return numero.isEmpty() ? 0 : Integer.parseInt(numero);
    }

    private String obterValorCelula(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    // Converte número para string sem decimais se for inteiro
                    double num = cell.getNumericCellValue();
                    if (num == (int) num) {
                        yield String.valueOf((int) num);
                    } else {
                        yield String.valueOf(num);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private int gerarIdUnico() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}