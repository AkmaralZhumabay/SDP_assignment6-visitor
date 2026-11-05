package com.inspection;

import com.inspection.element.*;
import com.inspection.visitor.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Create project directories
        Directory project = new Directory("fintech-project");
        Directory src = new Directory("src");
        Directory tests = new Directory("tests");
        Directory config = new Directory("config");

        // Nested directory for additional source files
        Directory utils = new Directory("utils");

        // Production source files
        src.add(new SourceCodeFile(
                "PaymentService.java", 200, 12, true, true
        ));

        src.add(new SourceCodeFile(
                "UserService.java", 150, 6, false, false
        ));

        // Nested source file
        utils.add(new SourceCodeFile(
                "ValidationUtils.java", 80, 4, false, false
        ));

        src.add(utils);

        // Test files
        tests.add(new TestFile(
                "PaymentServiceTest.java", 100, 5, "PaymentService"
        ));

        // Configuration files
        config.add(new ConfigurationFile(
                "application.properties", 30, true, false, true
        ));

        // Dependency file
        DependencyFile pom = new DependencyFile(
                "pom.xml",
                List.of(
                        new DependencyFile.Dependency(
                                "spring-core", "6.1.0", false, false
                        ),
                        new DependencyFile.Dependency(
                                "old-library", "1.0", true, false
                        ),
                        new DependencyFile.Dependency(
                                "unapproved-library", "", false, true
                        )
                )
        );

        // Build the project structure
        project.add(src);
        project.add(tests);
        project.add(config);
        project.add(pom);

        // Run all Visitors on the same project
        System.out.println("SMART CODE INSPECTION ENGINE");
        System.out.println("Project: " + project.getName());

        SecurityAuditVisitor security = new SecurityAuditVisitor();
        project.accept(security);
        security.printReport();

        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        project.accept(statistics);
        statistics.printReport();

        QualityGateVisitor quality = new QualityGateVisitor();
        project.accept(quality);
        quality.printReport();

        ComplianceVisitor compliance = new ComplianceVisitor();
        project.accept(compliance);
        compliance.printReport();

        TechnicalDebtVisitor debt = new TechnicalDebtVisitor();
        project.accept(debt);
        debt.printReport();
    }
}
