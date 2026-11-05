package com.inspection.element;

import com.inspection.visitor.ProjectVisitor;

public class ConfigurationFile implements ProjectElement {

    private final String name;
    private final int linesOfCode;
    private final boolean debugEnabled;
    private final boolean encryptionEnabled;
    private final boolean hasExposedCredentials;

    public ConfigurationFile(String name, int linesOfCode,
                             boolean debugEnabled, boolean encryptionEnabled,
                             boolean hasExposedCredentials) {
        this.name = name;
        this.linesOfCode = linesOfCode;
        this.debugEnabled = debugEnabled;
        this.encryptionEnabled = encryptionEnabled;
        this.hasExposedCredentials = hasExposedCredentials;
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public boolean hasExposedCredentials() {
        return hasExposedCredentials;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
