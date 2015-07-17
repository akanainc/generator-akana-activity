package <%= props.rendererPackage %>;

import com.digev.fw.exception.ErrorCode;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.digev.fw.log.LogLevel;
import com.digev.fw.message.MessageSource;
import com.digev.fw.xml.util.DOMBuilderPool;
import com.soa.bpel.BpelErrorCode;
import com.soa.console.activity.ActivityRequestContents;
import com.soa.console.activity.ProcessActivitiesContext;
import com.soa.console.activity.ProcessActivitiesInfo;
import com.soa.console.activity.render.ActivityRenderer;
import com.soa.container.configuration.virtualservices.ActivitySummary;
import com.soa.portal.PortalConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import java.util.Locale;
import java.util.Map;

public abstract class BaseActivityRenderer implements ActivityRenderer {

	protected JAXBContext context;
	protected DOMBuilderPool builders;

	private String activityName;
	private String processKey;
	private ProcessActivitiesContext processContext;
	private ProcessActivitiesInfo activityInfo;
	private Locale locale;

	private static MessageSource messageSource;

	private static final Log log = Log.getLog(BaseActivityRenderer.class);

	public void setContext(JAXBContext context) {
		this.context = context;
	}

	public void setBuilders(DOMBuilderPool builders) {
		this.builders = builders;
	}

	public void initializeRenderer(HttpServletRequest request) throws GException {
		processKey = request.getParameter("processKey");
		ActivityRequestContents requestContents = new ActivityRequestContents(request.getSession(),
				request.getParameter(PortalConstants.TAB_ID));
		processContext = requestContents.getProcessFromSession(processKey);
		if (processContext == null) {
			// can't do anything, so don't return null
			throw new GException(BpelErrorCode.ERR_PROCESS_NOT_FOUND);
		}
		activityName = request.getParameter("activityName");

		Map<String, ProcessActivitiesInfo> activities = processContext.getActivities();
		activityInfo = activities.get(activityName);

		setLocale(request.getLocale());

	}


	protected Element buildActivityElement(Object activity, JAXBContext context, DOMBuilderPool builders)
			throws GException {
		DocumentBuilder builder = null;
		try {
			builder = builders.borrowObject();
			Marshaller marshaller = context.createMarshaller();
			Document doc = builder.newDocument();
			marshaller.marshal(activity, doc);
			return doc.getDocumentElement();
		} catch (JAXBException e) {
			log.error(e);
			throw new GException(ErrorCode.ERR_MARSHALLING, e);
		} finally {
			if (builder != null) {
				try {
					builders.returnObject(builder);
				} catch (GException e) {
					log.logThrowable(e, LogLevel.WARNING);
				}
			}
		}
	}

	public String getActivityType() {
		return ActivitySummary.INTERNAL_ACTIVITY_TYPE;
	}

	public void setMessageSource(MessageSource messageSource) {
		BaseActivityRenderer.messageSource = messageSource;
	}

	public String getLocalMessage(Locale locale, String key) {
		return getLocalMessage(locale, key, new String[] {});
	}

	public String getLocalMessage(Locale locale, String key, String args[]) {
		String msg = (messageSource == null ? "" : messageSource.getFormattedMessage(locale, key, args));
		return (msg == null ? "" : msg);
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public ProcessActivitiesContext getProcessContext() {
		return processContext;
	}

	public void setProcessContext(ProcessActivitiesContext processContext) {
		this.processContext = processContext;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
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
