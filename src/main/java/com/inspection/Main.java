package com.inspection;

import com.inspection.element.*;
import com.inspection.visitor.SecurityAuditVisitor;
import java.util.List;
import com.inspection.visitor.ProjectStatisticsVisitor;

public class Main {

    public static void main(String[] args) {

        Directory project = new Directory("fintech-project");
        Directory src = new Directory("src");
        Directory config = new Directory("config");
        Directory tests = new Directory("tests");

        src.add(new SourceCodeFile(
                "PaymentService.java", 200, 12, true, true
        ));

        src.add(new SourceCodeFile(
                "UserService.java", 150, 6, false, false
        ));

        config.add(new ConfigurationFile(
                "application.properties", 30, true, false, true
        ));

        tests.add(new TestFile(
                "PaymentServiceTest.java", 100, 5, "PaymentService"
        ));

        project.add(src);
        project.add(config);
        project.add(tests);

        project.add(new DependencyFile(
                "pom.xml",
                List.of(
                        new DependencyFile.Dependency(
                                "spring-core", "6.1.0", false, false
                        ),
                        new DependencyFile.Dependency(
                                "old-library", "1.0", true, false
                        )
                )
        ));

        SecurityAuditVisitor security = new SecurityAuditVisitor();
        project.accept(security);
        security.printReport();

        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        project.accept(statistics);
        statistics.printReport();
    }
}
