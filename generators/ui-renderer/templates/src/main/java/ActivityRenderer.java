package <%= props.validatorPackage %>;

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


	public void processContents(HttpServletRequest request) throws GException {
		initializeRenderer(request);
		this.getActivityInfo().setActivityDefinition(buildActivityElement(buildActivity(request), context, builders));
	}

	private <%= props.component %>Activity buildActivity(HttpServletRequest request) throws GException {
		<%= props.component %>Activity activity = new <%= props.component %>Activity();
		//String messageName = request.getParameter("messageName");
		//activity.setMessageName(messageName);
		return activity;
	}

	public Element createDefaultDefinition() throws GException {
		<%= props.component %>Activity activity = new <%= props.component %>Activity();
		//activity.setMessageName("message");
		return buildActivityElement(activity, context, builders);
	}

	public String getContentLocation() {
		return "/<%= props.rendererPackage %>/activity_details.jsp";
	}

	public String getDescription(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.description");
	}

	public String getIcon(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.icon");
	}

	public QName getInternalName() {
		return new QName("urn:<%= props.namespace %>",
			"<%= props.component %>Activity");
	}

	public String getMenuHoverHelp(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.menuHoverHelp");
	}

	public String getName(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.name");
	}

	@Override
	public List<String> makeScriptKeysList(String s) throws GException {
		return null;
	}

	@Override
	public Object createDefaultActivity(ComplexProcess complexProcess, String s, boolean b) {
		return null;
	}
}
