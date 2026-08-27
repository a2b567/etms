package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ETMSTable extends JTable {

    public ETMSTable(DefaultTableModel model) {
        super(model);
        setRowHeight(Spacing.XXL + Spacing.SM);
        setFont(Typography.BODY);
        setForeground(ThemeManager.getText());
        setBackground(ThemeManager.getSurface());
        setGridColor(ThemeManager.getBorder());
        setSelectionBackground(ThemeManager.getAccentSoft());
        setSelectionForeground(ThemeManager.getText());
        setShowHorizontalLines(true);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 1));
        setDefaultRenderer(Object.class, new ETMSTableRenderer());

        JTableHeader header = getTableHeader();
        header.setFont(Typography.CARD_TITLE);
        header.setBackground(ThemeManager.getSurface());
        header.setForeground(ThemeManager.getTextSecondary());
        header.setDefaultRenderer(new HeaderRenderer());
        header.setReorderingAllowed(false);
    }

    private static class ETMSTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? ThemeManager.getSurface() : ThemeManager.getElevated());
            }
            c.setForeground(ThemeManager.getText());
            setBorder(BorderFactory.createEmptyBorder(Spacing.SM, Spacing.SM, Spacing.SM, Spacing.SM));
            return c;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(ThemeManager.getSurface());
            c.setForeground(ThemeManager.getTextSecondary());
            setBorder(BorderFactory.createEmptyBorder(Spacing.SM, Spacing.SM, Spacing.SM, Spacing.SM));
            return c;
        }
    }

    public void refreshTheme() {
        setForeground(ThemeManager.getText());
        setBackground(ThemeManager.getSurface());
        setGridColor(ThemeManager.getBorder());
        setSelectionBackground(ThemeManager.getAccentSoft());
        setSelectionForeground(ThemeManager.getText());

        JTableHeader header = getTableHeader();
        if (header != null) {
            header.setBackground(ThemeManager.getSurface());
            header.setForeground(ThemeManager.getTextSecondary());
        }

        // Force the table to redraw with new colors
        repaint();
        revalidate();
    }
}