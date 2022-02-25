package com.example.demo;

import org.junit.platform.suite.api.ExcludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.example.demo")
@ExcludeClassNamePatterns({"^.*IntegrationTests?$"})
public class UnitTestSuite {

}
