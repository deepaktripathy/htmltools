package com.deepaktripathy.parsers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

import com.deepaktripathy.htmltools.AbstractDomParser;
import com.deepaktripathy.htmltools.Utils;

public class JTidyParser extends AbstractDomParser{
	
	private InputStream is = null;
	Tidy tidy = null;
	Document doc = null;//cache the document for getting text content
	
	public JTidyParser(String uriToHtmlFile) throws MalformedURLException, IOException{
		is = Utils.uriToInputStream(uriToHtmlFile);
		configure();
	}
	
	public JTidyParser(InputStream htmlStream) {
		this.is = is;
		configure();
	}

	//TODO: default constructor, remove after we have a common access from the factory
	public JTidyParser() {
		configure();
	}
	
	/**
	 * TODO: TEmporary method, remove after we get a common way to create the parsers via the factory 
	 * instead of calling the default constructor.
	 * 
	 * @param htmlStream
	 */
	public void setInput(InputStream htmlStream) {
		this.is = htmlStream;
	}
	
	private void configure() {
		tidy = new Tidy();
		
		//CONFIGURE tidy from a config files
		//Create any file like J:/config.txt.
		//add whatever parameters like "new-inline-tags: noindex" to it.
		//then use "tidy.setConfigurationFromFile("J:/config.txt");" to load the config

		// configure Tidy instance as required
		//tidy.setUpperCaseTags(!tagsLowerCase);
		//tidy.setUpperCaseAttrs(!tagsLowerCase);

		//tidy.setWraplen(0);
		//tidy.setWrapAttVals(false);
		
		//tidy.setXHTML(true);

		//TODO: TIDY trims newlines, How to fix this?
		//suggested that you either preprocess your HTML, 
		//or put a hack in StreamInImpl.readChar.
		tidy.setShowErrors(0);//#of error lines to show
		tidy.setShowWarnings(false);
		tidy.setQuiet(true);//no messages like debug messages
		//tidy.setErrout(null);//does NOT work, creates a null Document.
		
		//followings are for bad html
		//tidy.setForceOutput(true);
		//tidy.setMakeClean(true);
		
		//to get actual output without any cleaning/conversion
		tidy.setMakeBare(true);
		tidy.setMakeClean(true);
		tidy.setRawOut(true);
	}
	
	/**
	 * Returns the name of this parser
	 * @return
	 */
	public String getName() {
		return PARSER_NAME_TIDY;
	}
	
	/**
	 * Would process a file/url/inputsource etc as contained inside the implementer
	 * @return
	 */
	public Document parseDocument() {
		org.w3c.dom.Document doc = null;
		
		try {
			System.out.println("JTidy parsing InputStream...");
			doc = tidy.parseDOM(is, null);
			System.out.println("JTidy parsing InputStream... Done");
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			parseStatus = ex;
		}
		
		this.doc = doc;
		return doc;
	}

	/**
	 * This will only process any html fragments and return a list of nodes/elements as available.
	 * Since an element can contain children elements, we cannot mention the tpe.
	 * @param fragment
	 * @return
	 */
	public Node parse(String fragment) {
		org.w3c.dom.Document doc = null;
		
		try {
			System.out.println("JTidy parsing InputStream...");
			InputStream is = Utils.toInputStream(fragment);
			doc = tidy.parseDOM(is, null);
			System.out.println("JTidy parsing InputStream... Done");
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			parseStatus = ex;
		}
		
		this.doc = doc;
		return doc;
	}

	
	/**
	 * Returns the text content in the default encoding.
	 * @return
	 */
	public String getTextContent() {
		String output = "";
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			tidy.pprint(doc, baos);
			return baos.toString();
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			parseStatus = ex;
			return null;
		}
	}

	/**
	 * Defines the case type of the output document
	 * @param outputCase
	 */
	public void setOutputCase(final int outputCase) {
		if(outputCase == K_OUTPUT_NO_CASE)
			return;
		else{
			if(outputCase == K_OUTPUT_LOWER_CASE) {
				tidy.setUpperCaseTags(false);
				tidy.setUpperCaseAttrs(false);
			}
			else{
				tidy.setUpperCaseTags(true);
				tidy.setUpperCaseAttrs(true);
			}
		}
	}

}
