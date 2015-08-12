package com.deepaktripathy.htmltools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.deepaktripathy.parsers.JTidyParser;


public class TestJTidyParser extends CommonTestSetup{

	static IDomParser domParser = null;

	@BeforeClass
	public static void setupClass() {
		try {
			System.out.println("Setting up the harness...");
			domParser = DomParserFactory.createParser(IDomParser.PARSER_NAME_TIDY);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	public void testSimpleDocument() {
		try {
			InputStream is = Utils.uriToInputStream(referencenocasesimplepath);
			((JTidyParser)domParser).setInput(is);
			Document doc = (Document)domParser.parseDocument();
			System.out.println("------------------------Source file-------------------------");
			assertNotNull(doc);
			Utils.prettyPrint(doc);

			//get equivalent Document.
			System.out.println("------------------------Building Reference Document-------------------------");
			Document referenceDoc = buildSimpleReferenceDocument();
			System.out.println("------------------------Reference Document-------------------------");
			assertNotNull(referenceDoc);
			Utils.prettyPrint(referenceDoc);
			
			//DEBUG CODE - START
			//get both meta attributes to compare
			org.w3c.dom.Element srcHeadNode = (org.w3c.dom.Element)doc.getElementsByTagName("head").item(0);
			org.w3c.dom.Element refHeadNode = (org.w3c.dom.Element)referenceDoc.getElementsByTagName("head").item(0);
			org.w3c.dom.Element srcMetaNode = (org.w3c.dom.Element)srcHeadNode.getLastChild();
			org.w3c.dom.Element refMetaNode = (org.w3c.dom.Element)refHeadNode.getLastChild();			
			System.out.println("src node name=" + srcMetaNode.getNodeName() + ", ref node name=" + refMetaNode.getNodeName());
			System.out.println("src node content=" + Utils.prettyPrint(srcMetaNode) + " \nref node content=" + Utils.prettyPrint(refMetaNode));
			
			org.w3c.dom.Attr srcAttr = srcMetaNode.getAttributeNode("href");
			org.w3c.dom.Attr refAttr = refMetaNode.getAttributeNode("href");
			System.out.println("src attr: " + srcAttr.getNodeValue() + ", ref attr: " + refAttr.getNodeValue());
			//DEBUG CODE - END
			
			//System.out.println("Comparing via JUnit Assert Equals");
			//assertEquals(doc, referenceDoc);
			
			//above may not work, do it again
			System.out.println("Comparing via XMLSec Assert Equals");
			assertXMLEqual(referenceDoc, doc);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Ignore
	@Test
	public void testNoCaseDocument() {
		try {
			InputStream is = Utils.uriToInputStream(referencenocasepath);
			((JTidyParser)domParser).setInput(is);
			Document doc = domParser.parseDocument();
			System.out.println("------------------------Source file-------------------------");
			assertNotNull(doc);
			Utils.prettyPrint(doc);

			//get equivalent Document.
			System.out.println("------------------------Building Reference Document-------------------------");
			Document referenceDoc = buildReferenceDocument();
			System.out.println("------------------------Reference Document-------------------------");
			assertNotNull(referenceDoc);
			Utils.prettyPrint(referenceDoc);
			
			//System.out.println("Comparing via JUnit Assert Equals");
			//assertEquals(doc, referenceDoc);
			
			//above may not work, do it again
			System.out.println("Comparing via XMLSec Assert Equals");
			assertXMLEqual(referenceDoc, doc);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//test fragment

	//test text output

	//test upper/lower case


	@After
	public void teardown() {
		System.out.println("Tearing down the harness...");
	}

	public static void assertXMLEqual(String expected, String actual) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException, InvalidCanonicalizerException, TransformerException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		String canonicalExpected = Utils.prettyFormat(Utils.toCanonicalXml(expected));
		String canonicalActual = Utils.prettyFormat(Utils.toCanonicalXml(actual));
		assertEquals(canonicalExpected, canonicalActual);
	}

	public static void assertXMLEqual(Document expected, Document actual) throws ParserConfigurationException, IOException, SAXException, CanonicalizationException, InvalidCanonicalizerException, TransformerException, IllegalAccessException, ClassNotFoundException, InstantiationException {
		String canonicalExpected = Utils.prettyFormat(Utils.toCanonicalXml(Utils.toString(expected)));
		String canonicalActual = Utils.prettyFormat(Utils.toCanonicalXml(Utils.toString(actual)));

		System.out.println("\n\n\n");
		System.out.println("-------------------Source Doc: Canonical------------------");
		System.out.println(canonicalActual);
		
		System.out.println("-------------------Reference Doc: Canonical------------------");
		System.out.println(canonicalExpected);
		
		assertEquals(canonicalExpected, canonicalActual);
	}

}
