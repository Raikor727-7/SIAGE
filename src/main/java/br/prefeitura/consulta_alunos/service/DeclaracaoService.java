package br.prefeitura.consulta_alunos.service;

import br.prefeitura.consulta_alunos.model.Declaracao;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DeclaracaoService {

    public void gerarDeclaracaoWord(Declaracao declaracao, String caminhoSaida) throws IOException {
        try (XWPFDocument doc = new XWPFDocument()) {
            // Criar parágrafo centralizado para o cabeçalho
            XWPFParagraph headerParagraph = doc.createParagraph();
            headerParagraph.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setBold(true);
            headerRun.setFontSize(14);
            headerRun.setText("COLÉGIO MUNICIPAL JOÃO VIEIRA BEZERRA");
            headerRun.addBreak();
            headerRun.setText("PRAÇA MARIA AURORA, 02.");
            headerRun.addBreak();
            headerRun.setText("LAGOA DE ITAENGA -- PE");
            headerRun.addBreak();
            headerRun.setText("CADASTRO ESCOLAR Nº M. 355.002");
            headerRun.addBreak();
            headerRun.addBreak();

            // Adicionar imagem (se existir)
            adicionarImagemLogo(doc);

            // Título DECLARAÇÃO
            XWPFParagraph titleParagraph = doc.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            titleParagraph.setSpacingAfter(400); // Espaço após o título

            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText("DECLARAÇÃO");
            titleRun.addBreak();

            // Corpo da declaração
            XWPFParagraph bodyParagraph = doc.createParagraph();
            bodyParagraph.setAlignment(ParagraphAlignment.BOTH);
            bodyParagraph.setIndentationFirstLine(600); // Recuo da primeira linha
            bodyParagraph.setSpacingAfter(400); // Espaço após o parágrafo

            XWPFRun bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);

            // Texto normal
            bodyRun.setText("Declaro para fins de comprovação que ");

            // Nome em negrito
            XWPFRun nomeRun = bodyParagraph.createRun();
            nomeRun.setFontSize(12);
            nomeRun.setBold(true);
            nomeRun.setText(declaracao.getNome().toUpperCase());

            // Texto normal
            bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);
            bodyRun.setText(", nascido(a) em ");

            // Data em negrito
            XWPFRun dataRun = bodyParagraph.createRun();
            dataRun.setFontSize(12);
            dataRun.setBold(true);
            dataRun.setText(formatarData(declaracao.getDataNascimento()));

            // Texto normal
            bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);
            bodyRun.setText(", filho(a) de ");

            // Pai em negrito
            XWPFRun paiRun = bodyParagraph.createRun();
            paiRun.setFontSize(12);
            paiRun.setBold(true);
            paiRun.setText(declaracao.getPai().toUpperCase());

            // Texto normal
            bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);
            bodyRun.setText(" e de ");

            // Mãe em negrito
            XWPFRun maeRun = bodyParagraph.createRun();
            maeRun.setFontSize(12);
            maeRun.setBold(true);
            maeRun.setText(declaracao.getMae().toUpperCase());

            // Texto normal
            bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);
            bodyRun.setText(", que o(a) mesmo(a) estudou neste Estabelecimento de Ensino, no Ano de ");

            // Ano em negrito
            XWPFRun anoRun = bodyParagraph.createRun();
            anoRun.setFontSize(12);
            anoRun.setBold(true);
            anoRun.setText(String.valueOf(declaracao.getAno()));

            // Texto normal
            bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);
            bodyRun.setText(" na ");

            // Série em negrito
            XWPFRun serieRun = bodyParagraph.createRun();
            serieRun.setFontSize(12);
            serieRun.setBold(true);
            serieRun.setText(declaracao.getSerie().toUpperCase());

            // Texto normal final
            bodyRun = bodyParagraph.createRun();
            bodyRun.setFontSize(12);
            bodyRun.setText(" série do Ensino Fundamental.");

            // Data e assinatura
            XWPFParagraph dateParagraph = doc.createParagraph();
            dateParagraph.setAlignment(ParagraphAlignment.CENTER);
            dateParagraph.setSpacingBefore(600); // Espaço antes da data

            XWPFRun dateRun = dateParagraph.createRun();
            dateRun.setText("Lagoa de Itaenga, " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy")) + ".");
            dateRun.addBreak();
            dateRun.addBreak();
            dateRun.addBreak();

            XWPFRun signatureRun = dateParagraph.createRun();
            signatureRun.setText("___________________________________");
            signatureRun.addBreak();
            signatureRun.setText("Coordenação/Direção");
            signatureRun.addBreak();
            signatureRun.setText("Colégio Municipal João Vieira Bezerra");

            // Salvar documento
            try (FileOutputStream out = new FileOutputStream(caminhoSaida)) {
                doc.write(out);
            }
        }
    }

    private void adicionarImagemLogo(XWPFDocument doc) {
        try {
            // Caminho da imagem - ajuste conforme sua estrutura de pastas
            String[] caminhosPossiveis = {
                    "src/main/resources/Images/Jvb.png",
                    "resources/Images/Jvb.png",
                    "Images/Jvb.png",
                    "Jvb.png"
            };

            InputStream imagemStream = null;
            for (String caminho : caminhosPossiveis) {
                File arquivoImagem = new File(caminho);
                if (arquivoImagem.exists()) {
                    imagemStream = new FileInputStream(arquivoImagem);
                    break;
                }
            }

            // Se não encontrou nos caminhos, tenta carregar do classpath
            if (imagemStream == null) {
                imagemStream = getClass().getResourceAsStream("/Images/Jvb.png");
            }

            if (imagemStream != null) {
                XWPFParagraph imageParagraph = doc.createParagraph();
                imageParagraph.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun imageRun = imageParagraph.createRun();

                // NOVOS TAMANHOS: 7cm largura x 6.57cm altura (sem bordas)
                // Converter centímetros para EMU: 1cm = 360000 EMU
                int larguraEMU = (int) (8.0 * 360000);   // 8cm
                int alturaEMU = (int) (6.57 * 360000);   // 6.57cm

                imageRun.addPicture(imagemStream,
                        XWPFDocument.PICTURE_TYPE_PNG,
                        "logo.png",
                        larguraEMU,
                        alturaEMU);

                imageRun.addBreak();
                imageRun.addBreak();

                imagemStream.close();
            } else {
                System.out.println("⚠️ Imagem do logo não encontrada. Continuando sem imagem.");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Erro ao adicionar imagem: " + e.getMessage());
            // Continua sem a imagem - não quebra a geração do documento
        }
    }

    private String formatarData(LocalDate data) {
        if (data == null) {
            return "[data de nascimento]";
        }
        try {
            return data.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy"));
        } catch (Exception e) {
            return "[data de nascimento]";
        }
    }
}