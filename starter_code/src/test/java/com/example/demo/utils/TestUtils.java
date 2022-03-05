package com.example.demo.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class TestUtils {

	public static URI getUri(String rawPath) throws URISyntaxException {
		// the following accounts for spaces in the URL
		String escapedPath = new URI("dummy",rawPath, null).getRawSchemeSpecificPart();
		return new URI(escapedPath);
	}

}
