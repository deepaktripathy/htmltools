package com.deepaktripathy;
import java.util.Arrays;

import com.deepaktripathy.htmltools.Utils;


public class JunkUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//String realWorldString = "async=\"\"	 src=\"htmltools/testfiles/test script.js\"";
		String realWorldString = "async=\'123	 231\'	 src=\"htmltools/testfiles/test script.js\"";
		//String realWorldString = "style=\"opacity:0;\" id=\"backgroundPopup\"";
		//String realWorldString = "class=\"message hidden\"";
		//JunkUtils.parseHtmlAttributeString2(realWorldString);
		
		String realAttribute = "src=\"htmltools/testfiles/test script.js\"";
		//String realAttribute = " helloworld ";
		Utils.splitAttributePairs(realAttribute);
	}

}
