package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

public class SourceCodeFile implements ProjectElement {

    private final String name;
    private final int linesOfCode;
    private final int complexity;
    private final boolean hasHardcodedCredentials;
    private final boolean highRisk;

    public SourceCodeFile(String name, int linesOfCode, int complexity,
                          boolean hasHardcodedCredentials, boolean highRisk) {
        this.name = name;
        this.linesOfCode = linesOfCode;
        this.complexity = complexity;
        this.hasHardcodedCredentials = hasHardcodedCredentials;
        this.highRisk = highRisk;
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getComplexity() {
        return complexity;
    }

    public boolean hasHardcodedCredentials() {
        return hasHardcodedCredentials;
    }

    public boolean isHighRisk() {
        return highRisk;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
