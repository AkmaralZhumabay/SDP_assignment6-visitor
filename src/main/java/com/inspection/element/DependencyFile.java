package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;
import java.util.List;

public class DependencyFile implements ProjectElement {

    public record Dependency(
            String name,
            String version,
            boolean vulnerable,
            boolean prohibited
    ) {}

    private final String name;
    private final List<Dependency> dependencies;

    public DependencyFile(String name, List<Dependency> dependencies) {
        this.name = name;
        this.dependencies = List.copyOf(dependencies);
    }

    public String getName() {
        return name;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
