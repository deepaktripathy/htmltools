package com.deepaktripathy.htmltools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.deepaktripathy.parsers.JTidyParser;
import com.deepaktripathy.parsers.JerichoHtmlParser;

/**
 * Returns existing instance or a new instance. Saves memory footprint.
 * @author user
 *
 */
public class DomParserFactory {


	private enum parsers {TIDY, NEKO, HTMLCLEANER, JSOUP, LOBO, JERICHO, JOBO, JREX, MOZILLA};

	private static final String K_PARSER_TIDY = "TIDY";
	private static final String K_PARSER_JERICHO = "JERICHO";
	//add more final names as needed

	//static {
	private static final Map<String, InstanceHolder> factoryMap =
			Collections.unmodifiableMap(new HashMap<String, InstanceHolder>() {{
				put(K_PARSER_TIDY, new InstanceHolder(K_PARSER_TIDY, JTidyParser.class, null) );
				put(K_PARSER_JERICHO, new InstanceHolder(K_PARSER_JERICHO, JerichoHtmlParser.class, null) );
				//put more as needed
			}});

	//}

	//private constructor
	private DomParserFactory() {}

	/**
	 * Creates/returns instance of one of the registered parsers. Can return null otherwise.
	 * NOTE: An IDomParser must have a default constructor, else modify this method to accept a 
	 * common/well known instance method or constructor of the parser.
	 * 
	 * @param registeredName
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static AbstractDomParser createParser(String registeredName) throws InstantiationException, IllegalAccessException{
		//return (IDomParser) factoryMap.get(action).newInstance();
		InstanceHolder holder = factoryMap.get(registeredName);
		if(holder != null) {
			Object parserInstance = holder.instance;
			if(parserInstance == null) {
				parserInstance = holder.classObj.newInstance();
			}
			return (AbstractDomParser)parserInstance;
		}

		return null;
	}
}

//holds a parser's publich name, classname and instance.
//make the contents private?
class InstanceHolder{
	String name;
	Class classObj;
	Object instance;

	private InstanceHolder(){}
	public InstanceHolder(String name, Class classObj, Object instance) {
		this.name = name; this.classObj = classObj; this.instance = instance;
	}
}
