/**
 *  SOA Software, Inc. Copyright (C) 2000-2008, All rights reserved
 *
 *  This  software is the confidential and proprietary information of SOA Software, Inc. 
 *  and is subject to copyright protection under laws of the United States of America and 
 *  other countries. The  use of this software should be in accordance with the license 
 *  agreement terms you entered into with SOA Software, Inc.
 * 
 * $Id$
 */
package com.soa.ant.j2ee;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This task takes a web.xml and produces a corresponding Spring file with
 * the SOA OSGi servlet, resource, and filter publishing APIs.
 *
 * @author SOA Software, Inc. Copyright (C) 2000-2008, All rights reserved
 */
public class WebXMLConverterTask extends Task {

	private String webxml;
	private String destfile;
	private String context;
	private String prefix;
	private String initOnStartup = "true";
	
	private List resources = new ArrayList();
	
	private static final String HEADER = "<?xml version='1.0' encoding='UTF-8'?>" +
							"<beans xmlns='http://www.springframework.org/schema/beans' " +
							       "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
							       "xmlns:osgi='http://www.springframework.org/schema/osgi' " +
							       "xmlns:util='http://www.springframework.org/schema/util' " +
							       "xsi:schemaLocation='http://www.springframework.org/schema/beans " +
							  							"http://www.springframework.org/schema/beans/spring-beans.xsd " +
							  							"http://www.springframework.org/schema/osgi " +
							  							"http://www.springframework.org/schema/osgi/spring-osgi.xsd " +
							  							"http://www.springframework.org/schema/util " +
							  							"http://www.springframework.org/schema/util/spring-util.xsd'>";
	
	private static final String FOOTER = "</beans>";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public void addConfiguredResource(Resource resource) {
		resources.add(resource);
	}
	
	public void setDestfile(String destfile) {
		this.destfile = destfile;
	}

	public void setInitOnStartup(String initOnStartup) {
		this.initOnStartup = initOnStartup;
	}

	public void setWebxml(String webxml) {
		this.webxml = webxml;
	}
	
	public void setContext(String path) {
		this.context = path;
	}
	
