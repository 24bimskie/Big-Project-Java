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

        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // TITLE
        document.add(new Paragraph(title));
        document.add(new Paragraph(" "));

        int columnCount = tableView.getColumns().size();
        PdfPTable pdfTable = new PdfPTable(columnCount);

        // HEADER
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            PdfPCell cell = new PdfPCell(new Phrase(column.getText()));
            pdfTable.addCell(cell);
        }

        for (Object rowData : tableView.getItems()) {

            for (TableColumn<?, ?> column : tableView.getColumns()) {

                Object value = column.getCellData(rowData);

                pdfTable.addCell(value == null ? "" : value.toString());
            }
        }

        document.add(pdfTable);
        document.close();
    }
}