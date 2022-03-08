package com.vilson.runner.tests;

import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TestsGrouper {

    public static List<XmlClass> groupTestsByClass(List<Test> tests) {
        Map<String, List<XmlInclude>> groupedByFQClassName = testsGroupedByClasses(tests);

        return groupedByFQClassName.entrySet().stream()
                .map(TestsGrouper::composeXmlClass)
                .collect(Collectors.toList());
    }

    private static Map<String, List<XmlInclude>> testsGroupedByClasses(List<Test> tests) {
        Collector<String, ?, List<XmlInclude>> methodNamesToXmlIncludesList =
                Collectors.mapping(XmlInclude::new, Collectors.toList());
        Collector<Test, ?, List<XmlInclude>> testsToXmlIncludesList =
                Collectors.mapping(Test::getMethodName, methodNamesToXmlIncludesList);

        return tests.stream().collect(Collectors.groupingBy(Test::getFQClassName, testsToXmlIncludesList));
    }

    private static XmlClass composeXmlClass(Map.Entry<String, List<XmlInclude>> entry) {
        XmlClass xmlClass = new XmlClass(entry.getKey());
        xmlClass.setIncludedMethods(entry.getValue());
        return xmlClass;
    }
}
