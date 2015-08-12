package com.deepaktripathy.htmltools;

import static org.apache.xml.security.Init.init;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.htmlparser.jericho.Source;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Should contain any DOM related utilities.
 * 
 * TODO: Move the parser creator methods to the appropriate factory classes.
 * 
 * @author user
 *
 */
public class Utils {

	//no instance
	//private Utils() {}


	public org.w3c.dom.Document saxParseHtml(String uriToHtmlFile) throws ParserConfigurationException,
	SAXException, IOException 
	{
		//final List<String> rssLinks = new LinkedList<String>();
		final URL url = new URL(uriToHtmlFile);
		final org.w3c.dom.Document doc = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(url.openStream());
		/*
		//testing
	  final NodeList linkNodes = doc.getElementsByTagName("link");
	  for(int i = 0; i < linkNodes.getLength(); i++) {
	    final Element linkElement = (Element) linkNodes.item(i);
	    rssLinks.add(linkElement.getTextContent());
	  }
	  return rssLinks;
		 */
		return doc;
	}

	//checks if a character is single/double quote (starts with) otherwise is escaped by a backslash
	private static boolean isQuotedWord(String str, int charIndex) {
		if( charIndex == 0 && (str.charAt(charIndex) == '\'') || str.charAt(charIndex) == '"')
			return true;
	
		if( charIndex > 0) {
			if ((str.charAt(charIndex-1) == '\\') && ( (str.charAt(charIndex) == '\'') || (str.charAt(charIndex) == '"') )) {
				//escaped, ordinary char, return false;
				return false;
			}
			
			if (( (str.charAt(charIndex) == '\'') || (str.charAt(charIndex) == '"') ))
				return true;
		}
		
		return false;
	}

	private static boolean isTokenChar(char ch) {
		if(   (ch > '\u0000' && ch <= '\u0020') //whitespaces, space char = \u0020
			|| ch == '\u0030'   //equals char
			//|| ch == '\u0907' || ch == '\u0908' //single quote
			//|| ch == '\u0911' || ch == '\u0912' //double quote
				)
			return true;
		else
			return false;
	}

	/*
	 * Must handle real world and ugly html attributes like:
	 * async="" src="htmltools/testfiles/test script.js" OR
	 * style="opacity: 0;" id="backgroundPopup"
	 * class="message hidden"
	 * 
	 * if finds a single/double quote then cant parse using spaces.
	 * can it handle delimited quotes like \" or \' ???
	 * LOGIC:
	 * keep tokenizing on spaces & tabs and storing the position of any single/d-quotes. 
	 * If any dQuotes are found previously as a word start boundary, marks it as a token till 
	 * finds next single/d-quote as a word end. if not found continue till finish.
	 * Exceptions are escaped \' or \" quotes, assumes them as plain characters.
	 * 
	 * THIS WILL SEPARATE THE ATTRIBUTES PAIRS ONLY, THEN THEY HAVE TO BE INDIVIDUALLY SEPARATED.
	 * 
	 * TODO: Unfinished*** 
	 */
	public static List<String> parseHtmlAttributeString(String string)  
	{
		//System.out.println("Started with input [" + string + "]");
		int index = 0;
		int lastDquote = -1;
		int startIndex = 0;
		
		List<String> attributes = new ArrayList();
		while(index < string.length()) {
			char ch = string.charAt(index);
			//System.out.println("Current char: <" + ch + ">");
			
			if(isQuotedWord(string, index)) {
				//System.out.println("Detected: Quote char: <" + ch + "> at index: " + index);
				if(lastDquote == -1)
					lastDquote = index;//mark it
				else {
					String t1 = string.substring(lastDquote, index);
					//System.out.println("Extracting quote text [" + t1 +"]" );
					//attributes.add(t1);
					lastDquote = -1;//reset it
				}
			}
			
			//tokenize only if not quoted
			if(isTokenChar(ch) && lastDquote == -1) {
				//System.out.println("Detected: Token char: <" + ch + "> at index: " + index);
				//if(lastDquote == -1) {//not quoted
					if(index > startIndex) {
						String t2 = string.substring(startIndex, index);
						//System.out.println("Extracting token text [" + t2 +"]" );
						attributes.add(t2);
						startIndex = index+1;
						index++;//cross the delimiter
					}
				//}
			}
			
			index++;
		}
		//finally handle the remaining part
		if(index > startIndex) {
			String t3 = string.substring(startIndex, index);
			//System.out.println("Extracting last token text [" + t3 +"]" );
			attributes.add(t3);
		}		
	
		//System.out.println("Result: " + attributes);
		return attributes;
	}

