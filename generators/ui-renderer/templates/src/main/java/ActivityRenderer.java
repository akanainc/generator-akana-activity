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


/**
 * The ActivityRenderer that the activity UI framework will call when inserting and editing the <%= props.component %>Activity.
 * Informs the UI as to the representation of the activity.
 * 
 * @author 
 *
 */
public class <%= props.component %>ActivityRenderer extends BaseActivityRenderer {

	//Create an instance of the Log to use in this class
	public static final Log log = Log.getLog(<%= props.component %>ActivityRenderer.class);


	/**
	 * Build the XML activity definition based on the submitted form and sets it in the ProcessActivitiesInfo.ActivityDefinition  
	 * 
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#processContents(javax.servlet.http.HttpServletRequest)
	 * @param HttpServletRequest request
	 * @throws GException
	 */
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


	/**
	 * Creates the default definition for an activity.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#createDefaultDefinition()
	 * @return DOM Element holding the activity definition.
	 * @throws GException 
	 */
	@Override
	public Element createDefaultDefinition() throws GException {
		<%= props.component %>Activity activity = new <%= props.component %>Activity();
		activity.setMessageName("message");
		return buildActivityElement(activity, context, builders);
	}

	/**
	 * Gets the location of the web page content.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#getContentLocation()
	 * @return the content location
	 */
	@Override
	public String getContentLocation() {
		return "/<%= props.rendererPackage %>/activity_details.jsp";
	}

	/**
	 * Gets the description of the activity. The description may be locale specific so
	 * a locale is provided.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#getDescription(java.util.Locale)
	 * @param locale the locale
	 * @return the description
	 */
	@Override
	public String getDescription(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.description");
	}

	/**
	 * Gets the icon for the activity. The icon may be locale specific so a locale
	 * is provided.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#getIcon(java.util.Locale)
	 * @param locale the locale
	 * @return the icon
	 */
	@Override
	public String getIcon(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.icon");
	}

	/**
	 * Gets the unique internal name of the activity type as a QName.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#getInternalName()
	 * @return the internal name
	 */
	@Override
	public QName getInternalName() {
		return new QName("urn:<%= props.namespace %>",
			"<%= props.component %>Activity");
	}

	/**
	 * Gets the menu hover help. The help may be local specific so a locale is provided.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#getMenuHoverHelp(java.util.Locale)
	 * @param locale the locale
	 * @return the menu hover help
	 */
	@Override
	public String getMenuHoverHelp(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.menuHoverHelp");
	}

	/**
	 * Gets the name of the activity. The name may be locale specific so a locale
	 * is provided.
	 *
	 * @see <%= props.rendererPackage %>.BaseActivityRenderer#getName(java.util.Locale)
	 * @param locale the locale
	 * @return the name
	 */
	@Override
	public String getName(Locale locale) {
		return getLocalMessage(locale, "<%= props.namespace %>.name");
	}


}
