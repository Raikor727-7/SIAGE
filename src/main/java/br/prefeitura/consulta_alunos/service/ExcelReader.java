package br.prefeitura.consulta_alunos.service;

import br.prefeitura.consulta_alunos.model.Boletim;
import br.prefeitura.consulta_alunos.model.Frequencia;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {

    public List<Map<String, String>> lerArquivo(File arquivo) {
        List<Map<String, String>> dados = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = criarWorkbook(fis, arquivo.getName())) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 1️⃣ Lê cabeçalho (primeira linha)
            List<String> colunas = new ArrayList<>();
            if (rowIterator.hasNext()) {
                Row cabecalho = rowIterator.next();
                for (Cell celula : cabecalho) {
                    colunas.add(celula.getStringCellValue().trim());
                }
            }

            // 2️⃣ Lê o resto das linhas
            while (rowIterator.hasNext()) {
                Row linha = rowIterator.next();
                Map<String, String> registro = new HashMap<>();

                for (int i = 0; i < colunas.size(); i++) {
                    Cell celula = linha.getCell(i);
                    String valor = obterValorComoTexto(celula);
                    registro.put(colunas.get(i), valor);
                }

                dados.add(registro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dados;
    }

    public static List<Boletim> carregarBoletins(File arquivo) {
        List<Boletim> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = criarWorkbook(fis, arquivo.getName())) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Cabeçalho
            List<String> colunas = new ArrayList<>();
            if (rowIterator.hasNext()) {
                Row cabecalho = rowIterator.next();
                for (Cell celula : cabecalho) {
                    colunas.add(celula.getStringCellValue().trim());
                }
            }

            while (rowIterator.hasNext()) {
                Row linha = rowIterator.next();

                Boletim b = new Boletim(
                        (int) linha.getCell(0).getNumericCellValue(),
                        linha.getCell(1).getStringCellValue(),
                        linha.getCell(2).getStringCellValue(),
                        linha.getCell(3).getNumericCellValue(),
                        linha.getCell(4).getNumericCellValue(),
                        linha.getCell(5).getNumericCellValue(),
                        linha.getCell(6).getNumericCellValue(),
                        linha.getCell(7).getNumericCellValue(),
                        linha.getCell(8).getStringCellValue()
                );

                lista.add(b);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }


    public static List<Frequencia> carregarFrequencias(File arquivo) {
        List<Frequencia> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(arquivo);
             Workbook workbook = criarWorkbook(fis, arquivo.getName())) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Cabeçalho
            if (rowIterator.hasNext()) rowIterator.next();

            // Linhas
            while (rowIterator.hasNext()) {
                Row linha = rowIterator.next();
                Frequencia f = new Frequencia();

                f.setMatricula((int) linha.getCell(0).getNumericCellValue());
                f.setData(linha.getCell(2).getStringCellValue());
                f.setStatus(linha.getCell(3).getStringCellValue());

                lista.add(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }


    // Detecta se é .xls ou .xlsx
    private static Workbook criarWorkbook(FileInputStream fis, String nomeArquivo) throws Exception {
        if (nomeArquivo.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (nomeArquivo.toLowerCase().endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("Arquivo não é Excel: " + nomeArquivo);
        }
    }

    // Converte qualquer célula pra texto legível
    private static String obterValorComoTexto(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell))
                    yield cell.getDateCellValue().toString();
                else
                    yield String.valueOf((int) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

}
