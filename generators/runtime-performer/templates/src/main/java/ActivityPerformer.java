package <%=props.performerPackage%>;

import <%=props.modelPackage%>.<%=props.component%>Activity;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.soa.activity.ActivityContext;
import com.soa.activity.ActivityPerformer;
import com.soa.log.Logger;
import com.soa.message.Message;

public class <%=props.component%>ActivityPerformer implements ActivityPerformer {

	private static Log log = Log.getLog(<%=props.component%>ActivityPerformer.class);

	private <%=props.component%>Activity activity;
	
	public static final String STATIC_CONTENT = "static";
	public static final String VARIABLE_CONTENT = "variable";
	
	public void perform(ActivityContext context) throws GException {
		Logger auditLogger = context.getAuditLogger();
		
		try {
			log.startTraceBlock("<%=props.component%>ActivityPerformer.perform()");
			
			auditLogger.warn("You have selected [" + activity.getMessageName() + "]");
			Message message = getMessage((Object) context.getVariable(activity.getMessageName()));
			
		} catch (GException ex) {
			try {
				auditLogger.error("<%=props.component%> failure. " + ex.getLocalizedMessage());
			} finally {
				// don't throw exception as we don't want to stop execution for
				// an insert content failure
				log.error(ex);
			}
		} finally {
			log.endTraceBlock();
		}

	}

	
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
