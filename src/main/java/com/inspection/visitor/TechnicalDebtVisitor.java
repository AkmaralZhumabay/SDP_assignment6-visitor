package com.inspection.visitor;

import com.inspection.element.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TechnicalDebtVisitor implements ProjectVisitor {

    private final List<String> issues = new ArrayList<>();
    private final Set<String> sourceClasses = new HashSet<>();
    private final Set<String> testedClasses = new HashSet<>();

    private int estimatedHours = 0;

    @Override
    public void visit(SourceCodeFile file) {
        String className = file.getName().replaceFirst("\\.java$", "");
        sourceClasses.add(className);

        if (file.getComplexity() > 10) {
            addIssue("High complexity in " + file.getName(), 2);
        }

        if (file.isHighRisk()) {
            addIssue("High-risk source file: " + file.getName(), 3);
        }
    }

    @Override
    public void visit(TestFile file) {
        testedClasses.add(file.getTestedClass());
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.isDebugEnabled()) {
            addIssue("Debug mode enabled in " + file.getName(), 1);
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (DependencyFile.Dependency dependency : file.getDependencies()) {
            if (dependency.vulnerable()) {
                addIssue("Vulnerable dependency: " + dependency.name(), 2);
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getChildren()) {
            element.accept(this);
        }
    }

    private void addIssue(String message, int hours) {
        String unit = hours == 1 ? " hour" : " hours";
        issues.add(message + " (" + hours + unit + ")");
        estimatedHours += hours;
    }

    public List<String> getIssues() {
        List<String> allIssues = new ArrayList<>(issues);

        for (String className : sourceClasses) {
            if (!testedClasses.contains(className)) {
                allIssues.add("Missing tests for " + className + " (2 hours)");
            }
        }

        return allIssues;
    }

    public int getEstimatedHours() {
        int total = estimatedHours;

        for (String className : sourceClasses) {
            if (!testedClasses.contains(className)) {
                total += 2;
            }
        }

        return total;
    }

    public void printReport() {
        System.out.println("\n TECHNICAL DEBT REPORT ");

        List<String> allIssues = getIssues();

        if (allIssues.isEmpty()) {
            System.out.println("No technical debt detected.");
        } else {
            for (String issue : allIssues) {
                System.out.println("[DEBT] " + issue);
            }
        }

        System.out.println("Total debt issues: " + allIssues.size());
        System.out.println("Estimated work: " + getEstimatedHours() + " hours");
    }
}
