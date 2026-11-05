package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

public class TestFile implements ProjectElement {

    private final String name;
    private final int linesOfCode;
    private final int testCount;
    private final String testedClass;

    public TestFile(String name, int linesOfCode, int testCount,
                    String testedClass) {
        this.name = name;
        this.linesOfCode = linesOfCode;
        this.testCount = testCount;
        this.testedClass = testedClass;
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getTestCount() {
        return testCount;
    }

    public String getTestedClass() {
        return testedClass;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
