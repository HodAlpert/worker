package com.worker.managers;

import com.worker.common.init;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.logging.Logger;


@SuppressWarnings("Duplicates")
public class PDFManager {
    private Logger logger = init.logger;

    @SuppressWarnings("Duplicates")
    public String get_first_page_html(String pdf_url) {
        String file_name = null;
        String content = null;
        String new_file_name = null;
        try {
            file_name = download_pdf_file_from_url(pdf_url);
        } catch (Exception download_exception) {
            return handle_exception(download_exception, "ERROR: could not download pdf file");
        }
        PDDocument document;
        try {
            logger.info("file name: " + file_name);
            document = PDDocument.load(file_name);
        } catch (Exception load_pdf_exception) {
            return handle_exception(load_pdf_exception, "could not load pdf document");
        }
        try {
            new_file_name = file_name.replace(".pdf", ".html");
            content = get_html_text(document);
        } catch (Exception get_html_text) {
            return handle_exception(get_html_text, "could not convert to html");
        }
        try {
            write_text_to_file(new_file_name, content);
            return new_file_name;
        } catch (Exception e) {
            return handle_exception(e, "could not write html text to file");
        }
    }

    /**
     * @param pdf_url url of the pdf from which text should be taken
     * @return String containing all text from the first page
     */
    public String get_first_page_text(String pdf_url) {
        String filename = null;
        String content = null;
        try {
            filename = download_pdf_file_from_url(pdf_url);
        } catch (Exception download_exception) {
            return handle_exception(download_exception, "ERROR: could not download pdf file");
        }
        try {
            content = get_text(filename);
        } catch (Exception getting_text) {
            return handle_exception(getting_text, "could not get first page text");
        }
        try {
            String new_file_name = "to_s3" + filename.replace(".pdf", ".txt");
            write_text_to_file(new_file_name, content);
            return new_file_name;
        } catch (Exception write_text) {
            return handle_exception(write_text, "could not write text to file");
        }
    }

    private String get_html_text(PDDocument document) throws IOException {
        PDFText2HTML stripper = new PDFText2HTML("UTF-8");
        //PDFTextStripper stripper = new PDFTextStripper();
        StringWriter writer = new StringWriter();
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        stripper.writeText(document, writer);
        return writer.toString();
    }

    /**
     * @param pdf_url url of the pdf from which text should be taken
     * @return image file of first pdf file
     */
    public String get_first_page_image(String pdf_url) {
        PDDocument document = null;
        String file_name = null;
        try {
            file_name = download_pdf_file_from_url(pdf_url);
        } catch (Exception download_exception) {
            return handle_exception(download_exception, "ERROR: could not download pdf file");
        }
        try {
            logger.info("file name: " + file_name);
            document = PDDocument.load(file_name);
        } catch (Exception load_pdf_exception) {
            return handle_exception(load_pdf_exception, "could not load pdf document");
        }
        try {
            file_name = file_name.replace(".pdf", "");
            logger.info("new_file_name: " + file_name);
            PDFImageWriter imageWriter = new PDFImageWriter();
            imageWriter.writeImage(document,
                    "png",
                    null,
                    1,
                    1,
                    file_name,
                    BufferedImage.TYPE_INT_RGB,
                    96);

        } catch (Exception e) {
            return handle_exception(e, "could not save first page image");
        }
        return String.format("%s1.png", file_name);

    }

    private String handle_exception(Exception e, String answer) {
        logger.severe("got exception " + e.getMessage() + Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        return "ERROR: " + answer;
    }


    private String download_pdf_file_from_url(String pdf_url) throws Exception {
        String path_to_save_file = parse_file_name(pdf_url);
        URL url = new URL(pdf_url);
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(path_to_save_file), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            logger.severe("got exception " + e.getMessage() + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            throw e;
        }
        return path_to_save_file;
    }

    private String parse_file_name(String pdf_url) {
        String[] splited_url = pdf_url.split("/");
        return splited_url[splited_url.length - 1];
    }

    private String get_text(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        PDFParser parser = new PDFParser(fis);
        parser.parse();
        PDDocument pdfDocument = parser.getPDDocument();
        PDFTextStripper stripper = new PDFTextStripper();

        // get text of a certain page
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        return stripper.getText(pdfDocument);
    }

    private void write_text_to_file(String file_name, String content) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(file_name, "UTF-8");
        writer.print(content);
        writer.close();

    }
}