	/*
	 * Must handle real world and ugly html attributes like:
	 * async="" src="htmltools/testfiles/test script.js" OR
	 * style="opacity: 0;" id="backgroundPopup"
	 * class="message hidden"
	 * 
	 * if finds a single/double quote then cant parse using spaces.
	 * can it handle delimited quotes like \" or \' ???
	 * LOGIC:
	 * keep tokenizing on spaces & tabs and storing the position of any single/d-quotes. 
	 * If any dQuotes are found previously as a word start boundary, marks it as a token till 
	 * finds next single/d-quote as a word end. if not found continue till finish.
	 * Exceptions are escaped \' or \" quotes, assumes them as plain characters.
	 * TODO: Unfinished*** 
	 */
	private static List<String> parseHtmlAttributeString2(String realWorldString)  
	{
		try {
			StreamTokenizer st = new StreamTokenizer(new StringReader(realWorldString));
	
			int lastSquoteIndex = -1;
			int lastDquoteIndex = -1;
			
			//st.eolIsSignificant(true);
			st.resetSyntax();
	
			// Set all printing characters to be part of words.
			st.wordChars('\u0020', '\uFFFF');
			// Control characters and the space characters are all white space.
			st.whitespaceChars('\u0000', '\u0020');
			
			//equals char
			st.whitespaceChars('\u0030', '\u0030');
			//single quote char left/right: 0907/0908
			st.whitespaceChars('\u0907', '\u0908');
			//double quote char left/right: 0911/0912
			st.whitespaceChars('\u0911', '\u0912');
			
			int token = -1;//st.nextToken();
			while ( (token = st.nextToken()) != StreamTokenizer.TT_EOF) 
			{
				//token = st.nextToken();
				switch (token) 
				{
					case '=':
						System.out.println("Equals sign found");
						break;
					case '\'':
						System.out.println("Single quote char found");
						break;
					case '\"':
						System.out.println("Double quote char found");
						break;
					case StreamTokenizer.TT_NUMBER:
						double num = st.nval;
						System.out.println("Number found: [" + num + "]");
						//st.pushBack();
						break;
					case StreamTokenizer.TT_WORD:
						String word = st.sval;
						System.out.println("Word found: [" + word  + "]");
						break;
					default:
						System.out.println("Default token: " + token);
					/*	
					case '<':
					{
						int t = st.nextToken();
						switch(t)
						{
						case '=':
							System.out.println("<=");
							break;
						case '<':
							System.out.println("<<");
							break;
						default:
							st.pushBack();
							System.out.println("<");
							break;
						}
					}*/
				}
			}
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}

	/**
	 * Splits an attribute pair around the first equal sign without any trimming. 
	 * If there is no equals sign, returns the original String.
	 * NOTE: Does not handle escaped quotes, like \' or \"
	 * 
	 * @param attributePair
	 * @return
	 */
	public static String[] splitAttributePair(String attributePair) {
		//System.out.println("splitAttributePair() :: input [" + attributePair + "]");
		int eqIndex = attributePair.indexOf('=');
		String[] result = new String[2];
		if(eqIndex == -1)
			result[0] = attributePair.substring(0, eqIndex);
		else {
			result[0] = attributePair.substring(0, eqIndex);
			if(eqIndex < attributePair.length()) {
				int from = eqIndex+1;
				result[1] = attributePair.substring(from);
			}
		}
		//System.out.println("splitAttributePairs() :: result [" + Arrays.toString(result) + "]");
		return result;
	}

	/**
	 * Splits an attribute pair around the first equal sign without any trimming. 
	 * If there is no equals sign, returns the original String.
	 * NOTE: Does not handle escaped quotes, like \' or \"
	 * 
	 * @param attributePair
	 * @return
	 */
	public static List<String> splitAttributePairs(String attributePair) {
		//System.out.println("splitAttributePairs() :: input [" + attributePair + "]");
		int eqIndex = attributePair.indexOf('=');
		List<String> result = new ArrayList();
		if(eqIndex == -1)
			result.add(attributePair);
		else {
			result.add( attributePair.substring(0, eqIndex) );
			if(eqIndex < attributePair.length()) {
				int from = eqIndex+1;
				result.add( attributePair.substring(from));
			}
		}
		//System.out.println("splitAttributePairs() :: result [" + result + "]");
		return result;
	}

	/**
	 * Trims the single/double quote around strings. does not trim nor assume there may be (double)quotes before/after spaces.
	 * @param input
	 * @return
	 */
	public static String trimEndQuotes(String input) {
		StringBuffer sb = new StringBuffer(input);
		int index = input.length()-1;
		char ch = sb.charAt(index);
		if( ch == '\u0907' || ch == '\u0908' //single quote
		  || ch == '\u0911' || ch == '\u0912') //double quote
			sb.deleteCharAt(index);
			
		index = 0;
		ch = sb.charAt(index);
		if( ch == '\u0907' || ch == '\u0908' //single quote
		  || ch == '\u0911' || ch == '\u0912') //double quote
			sb.deleteCharAt(index);
	
		return sb.toString();
	}

	public static org.w3c.dom.Document nekoParseHtml(String uriToHtmlFile) throws IOException, SAXException{
		//Logger.getLogger("").setLevel(Level.OFF);  
		InputStream is = uriToInputStream(uriToHtmlFile);
		if(is != null) {
			return nekoParseHtml(is);
		}

		return null;
	}

	private static org.w3c.dom.Document nekoParseHtml(InputStream htmlStream) throws IOException, SAXException {
		System.out.println("Using NEKO parser to parse Document");

		org.w3c.dom.Document document = null;
		final org.cyberneko.html.parsers.DOMParser parser = new org.cyberneko.html.parsers.DOMParser();

		//dont care about case, as the tags would all be in uppercase.
		parser.parse(new InputSource(htmlStream));
		document = parser.getDocument();

		return document;
	}	


	public static InputStream uriToInputStream(String uriToHtmlFile) throws MalformedURLException, IOException{
		File file = new File(uriToHtmlFile);
		URL urlObj = file.toURI().toURL();
		URLConnection connection = urlObj.openConnection();
		InputStream in = connection.getInputStream();

		return in;
	}




	/**
	 * Uses generic SAX, needs well formed XML.
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static org.w3c.dom.Document getXMLDoc(File xmlFile) throws IOException,
	SAXException, ParserConfigurationException {
		if ((xmlFile != null) && (xmlFile.exists())) {
			return getXMLDoc(new InputSource(new FileReader(xmlFile)));
		}
		return null;
	}

	private static org.w3c.dom.Document getXMLDoc(InputSource source)
			throws IOException, ParserConfigurationException,
			SAXException {
		//Tried to speed this up with transformers once. This is faster.
		org.w3c.dom.Document doc = null;
		if (source != null) {
			DocumentBuilder bldr = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			doc = bldr.parse(source);
		}
		return doc;
	}


	/**
	 * uses tidy to parse stream.
	 * Note: http://infohound.net/tidy/tidy.pl has an online version of tidy where we can test any config options.
	 * 
	 * Found in the hard way: any table/list inside a <pre> tag gets converted to <<table>> like structure, 
	 * ideally this should pass thru like text it preserves the spaces & line breaks, but IE && FF browsers display 
	 * it like the appropriate text from the tags. So tidy must be customized to fix this issue. 
	 * 
	 * @param htmlStream
	 * @param tagsLowerCase
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static org.w3c.dom.Document tidyParseHtml(String uriToHtmlFile, boolean tagsLowerCase) throws IOException, SAXException{
		//Logger.getLogger("").setLevel(Level.OFF);  
		InputStream is = uriToInputStream(uriToHtmlFile);
		if(is != null) {
			return tidyParseHtml(is, tagsLowerCase);
		}

		return null;
	}

	private static org.w3c.dom.Document tidyParseHtml(InputStream htmlStream, boolean tagsLowerCase){
		System.out.println("Using JTidy parser to parse Document");
		//System.out.println("InputStream: " + toString(htmlStream));

		Tidy tidy = new Tidy();

		//CONFIGURE tidy from a config files
		//Create any file like J:/config.txt.
		//add whatever parameters like "new-inline-tags: noindex" to it.
		//then use "tidy.setConfigurationFromFile("J:/config.txt");" to load the config

		// configure Tidy instance as required
		tidy.setUpperCaseTags(!tagsLowerCase);
		tidy.setUpperCaseAttrs(!tagsLowerCase);

		//tidy.setWraplen(0);
		//tidy.setWrapAttVals(false);

		tidy.setXHTML(true);

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


		System.out.println("JTidy parsing InputStream...");
		org.w3c.dom.Document doc = tidy.parseDOM(htmlStream, null);
		System.out.println("JTidy parsing InputStream... Done");

		return doc;
	}	


	//TODO: TIKA Parser is also available.

	public static org.jsoup.nodes.Document jsoupParseHtml(String url) throws IOException{
		org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(url).get();
		//convert it to org.w3c.dom.Document
		return doc;
	}

	private static org.w3c.dom.Document jerichoParseHtml(String url)  throws IOException, Exception{
		//net.htmlparser.jericho
		net.htmlparser.jericho.Config.LoggerProvider=net.htmlparser.jericho.LoggerProvider.DISABLED;
		net.htmlparser.jericho.BasicLogFormatter.OutputLevel = false;
		//Config.LoggerProvider=LoggerProvider.DISABLED;

		//net.htmlparser.jericho.BasicLogFormatter = 
		Source source=new Source(new URL(url));
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
		org.w3c.dom.Document doc = jerichoBuildDocument(elementList);
		return doc;
	}

	private static org.w3c.dom.Document jerichoBuildDocument(List<net.htmlparser.jericho.Element> jElements) throws ParserConfigurationException{
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

	private static void jerichoElementToNode(net.htmlparser.jericho.Element jElement, org.w3c.dom.Element element, org.w3c.dom.Document doc) {
		//first dump this element
		if(jElement != null) {
			//first set the attributes
			net.htmlparser.jericho.Attributes jAttributes = jElement.getAttributes();
			System.out.println("Jericho element name "+jElement.getName()+", attribute count: " + jAttributes.getCount());
			for(int jIndex = 0; jIndex < jAttributes.getCount(); jIndex++) {
				net.htmlparser.jericho.Attribute jAttribute = jAttributes.get(jIndex);
				element.setAttribute(jAttribute.getName(), jAttribute.getValue());
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

	private static org.htmlcleaner.TagNode htmlcleanerParseHtml(String url) throws MalformedURLException, IOException{
		org.htmlcleaner.CleanerProperties props = new org.htmlcleaner.CleanerProperties();

		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);

		// do parsing
		org.htmlcleaner.TagNode tagNode = new org.htmlcleaner.
				HtmlCleaner(props).clean(new URL(url));

		// serialize to xml file
		//new org.htmlcleaner.PrettyXmlSerializer(props).writeToFile(
		//    tagNode, "chinadaily.xml", "utf-8");
		return tagNode;
	}

	private static org.htmlparser.util.NodeList htmlparserParseHtml(String url) throws org.htmlparser.util.ParserException 
	{
		org.htmlparser.Parser parser = new org.htmlparser.Parser (url);
		org.htmlparser.util.NodeList list = parser.parse (null);
		// do something with your list of nodes.
		return list;
	}

	//uses sax, or can use alternatively xerces
	private static org.w3c.dom.Document tagsoupParseHtml(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns all tags matching the tagName.
	 * TODO: add an exclude flag, if true, returns all tags except the skipped tags
	 */
	public static List<org.w3c.dom.Node> filterDocument(org.w3c.dom.Document doc, String tagName) throws IOException {

		System.out.println("---------------------------------------------------------------");
		System.out.println("Document: " + toString(doc));
		List filteredNodeList = new ArrayList();
		doc.getDocumentElement().normalize();//what it does?

		//first search the top level list
		org.w3c.dom.NodeList nList2 = doc.getElementsByTagName(tagName);
		for (int indx= 0; indx < nList2.getLength(); indx++) {
			org.w3c.dom.Element element = (org.w3c.dom.Element) nList2.item(indx);//returns Node, or Element?
			System.out.println("Current element: " + element.toString() + ", text content: " + element.getTextContent());
			filteredNodeList.add(element);
			if(element.getAttribute("name").equals(tagName)){
				//System.out.println("EnterName: " + eElement.getNodeValue());
			}

			//then search inside each element, if there are children
		}

		return filteredNodeList;
	}

	//DESTRUCTIVE: deletes any matching element inside the document. Excludes masterNode
	public static void filterDocumentSkipTags(org.w3c.dom.Document document, List<org.w3c.dom.Element> skippedList) {
		org.w3c.dom.Element docElement = document.getDocumentElement();
		filterDocumentSkipTags(docElement, skippedList);
	}

	//DESTRUCTIVE: deletes any matching element inside the document. Excludes masterNode
	private static void filterDocumentSkipTags(org.w3c.dom.Node masterNode, List<org.w3c.dom.Element> skippedList) {
		org.w3c.dom.NodeList nList = masterNode.getChildNodes();
		for (int indx= nList.getLength()-1; indx > -1; indx--) {
			org.w3c.dom.Node element = (org.w3c.dom.Node) nList.item(indx);
			if(skippedList.contains(element)){
				System.out.println("Current element is a MATCH: " + element.toString() + ", text content: " + element.getTextContent());
				masterNode.removeChild(element);
			}
			else{
				if(element.hasChildNodes()) {
					System.out.println("Current element is a PARENT: " + element.toString() + ", text content: " + element.getTextContent());
					//recurse down
					filterDocumentSkipTags(element, skippedList);
				}
			}
		}
	}

	public static String toString(InputStream in) throws IOException {
		//InputStream in = /* your InputStream */;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while(read != null) {
			//System.out.println(read);
			sb.append(read);
			read =br.readLine();
		}

		return sb.toString();
	}

