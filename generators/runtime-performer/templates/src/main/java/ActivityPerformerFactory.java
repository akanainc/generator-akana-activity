package <%=props.performerPackage%>;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import <%=props.modelPackage%>.<%=props.component%>Activity;
import com.digev.fw.exception.ErrorCode;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.soa.activity.ActivityPerformer;
import com.soa.activity.ActivityPerformerFactory;

/**
 * This factory is invoked by the Network Director when configuring the virtual service. 
 * It must return an instance of the ActivityPerformer for this process and service operation.
 * 
 * @author 
 * @see http://docs.akana.com/ag/assets/apiDocs_pm80/com/soa/activity/ActivityPerformerFactory.html
 *
 */
public class <%=props.component%>ActivityPerformerFactory implements ActivityPerformerFactory {
	
	//Create an instance of the Log to use in this class
	private static Log log = Log.getLog(<%=props.component%>ActivityPerformerFactory.class);
	
	/**
	 * The unique QName of this activity. This QName must match the activity definition in the model and is used
	 * by the product to find the relevant ActivityPerformerFactory
	 */
	private static final QName ACTIVITY_QNAME = new QName("urn:<%= props.namespace %>",
		"<%=props.component%>Activity");
	
	/**
	 * The list of activities supported by this ActivityPerformerFactory
	 */
	private static final QName[] SUPPORTED_ACTIVITIES = { ACTIVITY_QNAME };

	private JAXBContext context;
	/**
	 * The JAXB context used to unmarshall the definition. This is set via spring. 
	 * @param context
	 */
	public void setContext(JAXBContext context) {
		this.context = context;
	}
	/* (non-Javadoc)
	 * @see http://docs.akana.com/ag/assets/apiDocs_pm80/com/soa/activity/ActivityPerformerFactory.html#getSupportedActivities--
	 */
	public QName[] getSupportedActivities() {
		return SUPPORTED_ACTIVITIES;
	}
	/* 
	 * Unmarshall the activity definition and return an appropriate instance of the ActivityPerformer. This method is invoked only once 
	 * for a particular service operation.
	 * 
	 * @see http://docs.akana.com/ag/assets/apiDocs_pm80/com/soa/activity/ActivityPerformerFactory.html#createPerformer-org.w3c.dom.Node-
	 */
	public ActivityPerformer createPerformer(Node definition) throws GException {
		log.trace("<%=props.component%>ActivityPerformerFactory.createPerformer(Node)");
		try {
			//Create the JAXB unmarshaller
			Unmarshaller unmarshaller = this.context.createUnmarshaller();
			<%=props.component%>ActivityPerformer perf = new <%=props.component%>ActivityPerformer();
			
			<%=props.component%>Activity activity = (<%=props.component%>Activity) unmarshaller.unmarshal(definition);
			//Set the activity config on the ActivityPerformer
			perf.setActivityConfig(activity);
			
			return perf;			
		} catch (JAXBException e) {
			throw new GException(ErrorCode.ERR_UNMARSHALLING, e);
		}
	}

}
