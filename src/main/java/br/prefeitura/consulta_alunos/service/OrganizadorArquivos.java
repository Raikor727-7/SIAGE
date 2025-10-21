// Arquivo: br/prefeitura/consulta_alunos/service/OrganizadorArquivos.java
package br.prefeitura.consulta_alunos.service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class OrganizadorArquivos {

    private int documentosMovidos = 0;
    private int planilhasMovidas = 0;
    private int imagensMovidas = 0;
    private int ignorados = 0;
    private long inicioExecucao;

    public void organizarPasta(String caminhoPasta) {
        resetarContadores();
        inicioExecucao = System.currentTimeMillis();

        File pastaBase = new File(caminhoPasta);
        if (!pastaBase.exists() || !pastaBase.isDirectory()) {
            System.out.println("❌ Pasta não encontrada: " + caminhoPasta);
            return;
        }

        // Criar pastas de destino
        File pastaDocs = criarPasta(pastaBase, "Documentos");
        File pastaPlanilhas = criarPasta(pastaBase, "Planilhas");
        File pastaImagens = criarPasta(pastaBase, "Imagens");

        if (pastaDocs == null || pastaPlanilhas == null || pastaImagens == null) {
            System.out.println("❌ Erro ao criar pastas de organização");
            return;
        }

        // Processar arquivos da pasta principal (sem recursão)
        processarArquivos(pastaBase, pastaDocs, pastaPlanilhas, pastaImagens);

        // Gerar relatório
        gerarRelatorio(pastaBase);
    }

    private File criarPasta(File pastaBase, String nomePasta) {
        File pasta = new File(pastaBase, nomePasta);
        if (!pasta.exists()) {
            if (pasta.mkdir()) {
                System.out.println("✅ Pasta criada: " + nomePasta);
            } else {
                System.out.println("❌ Erro ao criar pasta: " + nomePasta);
                return null;
            }
        }
        return pasta;
    }

    private void processarArquivos(File pastaBase, File pastaDocs, File pastaPlanilhas, File pastaImagens) {
        File[] arquivos = pastaBase.listFiles(File::isFile);

        if (arquivos == null || arquivos.length == 0) {
            System.out.println("📁 Nenhum arquivo encontrado na pasta");
            return;
        }

        for (File arquivo : arquivos) {
            if (arquivo.isFile()) {
                moverArquivo(arquivo, pastaDocs, pastaPlanilhas, pastaImagens);
            }
        }
    }

    private void moverArquivo(File arquivo, File pastaDocs, File pastaPlanilhas, File pastaImagens) {
        String nomeArquivo = arquivo.getName().toLowerCase();
        Path destino = null;

        if (nomeArquivo.endsWith(".doc") || nomeArquivo.endsWith(".docx") || nomeArquivo.endsWith(".pdf")) {
            destino = pastaDocs.toPath().resolve(arquivo.getName());
            documentosMovidos++;
        } else if (nomeArquivo.endsWith(".xls") || nomeArquivo.endsWith(".xlsx") || nomeArquivo.endsWith(".csv")) {
            destino = pastaPlanilhas.toPath().resolve(arquivo.getName());
            planilhasMovidas++;
        } else if (nomeArquivo.endsWith(".jpg") || nomeArquivo.endsWith(".jpeg") ||
                nomeArquivo.endsWith(".png") || nomeArquivo.endsWith(".bmp")) {
            destino = pastaImagens.toPath().resolve(arquivo.getName());
            imagensMovidas++;
        } else {
            ignorados++;
            return;
        }

        try {
            Files.move(arquivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("📦 Movido: " + arquivo.getName() + " → " + destino.getParent().getFileName());
        } catch (IOException e) {
            System.out.println("❌ Erro ao mover " + arquivo.getName() + ": " + e.getMessage());
        }
    }

    private void gerarRelatorio(File pastaBase) {
        long tempoTotal = System.currentTimeMillis() - inicioExecucao;

        String relatorio = String.format(
                "📊 RELATÓRIO DE ORGANIZAÇÃO\n" +
                        "📍 Pasta: %s\n" +
                        "📅 Data: %s\n\n" +
                        "📄 Documentos movidos: %d\n" +
                        "📊 Planilhas movidas: %d\n" +
                        "🖼️ Imagens movidas: %d\n" +
                        "⚡ Ignorados: %d\n" +
                        "⏱️ Tempo total: %d ms\n",
                pastaBase.getAbsolutePath(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                documentosMovidos, planilhasMovidas, imagensMovidas, ignorados, tempoTotal
        );

        // Salvar relatório em arquivo
        try {
            File arquivoRelatorio = new File(pastaBase, "relatorio_organizacao.txt");
            Files.write(arquivoRelatorio.toPath(), relatorio.getBytes());
            System.out.println("\n" + relatorio);
            System.out.println("💾 Relatório salvo em: " + arquivoRelatorio.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("❌ Erro ao salvar relatório: " + e.getMessage());
        }
    }

    private void resetarContadores() {
        documentosMovidos = 0;
        planilhasMovidas = 0;
        imagensMovidas = 0;
        ignorados = 0;
    }
}