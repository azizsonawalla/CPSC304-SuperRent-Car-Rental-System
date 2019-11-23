package model.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TextTable {

    String format = "|";
    HashMap<String, Integer> headings = new HashMap<>();
    List<List<String>> rows = new ArrayList<>();


    public void addColumn(String heading, int maxCharSize) {
        format = format.concat("  %-" + maxCharSize + "s|");
        headings.put(heading, maxCharSize);
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    public String getTable() {
        Integer totalWidth = 0;
        for(Integer width: headings.values()) totalWidth += width;

        StringBuilder outlineBuilder = new StringBuilder();
        for(int i=0; i < totalWidth*1.1 + (2*headings.size()); i++) outlineBuilder.append("=");
        String outline = outlineBuilder.toString().concat("\n");

        String table = outline;
        format = format + "\n";
        String[] thisHeadings = headings.keySet().toArray(new String[0]);
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
//        String format = "|%-30s|%-10s|%-20s|\n";
//        System.out.format(format, "A", "AA", "AAA");
//        System.out.format(format, "B", "", "BBBBB");
//        System.out.format(format, "C", "CCCCC", "CCCCCCCC");
//
//        String ex[] = { "E", "EEEEEEEEEE", "E" };
//
//        System.out.format(String.format(format, (Object[]) ex));

        TextTable t = new TextTable();
        t.addColumn("ColA", 10);
        t.addColumn("Colb", 15);
        t.addColumn("ColC", 10);

        t.addRow(Arrays.asList("A", "B", "C"));
        t.addRow(Arrays.asList("A", "B", "C"));
        t.addRow(Arrays.asList("A", "B", "C"));

        System.out.print(t.getTable());
    }
}
