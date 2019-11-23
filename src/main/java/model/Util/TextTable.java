package model.Util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextTable {

    String format = "|";
    List<String> headings = new ArrayList<>();
    List<List<String>> rows = new ArrayList<>();
    Integer totalWidth = 0;


    public void addColumn(String heading, int maxCharSize) {
        format = format.concat("  %-" + maxCharSize + "s|");
        headings.add(heading);
        totalWidth += maxCharSize;
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    public String getTable() {
        StringBuilder outlineBuilder = new StringBuilder();
        for(int i=0; i < totalWidth*0.5+ (2*headings.size()); i++) outlineBuilder.append("="); // *1.1
        String outline = outlineBuilder.toString().concat("\n");

        String table = outline;
        format = format + "\n";
        String[] thisHeadings = headings.toArray(new String[0]);
        table = table.concat(String.format(format, thisHeadings));

        table = table.concat(outline);
        for (List<String> row: rows) {
            String[] thisRow = row.toArray(new String[0]);
            table = table.concat(String.format(format, thisRow));
        }
        table = table.concat(outline);
        return table;
    }

    public static void main(String args[]) {
        String format = "|%-30s|%-10s|%-20s|\n";
        System.out.format(format, "A  S:", "AA", "AAA");
        System.out.format(format, "Bbb", "", "BBBBB");
        System.out.format(format, "C", "CC::CCC", "CCccCCCC");

        String ex[] = { "E", "EEEEEE", "E" };

        System.out.format(String.format(format, (Object[]) ex));

//        TextTable t = new TextTable();
//        t.addColumn("ColA", 10);
//        t.addColumn("Colb", 15);
//        t.addColumn("ColC", 10);
//
//        t.addRow(Arrays.asList("A", "B", "C"));
//        t.addRow(Arrays.asList("A", "B", "C"));
//        t.addRow(Arrays.asList("A", "B", "C"));
//
//        System.out.print(t.getTable());
    }
}
