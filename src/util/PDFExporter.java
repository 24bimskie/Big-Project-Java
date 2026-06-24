package util;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.FileOutputStream;

public class PDFExporter {

    public static void exportTableViewToPDF(
            TableView<?> tableView,
            String filePath,
            String title) throws Exception {

        Document document = new Document();

        PdfWriter.getInstance(
                document,
                new FileOutputStream(filePath));

        document.open();

        document.add(new Paragraph(title));
        document.add(new Paragraph(" "));

        PdfPTable pdfTable = new PdfPTable(tableView.getColumns().size());

        // Header
        for (TableColumn<?, ?> column : tableView.getColumns()) {

            PdfPCell cell = new PdfPCell(
                    new Phrase(column.getText()));

            pdfTable.addCell(cell);
        }

        // Data
        for (int row = 0; row < tableView.getItems().size(); row++) {

            for (TableColumn<?, ?> column : tableView.getColumns()) {

                Object value = column.getCellData(row);

                pdfTable.addCell(
                        value == null
                                ? ""
                                : value.toString());
            }
        }

        document.add(pdfTable);

        document.close();
    }
}