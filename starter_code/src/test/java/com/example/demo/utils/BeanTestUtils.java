package com.example.demo.utils;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

public final class BeanTestUtils {

	private BeanTestUtils() {}
	
	public static void testBean(Class<?> beanClass) {
        assertThat(beanClass, allOf(
                hasValidBeanConstructor(),
                hasValidGettersAndSetters(),
                hasValidBeanHashCode(),
                hasValidBeanEquals()
                ));
	}

	public static void testSimpleBean(Class<?> beanClass) {
        assertThat(beanClass, allOf(
                hasValidBeanConstructor(),
                hasValidGettersAndSetters()
                ));
	}

}