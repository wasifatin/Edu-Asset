package com.examsphere.components;

import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * A themed data table panel with optional search bar, styled headers, and alternating row colors.
 * <p>
 * DataTablePanel wraps a {@link JTable} inside a {@link JScrollPane} and provides:
 * <ul>
 *   <li>Styled table headers with accent background and white bold text</li>
 *   <li>Alternating row background colors for readability</li>
 *   <li>38-pixel row height for comfortable touch targets</li>
 *   <li>Optional search bar that filters rows using {@link RowFilter}</li>
 *   <li>Automatic theme change support</li>
 * </ul>
 * </p>
 *
 * @author ExamSphere
 * @version 1.0
 */
public class DataTablePanel extends JPanel {

    /** The underlying data table. */
    private final JTable table;

    /** The table model backing the table. */
    private final DefaultTableModel tableModel;

    /** The scroll pane wrapping the table. */
    private final JScrollPane scrollPane;

    /** The row sorter used for search filtering. */
    private final TableRowSorter<DefaultTableModel> rowSorter;

    /** The search text field (null until addSearchBar is called). */
    private JTextField searchField;

    /** The column names for this table. */
    private final String[] columns;

    /**
     * Constructs a DataTablePanel with the specified column names and initial data.
     *
     * @param columns the column header names
     * @param data    the initial table data (each row is an Object array)
     */
    public DataTablePanel(String[] columns, Object[][] data) {
        super(new BorderLayout(0, Constants.SPACING_SM));
        this.columns = columns;
        setOpaque(false);

        ThemeManager tm = ThemeManager.getInstance();

        // Create non-editable table model
        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Apply styling
        applyTableStyle();

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(tm.getBorderColor(), 1));
        scrollPane.getViewport().setBackground(tm.getMainSurface());

        add(scrollPane, BorderLayout.CENTER);

        // Register theme change listener
        tm.addThemeChangeListener(this::applyTheme);
    }

    /**
     * Replaces the table data with a new data set, preserving column structure.
     *
     * @param data the new table data (each row is an Object array)
     */
    public void setData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    /**
     * Adds a search bar above the table that filters rows in real-time.
     * The filter matches any column containing the search text (case-insensitive).
     *
     * @param placeholder the placeholder text displayed when the search field is empty
     */
    public void addSearchBar(String placeholder) {
        ThemeManager tm = ThemeManager.getInstance();

        JPanel searchPanel = new JPanel(new BorderLayout(Constants.SPACING_SM, 0));
        searchPanel.setOpaque(false);

        JLabel searchIcon = new JLabel("  \uD83D\uDD0D ");
        searchIcon.setFont(tm.getFont(14));
        searchIcon.setForeground(tm.getSecondaryText());

        searchField = new JTextField();
        searchField.setFont(tm.getFont(13));
        searchField.setForeground(tm.getPrimaryText());
        searchField.setBackground(tm.getSecondarySurface());
        searchField.setCaretColor(tm.getPrimaryText());
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tm.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // Placeholder text behavior
        searchField.setToolTipText(placeholder);

        // Real-time filtering on text changes
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        add(searchPanel, BorderLayout.NORTH);
        revalidate();
    }

    /**
     * Returns the underlying JTable for direct access.
     *
     * @return the JTable instance
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Adds a selection listener to the table's selection model.
     *
     * @param listener the selection listener to register
     */
    public void setTableSelectionListener(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * Returns the current text in the search field, or an empty string if no search bar exists.
     *
     * @return the search text
     */
    public String getSearchText() {
        return searchField != null ? searchField.getText() : "";
    }

    /**
     * Applies the RowFilter to filter table rows based on the search text.
     * Matches any column containing the search text, case-insensitive.
     */
    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            } catch (java.util.regex.PatternSyntaxException ex) {
                rowSorter.setRowFilter(null);
            }
        }
    }

    /**
     * Applies visual styling to the table: headers, row height, alternating colors, fonts.
     */
    private void applyTableStyle() {
        ThemeManager tm = ThemeManager.getInstance();

        // Row height
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(
                tm.getPrimaryAccent().getRed(),
                tm.getPrimaryAccent().getGreen(),
                tm.getPrimaryAccent().getBlue(),
                40));
        table.setSelectionForeground(tm.getPrimaryText());
        table.setBackground(tm.getMainSurface());
        table.setForeground(tm.getPrimaryText());
        table.setFont(tm.getFont(13));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(tm.getPrimaryAccent());
        header.setForeground(Color.WHITE);
        header.setFont(tm.getBoldFont(13));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);

        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, column);
                label.setBackground(tm.getPrimaryAccent());
                label.setForeground(Color.WHITE);
                label.setFont(tm.getBoldFont(13));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, tm.getPrimaryAccent().darker()),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                label.setOpaque(true);
                return label;
            }
        });

        // Alternating row colors renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        label.setBackground(tm.getMainSurface());
                    } else {
                        label.setBackground(tm.getSecondarySurface());
                    }
                    label.setForeground(tm.getPrimaryText());
                }
                label.setFont(tm.getFont(13));
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                label.setOpaque(true);
                return label;
            }
        });
    }

    /**
     * Reapplies theme colors and fonts when the application theme changes.
     */
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();

        applyTableStyle();

        scrollPane.setBorder(BorderFactory.createLineBorder(tm.getBorderColor(), 1));
        scrollPane.getViewport().setBackground(tm.getMainSurface());

        if (searchField != null) {
            searchField.setFont(tm.getFont(13));
            searchField.setForeground(tm.getPrimaryText());
            searchField.setBackground(tm.getSecondarySurface());
            searchField.setCaretColor(tm.getPrimaryText());
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(tm.getBorderColor(), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }

        repaint();
        revalidate();
    }
}
