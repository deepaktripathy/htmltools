package com.deepaktripathy.htmltools;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.dom.DocumentTypeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import com.deepaktripathy.JunkUtils;

//anything common to all the tests, like filename/snippets, any setup/teardown methods etc.
public class CommonTestSetup {

	static final String filepath   = "testfiles/ET 500 Companies List 2009-2.htm";
	static final String outputpath = "testfiles/ET 500 Companies List 2009-33.htm";

	static final String referencenocasepath = "testfiles/reference_nocase.htm";
	static final String referencenocasesimplepath = "testfiles/reference_nocase_simple.htm";
	static final String referencelowercasepath    = "testfiles/reference_lowercase.htm";
	static final String referenceuppercasepath    = "testfiles/reference_uppercase.htm";
	static final String referencetextfilepath     = "testfiles/reference_textfile.htm";

	//add some dom manipulation references as well

	public static Document createReferenceDocument() throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.newDocument();

		// create the root element node
		Element element = doc.createElement("root");
		doc.appendChild(element);

		// create a comment node given the specified string
		Comment comment = doc.createComment("This is a comment");
		doc.insertBefore(comment, element);

		// add element after the first child of the root element
		Element itemElement = doc.createElement("item");
		element.appendChild(itemElement);

		// add an attribute to the node
		itemElement.setAttribute("myattr", "attrvalue");

		// create text for the node
		itemElement.insertBefore(doc.createTextNode("text"), itemElement.getLastChild());

		Utils.prettyPrint(doc);

