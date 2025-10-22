package br.prefeitura.consulta_alunos.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupArquivo {

    private static final String PASTA_BACKUP = "BACKUPS";

    public String fazerBackup(String caminhoArquivoOriginal) throws IOException {
        // Obter o diretório onde o JAR está executando
        String diretorioJar = getDiretorioExecucao();

        // Criar pasta BACKUPS se não existir
        Path pastaBackup = Paths.get(diretorioJar, PASTA_BACKUP);
        Files.createDirectories(pastaBackup);

        // Informações do arquivo original
        File arquivoOriginal = new File(caminhoArquivoOriginal);
        String nomeArquivo = arquivoOriginal.getName();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Nome do arquivo de backup (mantém extensão + timestamp)
        String nomeBase = nomeArquivo.substring(0, nomeArquivo.lastIndexOf('.'));
        String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.'));
        String nomeBackup = nomeBase + "_" + timestamp + extensao;

        // Caminho completo do backup
        Path caminhoBackup = pastaBackup.resolve(nomeBackup);

        // Copiar arquivo
        Files.copy(arquivoOriginal.toPath(), caminhoBackup, StandardCopyOption.REPLACE_EXISTING);

        return caminhoBackup.toString();
    }

    private String getDiretorioExecucao() {
        try {
            // Tenta obter o diretório do JAR
            String caminhoJar = BackupArquivo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(caminhoJar);
            return jarFile.getParent(); // Pasta onde está o JAR
        } catch (Exception e) {
            // Fallback: diretório atual de trabalho
            return System.getProperty("user.dir");
        }
    }

    // Método para listar backups existentes (opcional)
    public File[] listarBackups() {
        String diretorioJar = getDiretorioExecucao();
        Path pastaBackup = Paths.get(diretorioJar, PASTA_BACKUP);
        File pasta = pastaBackup.toFile();

        if (pasta.exists() && pasta.isDirectory()) {
            return pasta.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".xls") || name.toLowerCase().endsWith(".xlsx")
            );
        }
        return new File[0];
    }
}