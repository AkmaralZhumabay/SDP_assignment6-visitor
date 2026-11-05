package com.inspection;

import com.inspection.element.*;
import com.inspection.visitor.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorTest {

    @Test
    void sourceFileStatistics() {
        SourceCodeFile file = new SourceCodeFile(
                "Payment.java", 120, 8, false, false);

        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getSourceFiles());
        assertEquals(120, visitor.getTotalLinesOfCode());
        assertEquals(8.0, visitor.getAverageComplexity());
    }

    @Test
    void testFileStatistics() {
        TestFile file = new TestFile(
                "PaymentTest.java", 50, 4, "Payment");

        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getTestFiles());
        assertEquals(50, visitor.getTotalLinesOfCode());
    }

    @Test
    void configurationFileStatistics() {
        ConfigurationFile file = new ConfigurationFile(
                "app.properties", 25, false, true, false);

        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getTotalFiles());
        assertEquals(25, visitor.getTotalLinesOfCode());
    }

    @Test
    void dependencyFileStatistics() {
        DependencyFile file = new DependencyFile(
                "pom.xml",
                List.of(
                        new DependencyFile.Dependency(
                                "spring", "6.0", false, false),
                        new DependencyFile.Dependency(
                                "junit", "5.0", false, false)
                ));

        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getTotalFiles());
        assertEquals(2, visitor.getTotalDependencies());
    }

    @Test
    void detectsHardcodedCredentials() {
        SourceCodeFile file = new SourceCodeFile(
                "Login.java", 100, 5, true, false);

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getCriticalIssues());
        assertEquals(80, visitor.getSecurityScore());
        assertTrue(visitor.getIssues().get(0)
                .contains("Hardcoded credentials"));
    }

    @Test
    void detectsVulnerableDependency() {
        DependencyFile file = new DependencyFile(
                "pom.xml",
                List.of(new DependencyFile.Dependency(
                        "unsafe-library", "1.0", true, false)));

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getCriticalIssues());
        assertEquals(80, visitor.getSecurityScore());
    }

    @Test
    void detectsInsecureConfiguration() {
        ConfigurationFile file = new ConfigurationFile(
                "application.properties", 30, true, false, true);

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        file.accept(visitor);

        assertEquals(1, visitor.getCriticalIssues());
        assertEquals(2, visitor.getHighIssues());
        assertEquals(60, visitor.getSecurityScore());
    }

    @Test
    void calculatesProjectStatistics() {
        Directory project = new Directory("project");

        project.add(new SourceCodeFile(
                "A.java", 100, 6, false, false));
        project.add(new SourceCodeFile(
                "B.java", 200, 10, false, false));
        project.add(new TestFile(
                "ATest.java", 50, 3, "A"));

        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        project.accept(visitor);

        assertEquals(3, visitor.getTotalFiles());
        assertEquals(350, visitor.getTotalLinesOfCode());
        assertEquals(8.0, visitor.getAverageComplexity());
        assertEquals(0.5, visitor.getTestSourceRatio());
    }

    @Test
    void qualityGatePasses() {
        Directory project = new Directory("safe-project");

        project.add(new SourceCodeFile(
                "Payment.java", 100, 6, false, false));
        project.add(new TestFile(
                "PaymentTest.java", 50, 3, "Payment"));

        QualityGateVisitor visitor = new QualityGateVisitor();
        project.accept(visitor);

        assertTrue(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertTrue(visitor.isReleaseReady());
    }

    @Test
    void qualityGateFails() {
        Directory project = new Directory("unsafe-project");

        project.add(new SourceCodeFile(
                "Payment.java", 100, 15, true, true));

        QualityGateVisitor visitor = new QualityGateVisitor();
        project.accept(visitor);

        assertFalse(visitor.isSecurityPassed());
        assertFalse(visitor.isComplexityPassed());
        assertFalse(visitor.isTestsPassed());
        assertFalse(visitor.isReleaseReady());
    }

    @Test
    void complianceDetectsCredentials() {
        SourceCodeFile file = new SourceCodeFile(
                "Login.java", 100, 5, true, false);

        ComplianceVisitor visitor = new ComplianceVisitor();
        file.accept(visitor);

        assertTrue(visitor.getViolations().stream()
                .anyMatch(v -> v.contains("Exposed credentials")));
        assertFalse(visitor.isCompliant());
    }

    @Test
    void complianceDetectsDependencyViolations() {
        DependencyFile file = new DependencyFile(
                "pom.xml",
                List.of(new DependencyFile.Dependency(
                        "blocked-library", "", false, true)));

        ComplianceVisitor visitor = new ComplianceVisitor();
        file.accept(visitor);

        assertEquals(2, visitor.getViolations().size());
        assertFalse(visitor.isCompliant());
    }

    @Test
    void complianceDetectsMissingTests() {
        Directory project = new Directory("project");

        project.add(new SourceCodeFile(
                "Payment.java", 100, 5, false, false));

        ComplianceVisitor visitor = new ComplianceVisitor();
        project.accept(visitor);

        assertTrue(visitor.getViolations()
                .contains("Missing tests for Payment"));
    }

    @Test
    void visitsNestedDirectories() {
        Directory root = new Directory("root");
        Directory src = new Directory("src");
        Directory utils = new Directory("utils");

        utils.add(new SourceCodeFile(
                "Helper.java", 75, 4, false, false));

        src.add(utils);
        root.add(src);

        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        root.accept(visitor);

        assertEquals(1, visitor.getTotalFiles());
        assertEquals(75, visitor.getTotalLinesOfCode());
    }

    @Test
    void handlesEmptyDirectory() {
        Directory empty = new Directory("empty");

        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        empty.accept(statistics);

        assertEquals(0, statistics.getTotalFiles());
        assertEquals(0.0, statistics.getAverageComplexity());
        assertEquals(0.0, statistics.getTestSourceRatio());

        QualityGateVisitor quality = new QualityGateVisitor();
        empty.accept(quality);

        assertFalse(quality.isReleaseReady());
    }

    @Test
    void calculatesTechnicalDebt() {
        Directory project = new Directory("project");

        project.add(new SourceCodeFile(
                "Payment.java", 100, 12, false, true));
        project.add(new TestFile(
                "PaymentTest.java", 50, 3, "Payment"));

        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        project.accept(visitor);

        assertEquals(5, visitor.getEstimatedHours());
        assertEquals(2, visitor.getIssues().size());
    }

    @Test
    void securityScoreCannotBeNegative() {
        Directory project = new Directory("unsafe-project");

        for (int i = 0; i < 10; i++) {
            project.add(new SourceCodeFile(
                    "Unsafe" + i + ".java",
                    100, 15, true, true));
        }

        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        project.accept(visitor);

        assertEquals(10, visitor.getCriticalIssues());
        assertEquals(10, visitor.getHighIssues());
        assertEquals(0, visitor.getSecurityScore());
    }

    @Test
    void missingTestsAreNotCountedTwice() {
        Directory project = new Directory("project");

        project.add(new SourceCodeFile(
                "Payment.java", 100, 5, false, false));

        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        project.accept(visitor);

        assertEquals(2, visitor.getEstimatedHours());
        assertEquals(2, visitor.getEstimatedHours());
        assertEquals(1, visitor.getIssues().size());
    }
}
