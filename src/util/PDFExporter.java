package util;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PDFExporter {

    private static final float PAGE_WIDTH = 842f;
    private static final float PAGE_HEIGHT = 595f;
    private static final float LEFT_MARGIN = 25f;
    private static final float TOP_MARGIN = 520f;
    private static final float HEADER_HEIGHT = 20f;
    private static final float TITLE_FONT_SIZE = 12f;
    private static final float CELL_FONT_SIZE = 7.5f;
    private static final float LINE_HEIGHT = 10f;

    public static void exportTableViewToPDF(
            TableView<?> tableView,
            String filePath,
            String title) throws Exception {

        Path outputPath = Paths.get(filePath);
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        List<TableColumn<?, ?>> columns = new ArrayList<>(tableView.getColumns());
        List<List<String>> rows = new ArrayList<>();

        for (int row = 0; row < tableView.getItems().size(); row++) {
            List<String> values = new ArrayList<>();
            for (TableColumn<?, ?> column : columns) {
                Object value = column.getCellData(row);
                values.add(value == null ? "" : value.toString());
            }
            rows.add(values);
        }

        String contentStream = buildContentStream(title, columns, rows);
        byte[] contentBytes = contentStream.getBytes(StandardCharsets.ISO_8859_1);

        List<Integer> offsets = new ArrayList<>();
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));

        offsets.add(pdf.size());
        writeObject(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
        offsets.add(pdf.size());
        writeObject(pdf, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
        offsets.add(pdf.size());
        writeObject(pdf, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT
                + "] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\nendobj\n");
        offsets.add(pdf.size());
        writeObject(pdf, "4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");
        offsets.add(pdf.size());
        writeObject(pdf, "5 0 obj\n<< /Length " + contentBytes.length + " >>\nstream\n" + contentStream
                + "\nendstream\nendobj\n");

        int xrefOffset = pdf.size();
        pdf.write(("xref\n0 " + (offsets.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        pdf.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
        for (Integer offset : offsets) {
            pdf.write(String.format("%010d 00000 n \n", offset).getBytes(StandardCharsets.ISO_8859_1));
        }

        pdf.write(("trailer\n<< /Size " + (offsets.size() + 1) + " /Root 1 0 R >>\n")
                .getBytes(StandardCharsets.ISO_8859_1));
        pdf.write(("startxref\n" + xrefOffset + "\n%%EOF\n").getBytes(StandardCharsets.ISO_8859_1));

        Files.write(outputPath, pdf.toByteArray());
    }

    private static String buildContentStream(String title, List<TableColumn<?, ?>> columns, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();

        float tableTopY = TOP_MARGIN;
        if (title != null && !title.isBlank()) {
            String[] titleLines = title.split("\\R");
            float y = TOP_MARGIN + 20f;
            for (String line : titleLines) {
                String safeLine = escapePdfText(line.trim());
                if (safeLine.isEmpty()) {
                    safeLine = " ";
                }
                appendText(sb, LEFT_MARGIN, y, TITLE_FONT_SIZE, safeLine);
                y -= 12f;
            }
            tableTopY = y - 10f;
        }

        if (columns.isEmpty()) {
            return sb.toString();
        }

        float tableWidth = PAGE_WIDTH - (LEFT_MARGIN * 2f);
        List<Float> widths = computeColumnWidths(columns, rows, tableWidth);
        float x = LEFT_MARGIN;
        float y = tableTopY;
        float totalHeight = HEADER_HEIGHT + (rows.size() * 16f);

        drawRect(sb, x, y - HEADER_HEIGHT - totalHeight, tableWidth, totalHeight, false);
        drawRect(sb, x, y - HEADER_HEIGHT, tableWidth, HEADER_HEIGHT, true);

        float currentX = x;
        for (int i = 0; i < columns.size(); i++) {
            float width = widths.get(i);
            drawVerticalLine(sb, currentX + width, y - HEADER_HEIGHT - totalHeight, totalHeight);
            appendText(sb, currentX + 3f, y - 13f, CELL_FONT_SIZE, columns.get(i).getText());
            currentX += width;
        }

        float rowY = y - HEADER_HEIGHT;
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            rowY -= 16f;
            drawHorizontalLine(sb, x, rowY, tableWidth);
            List<String> rowValues = rows.get(rowIndex);
            float cellX = x;
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                float width = widths.get(colIndex);
                String value = rowValues.size() > colIndex ? rowValues.get(colIndex) : "";
                appendText(sb, cellX + 3f, rowY + 5f, CELL_FONT_SIZE, value);
                cellX += width;
            }
        }

        return sb.toString();
    }

    private static List<Float> computeColumnWidths(List<TableColumn<?, ?>> columns, List<List<String>> rows,
            float tableWidth) {
        List<Float> widths = new ArrayList<>();
        float totalMin = 0f;
        for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
            float minWidth = Math.max(50f, columns.get(colIndex).getText().length() * 4f);
            for (List<String> row : rows) {
                String value = row.size() > colIndex ? row.get(colIndex) : "";
                minWidth = Math.max(minWidth, (value == null ? 0 : value.length()) * 3.2f);
            }
            widths.add(minWidth);
            totalMin += minWidth;
        }

        if (totalMin <= tableWidth) {
            return widths;
        }

        float scale = tableWidth / totalMin;
        for (int i = 0; i < widths.size(); i++) {
            widths.set(i, widths.get(i) * scale);
        }
        return widths;
    }

    private static void drawRect(StringBuilder sb, float x, float y, float width, float height, boolean fill) {
        sb.append("q\n0.94 0.96 0.98 rg\n");
        sb.append(x).append(" ").append(y).append(" ").append(width).append(" ").append(height).append(" re\n");
        if (fill) {
            sb.append("f\n");
        } else {
            sb.append("0 0 0 RG\nS\n");
        }
        sb.append("Q\n");
    }

    private static void drawHorizontalLine(StringBuilder sb, float x, float y, float width) {
        sb.append("q\n0 0 0 RG\n").append(x).append(" ").append(y).append(" m\n").append(x + width).append(" ")
                .append(y).append(" l\nS\nQ\n");
    }

    private static void drawVerticalLine(StringBuilder sb, float x, float y, float height) {
        sb.append("q\n0 0 0 RG\n").append(x).append(" ").append(y).append(" m\n").append(x).append(" ")
                .append(y + height).append(" l\nS\nQ\n");
    }

    private static void appendText(StringBuilder sb, float x, float y, float size, String text) {
        String safeText = escapePdfText(text == null ? "" : text);
        sb.append("BT\n/F1 ").append(size).append(" Tf\n").append(x).append(" ").append(y).append(" Td\n(")
                .append(safeText).append(") Tj\nET\n");
    }

    private static String escapePdfText(String text) {
        return text == null ? ""
                : text
                        .replace("\\", "\\\\")
                        .replace("(", "\\(")
                        .replace(")", "\\)");
    }

    private static void writeObject(ByteArrayOutputStream outputStream, String objectContent) throws IOException {
        outputStream.write(objectContent.getBytes(StandardCharsets.ISO_8859_1));
    }
}