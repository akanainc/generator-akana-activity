package <%= props.rendererPackage %>;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;

import org.w3c.dom.Element;

import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.digev.fw.message.MessageSource;
import com.soa.bpel.BpelErrorCode;
import com.soa.console.activity.ActivityDetails;
import com.soa.console.activity.ActivityRequestContents;
import com.soa.console.activity.ProcessActivitiesContext;
import com.soa.console.activity.ProcessActivitiesInfo;
import com.soa.console.activity.VariableInfo;
import com.soa.portal.PortalConstants;

@SuppressWarnings("serial")
public abstract class BaseActivityDetailsBean implements ActivityDetails, Serializable {
	private static final Log log = Log.getLog(BaseActivityDetailsBean.class);
	
	private String errorMessage;
	private String activityName;
	private String processKey;
	private ProcessActivitiesContext processContext;
	private ProcessActivitiesInfo activityInfo;
	private Locale locale;
	
	private static MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		BaseActivityDetailsBean.messageSource = messageSource;
	}
	
	protected static JAXBContext context;

	public void setContext(JAXBContext context) {
		BaseActivityDetailsBean.context = context;
	}

	public void setup(HttpServletRequest request) {
		try {
			log.startTraceBlock(this.getClass().getName() + ".setup()");
			initializeDetails(request);
			if (errorOccured()) {
				extractValuesFromParameters(request);
			} else {
				if (getActivityInfo().getActivityDefinition() == null) {
					// new insert content
				} else {
					Element elem = (Element) getActivityInfo().getActivityDefinition();
					setupActivity(elem);
				}
			}

		} catch (Exception e) {
			log.error(e);
			setErrorMessage("Error retrieving Manage Transport Headers data. " + e.getLocalizedMessage());
		} finally {
			log.endTraceBlock();
		}
	}

	protected abstract void extractValuesFromParameters(HttpServletRequest request);

	protected abstract void setupActivity(Element activityElem) throws GException;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public boolean errorOccured() {
		return getErrorMessage() != null && !getErrorMessage().isEmpty();
	}


	public void initializeDetails(HttpServletRequest request) throws GException {
		processKey = request.getParameter("processKey");
		ActivityRequestContents requestContents = new ActivityRequestContents(request.getSession(),
				request.getParameter(PortalConstants.TAB_ID));
		processContext = requestContents.getProcessFromSession(processKey);
		if (processContext == null) {
			// can't do anything, so don't return null
			log.error("Process not found for key " + processKey + " and tab " + request.getParameter(PortalConstants.TAB_ID));
			throw new GException(BpelErrorCode.ERR_PROCESS_NOT_FOUND);
		}
		activityName = request.getParameter("activityName");
		Map<String, ProcessActivitiesInfo> activities = processContext.getActivities();
		activityInfo = activities.get(activityName);

		setErrorMessage((String) request.getAttribute("activityError"));

		locale = request.getLocale();
	}
	
	public List<VariableInfo> getAvailableVariables() {
		return processContext.getVariables();
	}
	
	public String getLocalMessage(Locale locale, String key) {
		return getLocalMessage(locale, key, new String[] {});
	}

	public String getLocalMessage(Locale locale, String key, String args[]) {
		String msg = (messageSource == null ? "" : messageSource.getFormattedMessage(locale, key, args));
		return (msg == null ? "" : msg);
	}
	
	protected List<VariableInfo> getSupportedVariables(List<String> types) {
		List<VariableInfo> variables = new ArrayList<VariableInfo>();
		for (VariableInfo varDetail : getAvailableVariables()) {
			if (types.contains(varDetail.getType()) ) {
				variables.add(varDetail);
			}
		}
		return variables;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public ProcessActivitiesInfo getActivityInfo() {
		return activityInfo;
	}
	
}
