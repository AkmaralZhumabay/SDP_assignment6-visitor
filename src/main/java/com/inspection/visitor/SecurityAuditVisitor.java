package com.inspection.visitor;

import com.inspection.element.*;
import java.util.ArrayList;
import java.util.List;

public class SecurityAuditVisitor implements ProjectVisitor {

    private final List<String> issues = new ArrayList<>();
    private int criticalIssues = 0;
    private int highIssues = 0;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hasHardcodedCredentials()) {
            addCritical("Hardcoded credentials in " + file.getName());
        }

        if (file.isHighRisk()) {
            addHigh("High-risk source file: " + file.getName());
        }
    }

    @Override
    public void visit(TestFile file) {
        // No security rules for test files yet.
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.hasExposedCredentials()) {
            addCritical("Exposed credentials in " + file.getName());
        }

        if (file.isDebugEnabled()) {
            addHigh("Debug mode enabled in " + file.getName());
        }

        if (!file.isEncryptionEnabled()) {
            addHigh("Encryption disabled in " + file.getName());
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (DependencyFile.Dependency dependency : file.getDependencies()) {
            if (dependency.vulnerable()) {
                addCritical("Vulnerable dependency: " + dependency.name());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getChildren()) {
            element.accept(this);
        }
    }

    private void addCritical(String message) {
        criticalIssues++;
        issues.add("[CRITICAL] " + message);
    }

    private void addHigh(String message) {
        highIssues++;
        issues.add("[HIGH] " + message);
    }

    public int getCriticalIssues() {
        return criticalIssues;
    }

    public int getHighIssues() {
        return highIssues;
    }

    public List<String> getIssues() {
        return List.copyOf(issues);
    }

    public int getSecurityScore() {
        return Math.max(0, 100 - criticalIssues * 20 - highIssues * 10);
    }

    public void printReport() {
        System.out.println("\n SECURITY AUDIT REPORT ");

        if (issues.isEmpty()) {
            System.out.println("No security issues found.");
        } else {
            for (String issue : issues) {
                System.out.println(issue);
            }
        }

        System.out.println("Critical issues: " + criticalIssues);
        System.out.println("High issues: " + highIssues);
        System.out.println("Security score: " + getSecurityScore() + "/100");
    }
}