	/**
	 * Allow a preifx to be defined that will preceed all paths
	 * @param prefix The prefix String
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void execute() throws BuildException {
		
		if ((webxml != null) && (destfile != null)) {
			
			try {
				File file = getProject().resolveFile(webxml);
				
				DocumentBuilder builder = getDocumentBuilder();
				Document dom = builder.parse(file);
				generate(dom);
			}
			catch (BuildException e) {
				throw e;
			}
			catch (Exception e) {
				throw new BuildException(e);
			}
		}
	}
	
	protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver() {

			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (publicId.equals("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN")) {
					//log("RESOLVING ENTITY:"+publicId+"  using classloader: "+this.getClass().getClassLoader());
					InputStream input = this.getClass().getResourceAsStream("/javax/servlet/resources/web-app_2_3.dtd");
					// log("RESOLVED ENTITY: "+publicId);
					return new InputSource(input);
				}
				else if (publicId.equals("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN")) {
					//log("RESOLVING ENTITY:"+publicId+"  using classloader: "+this.getClass().getClassLoader());
					InputStream input = this.getClass().getResourceAsStream("/javax/servlet/resources/web-app_2_2.dtd");
					// log("RESOLVED ENTITY: "+publicId);
					return new InputSource(input);
				}
				return null;
			}
			
		});

		return builder;
	}
	
	protected Map getInitParams(Element parent) {
		Map result = null;
		
		NodeList paramList = parent.getElementsByTagName("init-param");
		if ((paramList != null) && (paramList.getLength() > 0)) {
			result = new HashMap();
			for (int i = 0 ; i < paramList.getLength() ; i++) {
				String name = getChildValue((Element)paramList.item(i), "param-name");
				String value = getChildValue((Element)paramList.item(i), "param-value");
				if ((name != null) && (value != null)) {
					result.put(name, value);
				}
			}
		}
		
		return result;
	}
	
	protected Map getContextParams(Document dom) {
		Map result = null;
		
		NodeList paramList = dom.getDocumentElement().getElementsByTagName("context-param");
		if ((paramList != null) && (paramList.getLength() > 0)) {
			result = new HashMap();
			for (int i = 0 ; i < paramList.getLength() ; i++) {
				String name = getChildValue((Element)paramList.item(i), "param-name");
				String value = getChildValue((Element)paramList.item(i), "param-value");
				if ((name != null) && (value != null)) {
					result.put(name, value);
				}
			}
		}
		
		return result;
	}
	
	protected String getChildValue(Element element, String name) {
		String result = null;
		
		NodeList nodes = element.getElementsByTagName(name);
		if ((nodes != null) && (nodes.getLength() > 0)) {
			Element el = (Element)nodes.item(0);
			if (el.getFirstChild() != null) {
				// log("Value for [" + name + "] = [" + el.getFirstChild().getNodeValue() + "]");
				result = el.getFirstChild().getNodeValue();
			}
		}
		
		return result;
	}
	
	protected void generate(Document source) throws IOException {
		
		Map filters = new HashMap();
		Map servlets = new HashMap();
		List listeners = new ArrayList();
		
		// Process the Filters
		//
		NodeList filterNodes = source.getDocumentElement().getElementsByTagName("filter");
		for (int i = 0 ; i < filterNodes.getLength() ; i++) {
			Element element = (Element)filterNodes.item(i);
			
			FilterInfo info = new FilterInfo();
			info.setName(getChildValue(element, "filter-name"));
			info.setFilter(getChildValue(element, "filter-class"));
			info.setInitParameters(getInitParams(element));
			
			filters.put(info.getName(), info);
		}
		
		// Process the Filter mappings
		//
		NodeList filterMappingNodes = source.getDocumentElement().getElementsByTagName("filter-mapping");
		for (int i = 0 ; i < filterMappingNodes.getLength() ; i++) {
			Element element = (Element)filterMappingNodes.item(i);
			
			String name = getChildValue(element, "filter-name");
			
			if (name != null) {
				FilterInfo info = (FilterInfo)filters.get(name);
				if (info != null) {
					info.addPathSpec(getChildValue(element, "url-pattern"));
				}
			}
		}
		
		// Process the Servlets
		//
		NodeList servletNodes = source.getDocumentElement().getElementsByTagName("servlet");
		for (int i = 0 ; i < servletNodes.getLength() ; i++) {
			Element element = (Element)servletNodes.item(i);
			
			ServletInfo info = new ServletInfo();
			info.setName(getChildValue(element, "servlet-name"));
			info.setServlet(getChildValue(element, "servlet-class"));
			info.setInitParameters(getInitParams(element));
			info.setInitOrder(getChildValue(element, "load-on-startup"));
			
			servlets.put(info.getName(), info);
		}
		
		// Process the Servlet mappings
		//
		NodeList servletMappingNodes = source.getDocumentElement().getElementsByTagName("servlet-mapping");
		for (int i = 0 ; i < servletMappingNodes.getLength() ; i++) {
			Element element = (Element)servletMappingNodes.item(i);
			
			String name = getChildValue(element, "servlet-name");
			
			if (name != null) {
				ServletInfo info = (ServletInfo)servlets.get(name);
				if (info != null) {
					info.addPathSpec(getChildValue(element, "url-pattern"));
				}
			}
		}
		
		// Process the listeners
		//
		NodeList listenerNodes = source.getDocumentElement().getElementsByTagName("listener");
		for (int i = 0 ; i < listenerNodes.getLength() ; i++) {
			Element element = (Element)listenerNodes.item(i);
			
			String className = getChildValue(element, "listener-class");
			
			if (className != null) {
				ListenerInfo info = new ListenerInfo();
				info.setClassName(className);
				
				listeners.add(info);
			}
		}
		
		writeOutput(servlets, filters, listeners, getContextParams(source));
	}
	
	protected void writeInitParams(Map params, StringBuffer buffer) {
		
		if ((params != null) && !params.isEmpty()) {
			buffer.append("\t").append("<property name='initParameters'>").append(LINE_SEPARATOR);
			buffer.append("\t\t").append("<map>").append(LINE_SEPARATOR);
			
			Iterator it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = (String)it.next();
				buffer.append("\t\t\t").append("<entry key='").append(key).append("' value='");
				buffer.append(params.get(key)).append("'/>").append(LINE_SEPARATOR);
			}
			
			buffer.append("\t\t").append("</map>").append(LINE_SEPARATOR);
			buffer.append("\t").append("</property>").append(LINE_SEPARATOR);
		}
	}
	
	protected void writeServlet(ServletInfo servlet, Writer writer) throws IOException {
		
		Iterator it = servlet.getPathSpecs().iterator();
		while (it.hasNext()) {
			writeServlet(servlet, writer, (String)it.next());
		}
	}
	
	protected void writeServlet(ServletInfo servlet, Writer writer, String pathSpec) throws IOException {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<bean class='com.soa.transport.http.deploy.impl.SimpleServletRegistration' ");
		buffer.append("id='").append(servlet.getName()).append("'>");
		buffer.append(LINE_SEPARATOR);
		buffer.append("\t").append("<property name='name' value='").append(servlet.getName()).append("'/>");
		buffer.append(LINE_SEPARATOR);
		
		if (prefix != null) {
			pathSpec = prefix + pathSpec;
		}
		
		buffer.append("\t").append("<property name='pathSpec' value='").append(pathSpec).append("'/>");
		buffer.append(LINE_SEPARATOR);
		
		// if (servlet.getInitOrder() != null) {
		//	buffer.append("\t").append("<property name='initOrder' value='").append(servlet.getInitOrder()).append("'/>");
		//	buffer.append(LINE_SEPARATOR);
		// }
		
		// Write the actual servlet bean
		//
		buffer.append("\t").append("<property name='servlet'>").append(LINE_SEPARATOR);
		buffer.append("\t\t").append("<bean class='").append(servlet.getServlet()).append("'/>").append(LINE_SEPARATOR);
		buffer.append("\t").append("</property>").append(LINE_SEPARATOR);
		
		buffer.append("\t").append("<property name='initOnStartup' value='").append(initOnStartup).append("'/>");
		buffer.append(LINE_SEPARATOR);
		
		writeInitParams(servlet.getInitParameters(), buffer);
		
		buffer.append("</bean>").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
		
		writer.write(buffer.toString());
	}
	
	protected void writeServletService(ServletInfo servlet, Writer writer) throws IOException {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<osgi:service interface='com.soa.transport.http.deploy.ServletRegistration' ");
		buffer.append("ref='").append(servlet.getName()).append("'>");
		buffer.append(LINE_SEPARATOR);
		
		buffer.append("\t").append("<osgi:service-properties>").append(LINE_SEPARATOR);
		buffer.append("\t\t").append("<entry key='name' value='com.soa.servlet.").append(servlet.getName()).append("'/>");
		buffer.append(LINE_SEPARATOR);
		buffer.append("\t").append("</osgi:service-properties>").append(LINE_SEPARATOR);
			
		buffer.append("</osgi:service>").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
		
		writer.write(buffer.toString());
	}
	
	protected void writeFilter(FilterInfo filter, Writer writer) throws IOException {
		
		log("Writing filter [" + filter.getName() + "]");
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<bean class='com.soa.transport.http.deploy.impl.SimpleFilterRegistration' ");
		buffer.append("id='").append(filter.getName()).append("'>").append(LINE_SEPARATOR);
		buffer.append("\t<property name='name' value='").append(filter.getName()).append("'/>").append(LINE_SEPARATOR);

		writeInitParams(filter.getInitParameters(), buffer);
		
		// Write the actual filter bean
		//
		buffer.append("\t<property name='filter'>").append(LINE_SEPARATOR);
		buffer.append("\t\t<bean class='").append(filter.getFilter()).append("'/>").append(LINE_SEPARATOR);
		buffer.append("\t</property>").append(LINE_SEPARATOR);		
		
		buffer.append("</bean>").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
		
		writer.write(buffer.toString());
	}
	
	protected void writeFilterService(FilterInfo filter, Writer writer) throws IOException {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<osgi:service interface='com.soa.transport.http.deploy.FilterRegistration' ");
		buffer.append("ref='").append(filter.getName()).append("'>").append(LINE_SEPARATOR);
		
		buffer.append("\t<osgi:service-properties>").append(LINE_SEPARATOR);
		buffer.append("\t\t<entry key='name' value='com.soa.filter.").append(filter.getName()).append("'/>").append(LINE_SEPARATOR);
		buffer.append("\t</osgi:service-properties>").append(LINE_SEPARATOR);
			
		buffer.append("</osgi:service>").append(LINE_SEPARATOR).append(LINE_SEPARATOR);
		
		writer.write(buffer.toString());
	}
	
	protected void writeExporter(Writer writer, Map servlets, Map filters, List listeners, Map contextParams) throws IOException {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("<osgi:service interface='com.soa.transport.http.deploy.WebAppRegistration'>");
		buffer.append(LINE_SEPARATOR);
		
		buffer.append("\t<bean class='com.soa.transport.http.deploy.impl.SimpleWebAppRegistration'>");
		buffer.append(LINE_SEPARATOR);
		
		// Write the context
		//
		if (context != null) {
			buffer.append("\t\t<property name='context' value='").append(context).append("'/>").append(LINE_SEPARATOR);
		}
		
		// Write the context params
		//
		if (contextParams!=null && !contextParams.isEmpty()) {
			buffer.append("\t\t<property name='contextInitParams'>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t\t<map>");
			buffer.append(LINE_SEPARATOR);
			
			Iterator it = contextParams.keySet().iterator();
			while (it.hasNext()) {
				String key = (String)it.next(); 
				buffer.append("\t\t\t\t<entry key='").append(key).append("' value='").append(contextParams.get(key)).append("'/>");
				buffer.append(LINE_SEPARATOR);
			}
			
			buffer.append("\t\t\t</map>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t</property>");
			buffer.append(LINE_SEPARATOR);
		}
		
		// Write the servlets
		//
		if (!servlets.isEmpty()) {
			buffer.append("\t\t<property name='servlets'>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t\t<list>");
			buffer.append(LINE_SEPARATOR);
			
			Iterator it = servlets.keySet().iterator();
			while (it.hasNext()) {
				buffer.append("\t\t\t\t<ref bean='").append(it.next()).append("'/>");
				buffer.append(LINE_SEPARATOR);
			}
			
			buffer.append("\t\t\t</list>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t</property>");
			buffer.append(LINE_SEPARATOR);
		}
		
		// Write the filters
		//
		if (!filters.isEmpty()) {
			buffer.append("\t\t<property name='filters'>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t\t<list>");
			buffer.append(LINE_SEPARATOR);
			
			Iterator filterIterator = filters.keySet().iterator();
			while (filterIterator.hasNext()) {
				buffer.append("\t\t\t\t<ref bean='").append(filterIterator.next()).append("'/>");
				buffer.append(LINE_SEPARATOR);
			}
			
			buffer.append("\t\t\t</list>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t</property>");
			buffer.append(LINE_SEPARATOR);	
		}
		
		// Write the listeners
		//
		if (!listeners.isEmpty()) {
			buffer.append("\t\t<property name='eventListeners'>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t\t<list>");
			buffer.append(LINE_SEPARATOR);
			
			Iterator listenerIterator = listeners.iterator();
			while (listenerIterator.hasNext()) {
				ListenerInfo info = (ListenerInfo)listenerIterator.next();
				buffer.append("\t\t\t\t<bean class='com.soa.transport.http.deploy.impl.SimpleListenerRegistration'>").append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t\t<property name='listener'>").append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t\t\t<bean class='").append(info.getClassName()).append("'/>").append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t\t</property>").append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t</bean>").append(LINE_SEPARATOR);
			}
			
			buffer.append("\t\t\t</list>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t</property>");
			buffer.append(LINE_SEPARATOR);
		}
		
		// Write the resources
		//
		if (!resources.isEmpty()) {
			buffer.append("\t\t<property name='resources'>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t\t<list>");
			buffer.append(LINE_SEPARATOR);
			
			Iterator resourceIterator = resources.iterator();
			while (resourceIterator.hasNext()) {
				Resource resource = (Resource)resourceIterator.next();
				
				buffer.append("\t\t\t\t<bean class='com.soa.transport.http.deploy.impl.SimpleResourceRegistration'>");
				buffer.append(LINE_SEPARATOR);
				
				if (resource.getPath() != null) {
					buffer.append("\t\t\t\t\t<property name='context' value='").append(resource.getPath()).append("'/>");
					buffer.append(LINE_SEPARATOR);
				}
				buffer.append("\t\t\t\t\t<property name='resources'>");
				buffer.append(LINE_SEPARATOR);
				
				buffer.append("\t\t\t\t\t\t<bean class='com.soa.transport.http.deploy.impl.SimpleHttpContext'>");
				buffer.append(LINE_SEPARATOR);
				
				String location = resource.getLocation() ;
				if (prefix != null) {
					location = prefix + location;
				}
				buffer.append("\t\t\t\t\t\t\t<property name='prefix' value='" + location + "'/>");
				buffer.append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t\t\t\t<property name='default' value='" + resource.getDefault() + "'/>");
				buffer.append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t\t\t</bean>");
				buffer.append(LINE_SEPARATOR);
				
				buffer.append("\t\t\t\t\t</property>");
				buffer.append(LINE_SEPARATOR);
				buffer.append("\t\t\t\t</bean>");
				buffer.append(LINE_SEPARATOR);
			}
			
			buffer.append("\t\t\t</list>");
			buffer.append(LINE_SEPARATOR);
			buffer.append("\t\t</property>");
			buffer.append(LINE_SEPARATOR);
		}

		buffer.append("\t</bean>");
		buffer.append(LINE_SEPARATOR);
		
		buffer.append("</osgi:service>");
		buffer.append(LINE_SEPARATOR);
		buffer.append(LINE_SEPARATOR);
		
		writer.write(buffer.toString());
	}
	
	protected void writeOutput(Map servlets, Map filters, List listeners, Map contextParams) throws IOException {
		
		File output = getProject().resolveFile(destfile);
		Writer writer = new FileWriter(output);
		
		writer.write(HEADER);
		writer.write(LINE_SEPARATOR);
		
		writeExporter(writer, servlets, filters, listeners, contextParams);
		
		Iterator servletIterator = servlets.keySet().iterator();
		while (servletIterator.hasNext()) {
			String key = (String)servletIterator.next();
			ServletInfo info = (ServletInfo)servlets.get(key);
			
			writeServlet(info, writer);
			// writeServletService(info, writer);
		}
		
		Iterator filterIterator = filters.keySet().iterator();
		while (filterIterator.hasNext()) {
			String key = (String)filterIterator.next();
			FilterInfo info = (FilterInfo)filters.get(key);
			
			writeFilter(info, writer);
			// writeFilterService(info, writer);
		}
		
		writer.write(FOOTER);
		writer.flush();
		writer.close();
	}
	
	private class FilterInfo {

		private String filter;
		private Map initParams;
		private String name;
		private List pathSpecs = new ArrayList();
		
		public void setFilter(String filter) {
			this.filter = filter;
		}

		public void setInitParameters(Map initParams) {
			this.initParams = initParams;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getFilter() {
			return filter;
		}

		public Map getInitParameters() {
			return initParams;
		}

		public String getName() {
			return name;
		}
		
		public void addPathSpec(String pathSpec) {
			this.pathSpecs.add(pathSpec);
		}
		
		public List getPathSpecs() {
			return this.pathSpecs;
		}

	}
	
	private class ServletInfo {

		private Map contextAttributes;
		private String initOrder;
		private Map initParameters;
		private String name;
		private List pathSpecs = new ArrayList();
		private String rootPath;
		private String servlet;
		
		public Map getContextAttributes() {
			return this.contextAttributes;
		}

		public String getInitOrder() {
			return this.initOrder;
		}

		public Map getInitParameters() {
			return this.initParameters;
		}

		public String getName() {
			return this.name;
		}

		public List getPathSpecs() {
			return this.pathSpecs;
		}

		public String getRootPath() {
			return this.rootPath;
		}

		public String getServlet() {
			return this.servlet;
		}

		public void setContextAttributes(Map contextAttributes) {
			this.contextAttributes = contextAttributes;
		}

		public void setInitOrder(String initOrder) {
			this.initOrder = initOrder;
		}

		public void setInitParameters(Map initParameters) {
			this.initParameters = initParameters;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void addPathSpec(String pathSpec) {
			this.pathSpecs.add(pathSpec);
		}

		public void setRootPath(String rootPath) {
			this.rootPath = rootPath;
		}

		public void setServlet(String servlet) {
			this.servlet = servlet;
		}

	}
	
	private class ListenerInfo {
		private String className;

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}
		
	}


}
