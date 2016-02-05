package <%=props.performerPackage%>;

import <%=props.modelPackage%>.<%=props.component%>Activity;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.soa.activity.ActivityContext;
import com.soa.activity.ActivityPerformer;
import com.soa.log.Logger;
import com.soa.message.Message;

/**
 * Perform the runtime activities associated with a particular activity definition.
 * 
 * @author 
 * @see http://docs.akana.com/ag/assets/apiDocs_pm80/com/soa/activity/ActivityPerformer.html
 *
 */
public class <%=props.component%>ActivityPerformer implements ActivityPerformer {

	//Create an instance of the Log to use in this class
	private static Log log = Log.getLog(<%=props.component%>ActivityPerformer.class);

	// The <%=props.component%>Activity config that is passed in by the factory
	private <%=props.component%>Activity activity;
	
	public static final String STATIC_CONTENT = "static";
	public static final String VARIABLE_CONTENT = "variable";
	
	/* 
	 * This method performs the actual runtime processing of the activity when invoked. The ActivityContext contains the
	 * variables defined in the process (such as the messages). 
	 * 
	 * @see http://docs.akana.com/ag/assets/apiDocs_pm72/com/soa/activity/ActivityPerformer.html#perform-com.soa.activity.ActivityContext-
	 * 
	 */
	public void perform(ActivityContext context) throws GException {
		/*
		 *  The auditLogger is used to write debug activities into the message logs and is controlled by the debug settings in the console 
		 *  for the virtual service.  There are effectively 2 loggers in this class - the audit logger (auditLogger) and the system logger (log).
		 */
		Logger auditLogger = context.getAuditLogger();
		
		try {
			/*
			 * Start the log trace block - this adds an indent to the system log and allows subsequent logs (if any)
			 * to be easily found in context. 
			 */
			log.startTraceBlock("<%=props.component%>ActivityPerformer.perform()");
			
			auditLogger.warn("You have selected [" + activity.getMessageName() + "]");
			Message message = getMessage((Object) context.getVariable(activity.getMessageName()));
			
		} catch (GException ex) {
			try {
				/*
				 * Attempt to log something informational about the failure. In this case it may be redundant because the 
				 * exception itself will also be logged
				 */
				auditLogger.error("<%=props.component%> failure. " + ex.getLocalizedMessage());
			} finally {
				// don't throw exception as we don't want to stop execution for
				// an insert content failure
				log.error(ex);
			}
		} finally {
			//Important! You must end the trace block in a finally clause
			log.endTraceBlock();
		}

	}

	/**
	 * Returns a Message object if the passed object is of the correct Message type. Otherwise returns null. 
	 * 
	 * @param msgObj
	 * @return Message instance
	 * @throws GException
	 * 
	 */	
	private Message getMessage(Object msgObj) throws GException {
		if (msgObj != null && msgObj instanceof Message) {
			Message msg = (Message) msgObj;
			return msg;
		} 
		return null;
	}

	
	public void setActivityConfig(<%=props.component%>Activity activity) {
		this.activity = activity;
	}

	
}
