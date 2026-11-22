package com.examsphere.interfaces;

/**
 * Interface for entities capable of generating and exporting reports.
 * <p>
 * Implementations produce a textual report and can write it to a file.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public interface ReportGenerator {

    /**
     * Generates a textual report summarizing the entity's data.
     *
     * @return the generated report as a {@code String}
     */
    String generateReport();

    /**
     * Exports the generated report to the specified file path.
     *
     * @param filePath the absolute or relative path of the output file
     */
    void exportReport(String filePath);
}