	public static InputSource toInputSource(String inputText) throws IOException {
		InputSource inputSource = new InputSource( new StringReader(inputText) );
		return inputSource;
	}

	/**
	 * returns a stream from String. assumes default charset.
	 * @param string
	 * @return
	 */
	public static InputStream toInputStream(String string) {
		InputStream iStream = new ByteArrayInputStream(string.getBytes(/*StandardCharsets.UTF_8*/));
		return iStream;
	}

	public static String toString(org.w3c.dom.Document doc) throws IOException {
		try
		{
			javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(doc);
			StringWriter writer = new StringWriter();
			javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
			javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			String output = writer.toString();
			System.out.println("toString() result: " + output);
			return output;
		}
		catch(javax.xml.transform.TransformerException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static final void prettyPrint(org.w3c.dom.Document xml) throws Exception {
		javax.xml.transform.Transformer tf = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new javax.xml.transform.dom.DOMSource(xml), new javax.xml.transform.stream.StreamResult(out));
		System.out.println(out.toString());
	}

	public static void writeToFile(org.w3c.dom.Document doc, String filePath) throws IOException {
		try
		{
			javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(doc);
			//StringWriter writer = new StringWriter();
			File outputFile = new File(filePath);
			javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(outputFile);
			javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			//return writer.toString();
		}
		catch(javax.xml.transform.TransformerException ex)
		{
			ex.printStackTrace();
			//return null;
		}
	}

	//this writes to a file using the apache libraries. may work.
	private static void writeToFile2(org.w3c.dom.Document doc, String filePath) throws IOException {
		try
		{
			//BufferedWriter br = new BufferedWriter
			org.apache.xml.serialize.OutputFormat format = new org.apache.xml.serialize.OutputFormat(doc); //document is an instance of org.w3c.dom.Document
			format.setLineWidth(65);
			format.setIndenting(true);
			format.setIndent(2);
			Writer out = new StringWriter();
			org.apache.xml.serialize.XMLSerializer serializer = new org.apache.xml.serialize.XMLSerializer(out, format);
			serializer.serialize(doc);

			String formattedXML = out.toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//return null;
		}
	}

	/**
	 * NOTE: NEED static initialization of the library. Must call in the imports like:
	 * import static org.apache.xml.security.Init.init;
	 * 
	 * @param xml
	 * @return
	 * @throws InvalidCanonicalizerException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws CanonicalizationException
	 * @throws IOException
	 */
	public static String toCanonicalXml(String xml) throws InvalidCanonicalizerException, ParserConfigurationException, SAXException, CanonicalizationException, IOException {
		//Call the static method "org.apache.xml.security.Init.init();" to do that before you use any functionality from that library.
		org.apache.xml.security.Init.init();
		Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
		byte canonXmlBytes[] = canon.canonicalize(xml.getBytes());
		String output = new String(canonXmlBytes);
		System.out.println("toCanonicalXml() result: " + output);
		return output;
	}

	public static String prettyFormat(String htmlInput) throws TransformerException, ParserConfigurationException, IOException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		InputSource src = new InputSource(new StringReader(htmlInput));
		Element document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
		/*
        Boolean keepDeclaration = input.startsWith("<?xml");
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();
        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);
        return writer.writeToString(document);*/
		return prettyFormatDocument(document);
	}

