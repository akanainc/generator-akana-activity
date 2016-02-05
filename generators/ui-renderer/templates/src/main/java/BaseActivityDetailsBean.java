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

/**
 * A base class containing a number of common methods for activities. 
 * At some point this should probably be moved into the core product or a common framework. 
 * 
 * @author 
 *
 */
@SuppressWarnings("serial")
public abstract class BaseActivityDetailsBean implements ActivityDetails, Serializable {
	private static final Log log = Log.getLog(BaseActivityDetailsBean.class);
	
	private String errorMessage;
	private String activityName;
	private String processKey;
	private ProcessActivitiesContext processContext;
	private ProcessActivitiesInfo activityInfo;
	//The locale of the HTTP request
	private Locale locale;
	//The MessageSource for all localized messages registered via Spring
	private static MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		BaseActivityDetailsBean.messageSource = messageSource;
	}
	//The JAXB context used to marshall/unmarshall the definition. This is set via spring.
	protected static JAXBContext context;

	public void setContext(JAXBContext context) {
		BaseActivityDetailsBean.context = context;
	}

	/**
	 * Initialize the bean by retrieving the ProcessContext, Activity Name, ActivityInfo and Local from the HTTP request
	 * 
	 * @param request 
	 * @throws GException
	 */
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

	/**
	 * Used to populate the bean from the request in order to re-render the page in the event of an error
	 * 
	 * @param request
	 */
	protected abstract void extractValuesFromParameters(HttpServletRequest request);
	/**
	 * Used to populate the bean from the activity configuration 
	 * 
	 * @param activityElem
	 * @throws GException
	 */
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

	/**
	 * Initialize the bean by retrieving the ProcessContext, Activity Name, ActivityInfo and Local from the HTTP request
	 * 
	 * @param request 
	 * @throws GException
	 */
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

	/**
	 * Returns the localized message for a key
	 * 
	 * @param locale
	 * @param key
	 * @return
	 */
	public String getLocalMessage(Locale locale, String key) {
		return getLocalMessage(locale, key, new String[] {});
	}

	/**
	 * Returns the localized message for a key
	 * 
	 * @param locale
	 * @param key
	 * @param args
	 * @return
	 */
	public String getLocalMessage(Locale locale, String key, String args[]) {
		String msg = (messageSource == null ? "" : messageSource.getFormattedMessage(locale, key, args));
		return (msg == null ? "" : msg);
	}
	
	/**
	 * Returns a list of variables that match a list of variable types
	 * 
	 * @param types
	 * @return a list of variables available in the process
	 */
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
