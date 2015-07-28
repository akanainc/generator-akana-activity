package <%= props.rendererPackage %>;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import com.soa.container.configuration.virtualservices.ComplexProcess;
import org.w3c.dom.Element;

import <%= props.modelPackage %>.*;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;

public class <%= props.component %>ActivityRenderer extends BaseActivityRenderer {


	public static final Log log = Log.getLog(<%= props.component %>ActivityRenderer.class);

	@Override
	public void processContents(HttpServletRequest request) throws GException {
		initializeRenderer(request);
		this.getActivityInfo().setActivityDefinition(buildActivityElement(buildActivity(request), context, builders));
	}

	private <%= props.component %>Activity buildActivity(HttpServletRequest request) throws GException {
		<%= props.component %>Activity activity = new <%= props.component %>Activity();
		String messageName = request.getParameter("messageName");
		
		//Validate values from UI. If validation fails throw a GException with an appropriate message 

		activity.setMessageName(messageName);
		return activity;
	}

	@Override
	public Element createDefaultDefinition() throws GException {
		<%= props.component %>Activity activity = new <%= props.component %>Activity();
		activity.setMessageName("message");
		return buildActivityElement(activity, context, builders);
	}

	@Override
	public String getContentLocation() {
		return "/<%= props.rendererPackage %>/activity_details.jsp";
	}

	@Override
	public String getDescription(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.description");
	}

	@Override
	public String getIcon(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.icon");
	}

	@Override
	public QName getInternalName() {
		return new QName("urn:<%= props.namespace %>",
			"<%= props.component %>Activity");
	}

	@Override
	public String getMenuHoverHelp(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.menuHoverHelp");
	}

	@Override
	public String getName(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.name");
	}


}