		return doc;
	}

	/**
	 * Creates an Document with basic elements required to meet
	 * the <a href="http://www.w3.org/TR/xhtml1/#strict">XHTML standards</a>.
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <!DOCTYPE html 
	 *     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	 *     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	 * <html xmlns="http://www.w3.org/1999/xhtml">
	 *         <title>My Title</title>
	 *         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	 *     <head>
	 *     </head>
	 *     <body data-twttr-rendered="true">
	 *         <div style='opacity :0;' id="backgroundPopup"></div>
	 *     </body>
	 * </html>
	 * }
	 * </pre>
	 * 
	 * @param title desired text content for title tag. If null, no text will be added.
	 * @return basic HTML Document. 
	 */
	public static Document buildSimpleReferenceDocument() throws ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document htmlDoc = builder.newDocument();

		DocumentType docType = new DocumentTypeImpl(null, "html",
				"-//W3C//DTD XHTML 1.0 Strict//EN",
				"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		htmlDoc.appendChild(docType);
		Element htmlElement = htmlDoc.createElementNS("http://www.w3.org/1999/xhtml", "html");
		htmlDoc.appendChild(htmlElement);
		Element headElement = htmlDoc.createElement("head");
		htmlElement.appendChild(headElement);

		Element bodyElement = htmlDoc.createElement("body");
		htmlElement.appendChild(bodyElement);

		//finally build head and body children
		//buildHead(htmlDoc, headElement);
		//Element titleElement = htmlDoc.createElement("title");
		//titleElement.setTextContent("My Title");
		//headElement.appendChild(titleElement);

		addElement(htmlDoc, headElement, "title","My Title",null);
		addElement(htmlDoc, headElement, "link",null,"href=\"ET_Companies_files/css common.css\" type=\"text/css\" rel=\"stylesheet\"");

		//buildBody(htmlDoc, headElement);
		addElement(htmlDoc, bodyElement, "div", "", "style='opacity :0;' id=\"backgroundPopup\"");

		return htmlDoc;
	}


	/**
	 * Creates an Document with basic elements required to meet
	 * the <a href="http://www.w3.org/TR/xhtml1/#strict">XHTML standards</a>.
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <!DOCTYPE html 
	 *     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	 *     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	 * <html xmlns="http://www.w3.org/1999/xhtml">
	 *     <head>
	 *     </head>
	 *     <body data-twttr-rendered="true">
	 *     </body>
	 * </html>
	 * }
	 * </pre>
	 * 
	 * @param title desired text content for title tag. If null, no text will be added.
	 * @return basic HTML Document. 
	 */
	public static Document buildReferenceDocument() throws ParserConfigurationException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document htmlDoc = builder.newDocument();

		DocumentType docType = new DocumentTypeImpl(null, "html",
				"-//W3C//DTD XHTML 1.0 Strict//EN",
				"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		htmlDoc.appendChild(docType);
		Element htmlElement = htmlDoc.createElementNS("http://www.w3.org/1999/xhtml", "html");
		htmlDoc.appendChild(htmlElement);
		Element headElement = htmlDoc.createElement("head");
		htmlElement.appendChild(headElement);

		Element bodyElement = htmlDoc.createElement("body");
		htmlElement.appendChild(bodyElement);

		//finally build head and body children
		buildHead(htmlDoc, headElement);
		buildBody(htmlDoc, bodyElement);

		return htmlDoc;
	}

	/**
	 * 
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <!DOCTYPE html 
	 *     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	 *     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	 * <html xmlns="http://www.w3.org/1999/xhtml">
	 *     <head>
	 *         <title>My Title</title>
	 *         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	 *         <script async="" src="htmltools/testfiles/test_script.js"></script>
	 *         <meta http-equiv="Content-Type2" content="text/html; charset=UTF-8">
	 *         <script>
	 *             (function(_,e,rr,s){
	 *                _errs=[s];var c=_.onerror;_.onerror=function(){var a=arguments;_errs.push(a);
	 *                _.addEventListener?_.addEventListener("load",b,!1):_.attachEvent("onload",b)})
	 *                (window,document,"script","521ae8de25609c1f7900105a");
	 *         </script>
	 *         <meta content="website" name="object_type">
	 *         <style>
	 *             #adfloater {position:absolute; bottom: 0px;}
	 *             div > div#adfloater{position: fixed; }
	 *         </style>         
	 *         <link rel="publisher" href="https://plus.google.com/117381333622785235969">
	 *     </head>
	 *     <body data-twttr-rendered="true">
	 *     ...
	 *     ...
	 *     </body>
	 * </html>
	 * }
	 * </pre>
	 * @return
	 */
	private static void buildHead(Document htmlDoc, Element headElement) {
		//Element titleElement = htmlDoc.createElement("title");
		//titleElement.setTextContent("My Title");
		//headElement.appendChild(titleElement);
		addElement(htmlDoc, headElement, "title","My Title",null);

		addElement(htmlDoc, headElement, "meta",null,"http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"");
		addElement(htmlDoc, headElement, "script", "", "async=\"\" src=\"htmltools/testfiles/test_script.js\"");
		addElement(htmlDoc, headElement, "meta", null, "http-equiv=\"Content-Type2\" content=\"text/html; charset=UTF-8\"");
		addElement(htmlDoc, headElement, "script", "(function(_,e,rr,s){\"" +
				"                _errs=[s];var c=_.onerror;_.onerror=function(){var a=arguments;_errs.push(a);" +
				"                _.addEventListener?_.addEventListener(\"load\",b,!1):_.attachEvent(\"onload\",b)})" +
				"                (window,document,\"script\",\"521ae8de25609c1f7900105a\");", null);
		addElement(htmlDoc, headElement, "meta", null, "content=\"website\" name=\"object_type\"");
		addElement(htmlDoc, headElement, "style", "#adfloater {position:absolute; bottom: 0px;}" +
				"             div > div#adfloater{position: fixed; }", null);
		addElement(htmlDoc, headElement, "link", null, "rel=\"publisher\" href=\"https://plus.google.com/117381333622785235969\"");
	}

	/**
	 * 
	 * <pre>
	 * {@code
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <!DOCTYPE html 
	 *     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	 *     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
	 * <html xmlns="http://www.w3.org/1999/xhtml">
	 *     <head>
	 *     ...
	 *     ...
	 *     </head>
	 *     <body data-twttr-rendered="true">
	 *         <div style="opacity :0;" id="background & Popup"></div>
	 *         <div>
	 *            <noscript>
	 *               <img src="http://timeslog.indiatimes.com/amp;nojs=1" border="0" height="0" width="0">
	 *               <img border="0" height="0" width="0" src="http://cmstrendslog.indiatimes.com/cmsurtype=viewed">
	 *            </noscript>
	 *         </div>
	 *         <iframe allowtransparency="true" title="signupsso" class="pollbudget" id="signupsso" name="signupsso" align="middle"></iframe>
	 *         <h3>Enter a name for your WatchList</h3>
	 *         <p class="message hidden"></p>
	 *         <form><input maxlength="20" type="text"><input value="CREATE" type="submit"></form>
	 *         <a style="position: absolute;right: -4px; top: 5px;" id="popupContactClose" onclick="disablePopup();">
	 *         <table border="0" cellpadding="0" cellspacing="0" width="280">
	 *            <tbody><tr><td colspan="2"><div style="position:relative;" align="right"><a style="position: absolute;" id="popupContactClose" onclick="disablePopup();">
	 *            <span class="closebut"></span></a></div>
	 *            <iframe allowtransparency="true" title="signup_sso" src="" class="poll_budget" id="signup_sso" name="signup_sso"></iframe></td></tr></tbody>
	 *         </table>         
	 *     </body>
	 * </html>
	 * }
	 * </pre>
	 * @return
	 */
	private static void buildBody(Document htmlDoc, Element bodyElement) {
		addElement(htmlDoc, bodyElement, "div", "", "style=\"opacity :0;\" id=\"background & Popup\"");
		Element div1 = addElement(htmlDoc, bodyElement, "div", null, null);
		Element noscript1 = addElement(htmlDoc, div1, "noscript", null, null);
		addElement(htmlDoc, noscript1, "img", "", "src=\"http://timeslog.indiatimes.com/amp;nojs=1\" border=\"0\" height=\"0\" width=\"0\"");
		addElement(htmlDoc, noscript1, "img", "", "border=\"0\" height=\"0\" width=\"0\" src=\"http://cmstrendslog.indiatimes.com/cmsurtype=viewed\"");

		addElement(htmlDoc, bodyElement, "iframe", "", "allowtransparency=\"true\" title=\"signupsso\" class=\"pollbudget\" id=\"signupsso\" name=\"signupsso\" align=\"middle\"");
		addElement(htmlDoc, bodyElement, "h3", "Enter a name for your WatchList", "");
		addElement(htmlDoc, bodyElement, "p", "", "class=\"message hidden\"");
		Element form1 = addElement(htmlDoc, bodyElement, "form", null, null);
		addElement(htmlDoc, form1, "input", null, "maxlength=\"20\" type=\"text\"");
		addElement(htmlDoc, form1, "input", null, "value=\"CREATE\" type=\"submit\"");

		Element table1 = addElement(htmlDoc, bodyElement, "table", null, "border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"280\"");
		Element tbody1 = addElement(htmlDoc, table1, "tbody", null, null);
		Element tr1 = addElement(htmlDoc, table1, "tr", null, null);
		Element td1 = addElement(htmlDoc, tr1, "td", null, "colspan=\"2\"");

		Element div11 = addElement(htmlDoc, td1, "div", null, "style=\"position:relative;\" align=\"right\"");
		Element anchor1 = addElement(htmlDoc, div11, "a", null, "style=\"position: absolute;\" id=\"popupContactClose\" onclick=\"disablePopup();\"");
		Element span1 = addElement(htmlDoc, anchor1, "span", null, "class=\"closebut\"");

		Element iframe1 = addElement(htmlDoc, td1, "iframe", null, "allowtransparency=\"true\" title=\"signup_sso\" src=\"\" class=\"poll_budget\" id=\"signup_sso\" name=\"signup_sso\"");

	}

	///////////////////////////////// HELPER METHODS ////////////////////////////////////////////

	// escapes &, <, >, ", '.
	public static String escapeSpecialChars(String html) {
		//escape &, <, >, ", '
		String tmp = html;
		tmp = replaceAllOccurrences(tmp, '&', "&amp;");
		tmp = replaceAllOccurrences(tmp, '<', "&lt;");
		tmp = replaceAllOccurrences(tmp, '>', "&gt;");
		tmp = replaceAllOccurrences(tmp, '"', "&quot;");
		tmp = replaceAllOccurrences(tmp, '\'', "&#39;");

		return tmp.toString();
	}
	
	public static String replaceAllOccurrences(String sourceHtmlStr, char escapeChar, String replaceChar) {
		StringBuffer sb = new StringBuffer(sourceHtmlStr);
		int index = -1;
		while( (index = sb.indexOf("&")) > -1 ) {
			sb.deleteCharAt(index);
			sb.insert(index, "&amp;");
		}
		
		return sb.toString();
	}
	
	//Designed to do rapidfire coding by returning the modified Element.
	private static Element addElement(Document htmlDoc, Element parent, String elementName, String elementTextContent, String attributesStr) {
		Element element = htmlDoc.createElement(elementName);
		//elementTextContent = escapeSpecialChars(elementTextContent);
		if(elementTextContent != null) 
			element.setTextContent(elementTextContent);
		setAttributes(element, attributesStr);

		parent.appendChild(element);

		return element;
	}

	//ex: <div style="position:relative;" align="right">
	private static Element setAttributes(Element element, String attributesStr) {
		if(attributesStr != null && !attributesStr.isEmpty() ) {
			List<String> attributePairs = Utils.parseHtmlAttributeString(attributesStr);
			for(String attributePair : attributePairs) {
				String[] tokens = Utils.splitAttributePair(attributePair);
				if(tokens.length > 1)
					element.setAttribute(tokens[0], tokens[1]);
				else if(tokens.length == 1)
					element.setAttribute(tokens[0], null);
			}
			/*
			StringTokenizer strTok = new StringTokenizer(attributesStr, " \t");
			while(strTok.hasMoreTokens()) {
				String tok = strTok.nextToken();
				
				String[] tokens = tok.split("=");
				System.out.println("Current attributes: " + tok + ", tokens size=" + tokens.length);
				//trim doublequotes from string?
				if(tokens.length > 1)
					element.setAttribute(tokens[0], tokens[1]);
				else if(tokens.length == 1)
					element.setAttribute(tokens[0], null);
				
				List<String> tokens = parseHtmlAttributeString(tok);
				System.out.println("Current attributes: " + tok + ", tokens size=" + tokens.size());
				//trim doublequotes from string?
				if(tokens.size() > 1)
					element.setAttribute(tokens.get(0), tokens.get(1));
				else if(tokens.size() == 1)
					element.setAttribute(tokens.get(0), null);
			}
			*/
		}
		return element;
	}

	private static Element setAttributes(Element element, List<Attr> elementAttributes) {
		for(Attr n: elementAttributes)
			element.setAttributeNode(n);

		return element;
	}

}
