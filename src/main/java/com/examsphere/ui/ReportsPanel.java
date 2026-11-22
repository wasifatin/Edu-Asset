package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.RoundedPanel;
import com.examsphere.components.StatCard;
import com.examsphere.service.ReportService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * Institutional reports and analytics panel providing aggregated metrics,
 * Java2D graphical visualizations, and multi-threaded report generation/export.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ReportsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final ReportService reportService = ReportService.getInstance();

    public ReportsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        setBorder(new EmptyBorder(Constants.SPACING_LG, Constants.SPACING_LG, Constants.SPACING_LG, Constants.SPACING_LG));
        setBackground(theme.getBackground());

        buildUI();
        theme.addThemeChangeListener(this::refreshTheme);
    }

    private void buildUI() {
        removeAll();
        setBackground(theme.getBackground());

        // Header
        RoundedPanel headerCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        headerCard.setLayout(new BorderLayout(12, 12));
        headerCard.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Institutional Examination Analytics & Reports");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        CustomButton exportBtn = new CustomButton("⤓ Export Institutional Audit Report", theme.getPrimaryAccent(), Color.WHITE);
        exportBtn.addActionListener(e -> exportReportAsync());
        headerCard.add(exportBtn, BorderLayout.EAST);

        add(headerCard, BorderLayout.NORTH);

        // Center Content Container
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // Row 1: KPI Stat Cards
        JPanel statRow = new JPanel(new GridLayout(1, 3, Constants.SPACING, 0));
        statRow.setOpaque(false);
        statRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        double passRate = reportService.getPassPercentage();
        double failRate = reportService.getFailPercentage();
        double attRate = reportService.getAttendancePercentage();

        statRow.add(new StatCard("EXAM ATTENDANCE RATE", attRate + "%", "Present candidates across halls", theme.getPrimaryAccent()));
        statRow.add(new StatCard("INSTITUTIONAL PASS RATE", passRate + "%", "Examinees meeting degree threshold", theme.getSuccess()));
        statRow.add(new StatCard("UNSATISFACTORY (FAIL)", failRate + "%", "Candidates requiring retake", theme.getDanger()));

        mainContent.add(statRow);
        mainContent.add(Box.createVerticalStrut(Constants.SPACING));

        // Row 2: Custom Charts (Left: Java2D Pass/Fail Pie, Right: Department CGPA Comparison Bar Chart)
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, Constants.SPACING, 0));
        chartsRow.setOpaque(false);
        chartsRow.setPreferredSize(new Dimension(0, 240));
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));

        // Chart 1: Pass/Fail Pie Chart
        RoundedPanel pieCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        pieCard.setLayout(new BorderLayout(8, 8));
        pieCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel pieTitle = new JLabel("Academic Performance Distribution (Pass vs Fail)");
        pieTitle.setFont(theme.getBoldFont(14));
        pieTitle.setForeground(theme.getPrimaryText());
        pieCard.add(pieTitle, BorderLayout.NORTH);

        JPanel pieCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 40;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                int passAngle = (int) Math.round((passRate / 100.0) * 360.0);
                int failAngle = 360 - passAngle;

                g2.setColor(theme.getSuccess());
                g2.fillArc(x, y, size, size, 90, -passAngle);

                g2.setColor(theme.getDanger());
                g2.fillArc(x, y, size, size, 90 - passAngle, -failAngle);

                // Inner circle for donut style
                int innerSize = size / 2;
                int ix = (getWidth() - innerSize) / 2;
                int iy = (getHeight() - innerSize) / 2;
                g2.setColor(theme.getMainSurface());
                g2.fillOval(ix, iy, innerSize, innerSize);

                // Center text
                g2.setColor(theme.getPrimaryText());
                g2.setFont(theme.getBoldFont(14));
                String label = (int) passRate + "% Pass";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2, (getHeight() / 2) + 5);

                g2.dispose();
            }
        };
        pieCanvas.setOpaque(false);
        pieCard.add(pieCanvas, BorderLayout.CENTER);

        // Chart 2: Department Performance Bar Chart
        RoundedPanel barCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        barCard.setLayout(new BorderLayout(8, 8));
        barCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel barTitle = new JLabel("Departmental Average CGPA Index");
        barTitle.setFont(theme.getBoldFont(14));
        barTitle.setForeground(theme.getPrimaryText());
        barCard.add(barTitle, BorderLayout.NORTH);

        Map<String, Double> deptGpa = reportService.getDepartmentPerformance();
        JPanel barCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth() - 60;
                int h = getHeight() - 50;
                int barWidth = 45;
                int spacing = (w - (deptGpa.size() * barWidth)) / (deptGpa.size() + 1);

                int curX = 30 + spacing;
                for (Map.Entry<String, Double> entry : deptGpa.entrySet()) {
                    double gpa = entry.getValue();
                    int barHeight = (int) ((gpa / 4.0) * h);
                    int barY = 20 + (h - barHeight);

                    g2.setColor(theme.getPrimaryAccent());
                    g2.fillRoundRect(curX, barY, barWidth, barHeight, 8, 8);

                    g2.setColor(theme.getPrimaryText());
                    g2.setFont(theme.getBoldFont(12));
                    String val = String.valueOf(gpa);
                    g2.drawString(val, curX + (barWidth - g2.getFontMetrics().stringWidth(val)) / 2, barY - 4);

                    String dept = entry.getKey();
                    g2.setFont(theme.getFont(12));
                    g2.drawString(dept, curX + (barWidth - g2.getFontMetrics().stringWidth(dept)) / 2, barY + barHeight + 16);

                    curX += barWidth + spacing;
                }
                g2.dispose();
            }
        };
        barCanvas.setOpaque(false);
        barCard.add(barCanvas, BorderLayout.CENTER);

        chartsRow.add(pieCard);
        chartsRow.add(barCard);

        mainContent.add(chartsRow);
        mainContent.add(Box.createVerticalStrut(Constants.SPACING));

        // Row 3: Course Performance Table
        RoundedPanel coursePerfCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        coursePerfCard.setLayout(new BorderLayout(8, 8));
        coursePerfCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel tableTitle = new JLabel("Curricular Subject-Wise Average Performance Benchmark");
        tableTitle.setFont(theme.getBoldFont(14));
        tableTitle.setForeground(theme.getPrimaryText());
        coursePerfCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Subject Code", "Subject Title", "Average Mark (/100)", "Benchmark Assessment"};
        Map<String, Double> courseScores = reportService.getCoursePerformance();
        Object[][] perfData = new Object[courseScores.size()][4];
        int i = 0;
        for (Map.Entry<String, Double> e : courseScores.entrySet()) {
            perfData[i][0] = e.getKey();
            perfData[i][1] = "Course " + e.getKey();
            perfData[i][2] = e.getValue() + " / 100";
            perfData[i][3] = e.getValue() >= 80 ? "EXCELLENT" : (e.getValue() >= 60 ? "SATISFACTORY" : "NEEDS IMPROVEMENT");
            i++;
        }

        DataTablePanel perfTable = new DataTablePanel(cols, perfData);
        coursePerfCard.add(perfTable, BorderLayout.CENTER);

        mainContent.add(coursePerfCard);

        JScrollPane scroll = new JScrollPane(mainContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Demonstrates multithreading via SwingWorker for background report generation
     * and file I/O operations without freezing the Event Dispatch Thread (EDT).
     */
    private void exportReportAsync() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("EduAsset_Audit_Report.txt"));
        int ret = chooser.showSaveDialog(mainFrame);
        if (ret != JFileChooser.APPROVE_OPTION) return;

        File targetFile = chooser.getSelectedFile();

        // Background worker
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                String reportText = reportService.generateSummaryReport();
                try (FileWriter writer = new FileWriter(targetFile)) {
                    writer.write(reportText);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(mainFrame,
                            "Report successfully exported to:\n" + targetFile.getAbsolutePath(),
                            "Export Completed", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to write report: " + ex.getMessage(),
                            "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void refreshTheme() {
        buildUI();
    }
}

