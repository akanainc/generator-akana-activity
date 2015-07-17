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

public class Resource {
	
	private String location;
	private String defaultResource;
	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDefault() {
		return defaultResource;
	}
	
	public void setDefault(String defaultResource) {
		this.defaultResource = defaultResource;
	}

}
