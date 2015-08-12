package com.deepaktripathy.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;

import com.deepaktripathy.htmltools.AbstractDomParser;
import com.deepaktripathy.htmltools.Utils;

public class JerichoHtmlParser extends AbstractDomParser{

	private InputStream is = null;
	Tidy tidy = null;
	Document doc = null;//cache the document for getting text content

	public JerichoHtmlParser(String uriToHtmlFile) throws MalformedURLException, IOException{
		is = Utils.uriToInputStream(uriToHtmlFile);
		configure();
	}

	public JerichoHtmlParser(InputStream htmlStream) {
		this.is = is;
		configure();
	}

	private void configure() {
		//net.htmlparser.jericho
		net.htmlparser.jericho.Config.LoggerProvider=net.htmlparser.jericho.LoggerProvider.DISABLED;
		net.htmlparser.jericho.BasicLogFormatter.OutputLevel = false;
		//Config.LoggerProvider=LoggerProvider.DISABLED;
	}
	/**
	 * Returns the name of this parser
	 * @return
	 */
	public String getName() {
		return PARSER_NAME_JERICHO;
	}

	/**
	 * Would process a file/url/inputsource etc as contained inside the implementer
	 * @return
	 */
	public Document parseDocument() {

		org.w3c.dom.Document doc = null;
		try {
			//net.htmlparser.jericho.BasicLogFormatter = 
			//Source source=new Source(new URL(url));
			Source source=new Source(is);
			List<net.htmlparser.jericho.Element> elementList=source.getAllElements();
			int jsCount = 0; 
			for (net.htmlparser.jericho.Element element : elementList) {
				//System.out.println("-------------------------------------------------------------------------------");
				//System.out.println(element.getDebugInfo());
				//if (element.getAttributes()!=null) 
				//	System.out.println("XHTML StartTag:\n"+element.getStartTag().tidy(true));
				//System.out.println("Source text with content:\n"+element);
				if (element.getName().equalsIgnoreCase("script")) {
					jsCount++;
					String content = element.getContent().toString().trim();
					if(content.length() == 0)//then it is a call to a file?
						content = element.getStartTag().toString();
					if(content.length() == 0)//still empty? then dump the element
						content = element.toString();
					System.out.println("SCRIPT content-" + content);
				}
				else
					System.out.println("Normal content-" + element.toString());
			}
			//System.out.println(source.getCacheDebugInfo());//this would dump the debug data
			System.out.println("Script count:" + jsCount);

			//return elementList;
			//TODO: generate and return a DOM & add a filter that would filter in-place, making it faster.
			//recursive calls to Element.getChildElements() as long as the document is well formed.
			doc = jerichoBuildDocument(elementList);
		}
		catch(Exception ex) {
			//ex.printStackTrace();
			parseStatus = ex;
		}

		return doc;
	}

	/**
	 * This will only process any html fragments and return a list of nodes/elements as available.
	 * Since an element can contain children elements, we cannot mention the tpe.
	 * @param fragment
	 * @return
	 */
	//public Node parse(String fragment);


	/**
	 * Returns the text content.
	 * @return
	 */
	public String getTextContent() {
		try {
			Source source=new Source(is);
			// Call fullSequentialParse manually as most of the source will be parsed.
			source.fullSequentialParse();
			TextExtractor textExtractor=new TextExtractor(source);/* {
			public boolean excludeElement(StartTag startTag) {
				return startTag.getName()==HTMLElementName.P || "control".equalsIgnoreCase(startTag.getAttributeValue("class"));
			}
		};*/

			textExtractor.setIncludeAttributes(true);
			return textExtractor.toString();
		}
		catch(IOException ex) {
			ex.printStackTrace();
			this.parseStatus = ex;
		}
		return null;
	}

	private int outputCase;
	/**
	 * Defines the case type of the output document
	 * @param outputCase
	 */
	public void setOutputCase(final int outputCase) {
		this.outputCase = outputCase;
	}


	private org.w3c.dom.Document jerichoBuildDocument(List<net.htmlparser.jericho.Element> jElements) throws ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		org.w3c.dom.Document doc = builder.newDocument();
		// create the root element node
		org.w3c.dom.Element rootElement0 = doc.createElement("root");
		doc.appendChild(rootElement0);

		org.w3c.dom.Element rootElement = doc.getDocumentElement();

		//org.w3c.dom.Node node = doc.createElement("newnode");
		Iterator<net.htmlparser.jericho.Element> iter = jElements.iterator();
		while(iter.hasNext()) {
			net.htmlparser.jericho.Element jElement = iter.next();
			//org.w3c.dom.Node node = doc.createElement(element.getName());
			System.out.println("Creating element from jericho element with name: " + jElement.getName());
			//skip it if this is a doctype declaration
			if(! jElement.getName().equalsIgnoreCase("!doctype")) {
				org.w3c.dom.Element element = doc.createElement(jElement.getName());
				//populate the rest as well as the children inside the recursive routine
				rootElement.appendChild(element);
				jerichoElementToNode(jElement, element, doc);
			}
		}
		return doc;
	}

	private void jerichoElementToNode(net.htmlparser.jericho.Element jElement, org.w3c.dom.Element element, org.w3c.dom.Document doc) {
		//first dump this element
		if(jElement != null) {
			//first set the attributes
			net.htmlparser.jericho.Attributes jAttributes = jElement.getAttributes();
			System.out.println("Jericho element name "+jElement.getName()+", attribute count: " + jAttributes.getCount());
			for(int jIndex = 0; jIndex < jAttributes.getCount(); jIndex++) {
				net.htmlparser.jericho.Attribute jAttribute = jAttributes.get(jIndex);
				element.setAttribute(convertCase(jAttribute.getName()), convertCase(jAttribute.getValue()));
			}

			//then add the children, if any
			List<net.htmlparser.jericho.Element> jChildren = jElement.getChildElements();
			System.out.println("Jericho element name "+jElement.getName()+", children count: " + jChildren.size());
			if(jChildren != null && jChildren.size() > 0) {
				Iterator<net.htmlparser.jericho.Element> iter = jChildren.iterator();
				while(iter.hasNext()) {
					net.htmlparser.jericho.Element jChElement = iter.next();
					System.out.println("Creating child element name "+jElement.getName());
					org.w3c.dom.Element chElement = doc.createElement(jChElement.getName());
					element.appendChild(chElement);

					//recurse down.
					System.out.println("RECURSING down to Jericho element: " + jElement.getName());
					jerichoElementToNode(jChElement, chElement, doc);
				}				
			}
			else {
				//has no children, may be one of the special tags, like javascript/server etc, add any text content
				System.out.println("Jericho element name "+jElement.getName()+", text content: " + jElement.getContent().toString());
				element.setTextContent(jElement.getContent().toString());
			}
		}
	}

	//converts the case based on stored parameters
	//DO We care about the locale?
	private String convertCase(String value) {
		if(value == null)
			return value;
		
		switch(outputCase) {
		case K_OUTPUT_LOWER_CASE:
			return value.toLowerCase();
		case K_OUTPUT_UPPER_CASE:
			return value.toUpperCase();
		}
		return value;
	}
}
