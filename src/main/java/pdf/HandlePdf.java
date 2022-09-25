package pdf;

import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.*;
import java.io.*;
import java.util.stream.Stream;

public class HandlePdf {

    public void createNewFile() {
        PDDocument doc;
        try {
            doc = new PDDocument();
            doc.addPage(new PDPage());
            doc.save("empty_doc.pdf");
            doc.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void createNewFileWithContent() {
        PDDocument doc;
        PDPage page;

        try {
            doc = new PDDocument();
            page = new PDPage();

            doc.addPage(page);
            PDFont font = PDType1Font.HELVETICA_BOLD;

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.beginText();
            content.setFont(font, 20);
            content.setNonStrokingColor(Color.BLUE);
            content.newLineAtOffset(100, 700);
            content.showText("Hello It's me");

            content.endText();
            content.close();

            doc.save("pdf_with_text.pdf");
            doc.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void extractContentFromPDF() {
        PDDocument doc;
        BufferedWriter bw;
        try {
            File input = new File("pdf_with_text.pdf");
            File output = new File("pdf_with_text_copy.txt");
            doc = PDDocument.load(input);

            System.out.println(doc.getNumberOfPages());
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(2);
            // if you want to get all text
            // stripper.setEndPage(doc.getNumberOfPages())
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            stripper.writeText(doc, bw);

            bw.close();
            doc.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void insertTableToPdfFile() throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));

        document.open();

        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        addRows(table);
        addCustomRows(table);

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("column header 1", "column header 2", "column header 3").forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }

    private void addRows(PdfPTable table) {
        table.addCell("row 1, col 1");
        table.addCell("row 1, col 2");
        table.addCell("row 1, col 3");
    }

    private void addCustomRows(PdfPTable table) throws BadElementException, IOException {
        Image img = Image.getInstance("batman.jpeg");
        img.scalePercent(10);

        PdfPCell imageCell = new PdfPCell(img);
        table.addCell(imageCell);

        PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
        horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(horizontalAlignCell);

        PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
        verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(verticalAlignCell);
    }

    public void encrypt() throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        AccessPermission accessPermission = new AccessPermission();
        accessPermission.setCanPrint(false);
        accessPermission.setCanModify(false);

        StandardProtectionPolicy standardProtectionPolicy = new StandardProtectionPolicy("ownerpass", "userpass", accessPermission);
        document.protect(standardProtectionPolicy);
        document.save("pdfBoxEncryption.pdf");
        document.close();
    }

    public void convertImageToPDF() {
        PDDocument doc;
        try {
            doc = new PDDocument();
            PDPage page = new PDPage();
            doc.addPage(page);
            PDImageXObject image = JPEGFactory.createFromStream(doc, new FileInputStream("batman.jpeg"));

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.drawImage(image, 64, 512, 480, 270);
            content.close();

            doc.save("image_pdf.pdf");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
