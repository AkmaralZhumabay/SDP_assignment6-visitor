package com.inspection.visitor;

import com.inspection.element.*;

public class QualityGateVisitor implements ProjectVisitor {

    private final SecurityAuditVisitor security = new SecurityAuditVisitor();
    private final ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();

    @Override
    public void visit(SourceCodeFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(TestFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(ConfigurationFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(DependencyFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getChildren()) {
            element.accept(this);
        }
    }

    public boolean isSecurityPassed() {
        return security.getCriticalIssues() == 0;
    }

    public boolean isComplexityPassed() {
        return statistics.getAverageComplexity() <= 10;
    }

    public boolean isTestsPassed() {
        return statistics.getSourceFiles() > 0
                && statistics.getTestSourceRatio() >= 0.40;
    }

    public boolean isReleaseReady() {
        return isSecurityPassed()
                && isComplexityPassed()
                && isTestsPassed();
    }

    private String status(boolean passed) {
        return passed ? "PASS" : "FAIL";
    }

    public void printReport() {
        System.out.println("\n QUALITY GATE REPORT ");

        System.out.println("Security: " + status(isSecurityPassed()));
        System.out.println("Complexity: " + status(isComplexityPassed()));
        System.out.println("Tests: " + status(isTestsPassed()));

        System.out.println("\nFINAL RESULT: "
                + (isReleaseReady() ? "PASSED" : "FAILED"));
    }
}