	public static String prettyFormatDocument(Element document) throws TransformerException, ParserConfigurationException, IOException, SAXException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//InputSource src = new InputSource(new StringReader(input));
		//Element document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
		//Boolean keepDeclaration = input.startsWith("<?xml");
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
		LSSerializer writer = impl.createLSSerializer();
		writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
		//writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);
		String output = writer.writeToString(document);
		System.out.println("prettyFormatDocument() output: " + output);
		return output;
	}


	/**
	 * This skips the xml declaration: <?xml version="1.0" encoding="UTF-16"?>
	 * @param element
	 * @return
	 */
	public static String prettyPrint(org.w3c.dom.Node domNode) throws TransformerException{


		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(domNode),
				new StreamResult(buffer));
		String str = buffer.toString();
		return str;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String filepath = "file:///C:\\temp\\ET 500 Companies List 2009-2.htm";
		String outputpath = "C:\\temp\\ET 500 Companies List 2009-33.htm";
		try {
			System.out.println("Sarting main()");
			//System.out.println("Opening using Generic");
			//org.w3c.dom.Document doc4 = getXMLDoc(new InputSource(filepath));
			//System.out.println("Child node count= " + doc4.getChildNodes().getLength());
			/*
			System.out.println("Opening using Neko");
			org.w3c.dom.Document doc1 = nekoParseHtml(filepath);
			System.out.println("Child node count= " + doc1.getChildNodes().getLength());
			List listOfScripts1 = filterDocument(doc1, "Script");
			System.out.println("Script node count= " + listOfScripts1.size());


			System.out.println("Opening using Tidy");
			org.w3c.dom.Document doc2 = tidyParseHtml(filepath, false);
			System.out.println("Child node count= " + doc2.getChildNodes().getLength());
			List listOfScripts2 = filterDocument(doc2, "script");
			System.out.println("Script node count= " + listOfScripts2.size());
			filterDocumentSkipTags(doc2, listOfScripts2);
			writeToFile(doc2, outputpath);

			System.out.println("Opening using Lobo");
			org.w3c.dom.Document doc3 = loboParseHtml(filepath);
			System.out.println("Child node count= " + doc3.getChildNodes().getLength());

			System.out.println("Opening using JSoup");
			org.jsoup.nodes.Document doc5 = jsoupParseHtml(filepath);
			System.out.println("Child node count= " + doc5.childNodeSize());

			System.out.println("Opening using jericho");
			//List doc6 = jerichoParseHtml(filepath);
			org.w3c.dom.Document doc6 = jerichoParseHtml(filepath);
			System.out.println("Child node count= " + doc6.getChildNodes().getLength());
			List listOfScripts6 = filterDocument(doc6, "script");
			System.out.println("Script node count= " + listOfScripts6.size());
			//filterDocumentSkipTags(doc6, listOfScripts6);
			writeToFile(doc6, outputpath);

			System.out.println("Opening using htmlcleaner");
			org.htmlcleaner.TagNode doc7 = htmlcleanerParseHtml(filepath);
			System.out.println("Child node count= " + doc7.getAllChildren().size());

			System.out.println("Opening using htmlparser");
			org.htmlparser.util.NodeList doc8 = htmlparserParseHtml(filepath);
			System.out.println("Child node count= " + doc8.size());
			 */

			String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html     PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"     \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">    <head>    </head>    <body data-twttr-rendered=\"true\">    </body></html>";
			toCanonicalXml(xmlStr);

			System.out.println("Returning from Main()");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}

}
