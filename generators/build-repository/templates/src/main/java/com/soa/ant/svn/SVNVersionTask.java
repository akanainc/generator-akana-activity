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
package com.soa.ant.svn;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNVersionTask extends Task {

	private String svnurl;
	private String svnuser;
	private String svnpasswd;
	
	private String property;
	
	private SVNClientManager manager;
	private SVNRepository repository;
	
	public void execute() throws BuildException {
		
		if (property != null) {
			DAVRepositoryFactory.setup();
			
			SVNRepository repo = null;
			try {
				repo = getRepository();
				if (repo != null) {
					long revision = repo.getLatestRevision();
					getProject().setProperty(property, String.valueOf(revision));
					
					log("Got SVN version [" + revision + "]");
				}
			}
			catch (SVNException e) {
				throw new BuildException(e);
			}
			finally {
				if (repo != null) {
					repo.closeSession();
				}
			}
		}
	}
	
	protected SVNRepository getRepository() throws SVNException {
		
		if (repository == null) {
			if (svnurl != null) {
				ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
				if ((svnuser != null) && (svnpasswd != null)) {
					manager = SVNClientManager.newInstance(options, svnuser, svnpasswd);
				}
				else {
					manager = SVNClientManager.newInstance(options);
				}
				
				repository = manager.createRepository(SVNURL.parseURIDecoded(svnurl), false);
			}
		}
		return repository;
	}

	public void setSvnurl(String svnurl) {
		this.svnurl = svnurl;
	}

	public void setSvnuser(String svnuser) {
		this.svnuser = svnuser;
	}

	public void setSvnpasswd(String svnpasswd) {
		this.svnpasswd = svnpasswd;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}
