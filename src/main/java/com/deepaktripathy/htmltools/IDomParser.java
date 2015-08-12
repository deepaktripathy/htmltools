package com.deepaktripathy.htmltools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


public interface IDomParser {
	
	public static final String PARSER_NOT_AVAILABLE = "PARSER_NOT_AVAILABLE";
	public static final String PARSER_NAME_TIDY = "TIDY";
	public static final String PARSER_NAME_JERICHO = "JERICHO";
	
	public static final int K_OUTPUT_NO_CASE = 0;
	public static final int K_OUTPUT_LOWER_CASE = 1;
	public static final int K_OUTPUT_UPPER_CASE = 2;

	
	/**
	 * Returns the name of this parser
	 * @return
	 */
	public String getName();
	
	/**
	 * Would process a file/url/inputsource etc as contained inside the implementer
	 * @return
	 */
	public Document parseDocument();

	/**
	 * This will only process any html fragments and return a list of nodes/elements as available.
	 * Since an element can contain children elements, we cannot mention the tpe.
	 * @param fragment
	 * @return
	 */
	public Node parse(String fragment);

	
	/**
	 * Returns the parsing status, usually there is some sort of error returned from the underlying library.
	 * @return
	 */
	public Object getStatus();
	
	
	/**
	 * Returns the text content.
	 * @return
	 */
	public String getTextContent();
	
	/**
	 * Defines the case type of the output document
	 * 
	 * @param outputCase
	 */
	public void setOutputCase(int outputCase);
}
