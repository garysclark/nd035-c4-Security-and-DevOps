package com.example.demo;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.example.demo")
@IncludeClassNamePatterns({"^.*IntegrationTests?$"})
public class IntegrationTestSuite {

}
