package com.deepaktripathy.htmltools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This is a template class and any parser must implement the appropriate methods. 
 * So whether a parser supports any method can be checked from the return exception. 
 * 
 * TODO: a metadata describing the parser's behavior should be returned.
 * @author user
 *
 */
public class AbstractDomParser implements IDomParser{

	protected Object parseStatus;
	
	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Document parseDocument() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node parse(String fragment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getStatus() {
		return parseStatus;
	}

	@Override
	public String getTextContent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOutputCase(int outputCase) {
		throw new UnsupportedOperationException();
	}

}
