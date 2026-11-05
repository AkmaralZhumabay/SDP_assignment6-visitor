package com.inspection.visitor;

import com.inspection.element.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComplianceVisitor implements ProjectVisitor {

    private final List<String> violations = new ArrayList<>();
    private final Set<String> sourceClasses = new HashSet<>();
    private final Set<String> testedClasses = new HashSet<>();

    @Override
    public void visit(SourceCodeFile file) {
        String className = file.getName().replaceFirst("\\.java$", "");
        sourceClasses.add(className);

        if (file.hasHardcodedCredentials()) {
            violations.add("Exposed credentials in " + file.getName());
        }
    }

    @Override
    public void visit(TestFile file) {
        testedClasses.add(file.getTestedClass());
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.hasExposedCredentials()) {
            violations.add("Exposed credentials in " + file.getName());
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (DependencyFile.Dependency dependency : file.getDependencies()) {

            if (dependency.version() == null
                    || dependency.version().isBlank()) {
                violations.add("Missing version: " + dependency.name());
            }

            if (dependency.prohibited()) {
                violations.add("Prohibited dependency: " + dependency.name());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
        for (ProjectElement element : directory.getChildren()) {
            element.accept(this);
        }
    }

    public List<String> getViolations() {
        List<String> allViolations = new ArrayList<>(violations);

        for (String className : sourceClasses) {
            if (!testedClasses.contains(className)) {
                allViolations.add("Missing tests for " + className);
            }
        }

        return allViolations;
    }

    public boolean isCompliant() {
        return getViolations().isEmpty();
    }

    public void printReport() {
        System.out.println("\n COMPLIANCE REPORT ");

        List<String> allViolations = getViolations();

        if (allViolations.isEmpty()) {
            System.out.println("All compliance rules passed.");
        } else {
            for (String violation : allViolations) {
                System.out.println("[VIOLATION] " + violation);
            }
        }

        System.out.println("Total violations: " + allViolations.size());
        System.out.println("Compliance: " + (isCompliant() ? "PASS" : "FAIL"));
    }
}
