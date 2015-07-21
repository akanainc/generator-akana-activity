package <%=props.performerPackage%>;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.akana.activity.normalize.model.<%=props.component%>Activity;
import com.digev.fw.exception.ErrorCode;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.soa.activity.ActivityPerformer;
import com.soa.activity.ActivityPerformerFactory;


public class <%=props.component%>ActivityPerformerFactory implements ActivityPerformerFactory {

	private static Log log = Log.getLog(<%=props.component%>ActivityPerformerFactory.class);

	private static final QName NORMALIZE_ACTIVITY = new QName("urn:<%= props.namespace %>",
		"<%=props.component%>Activity");
	
	private static final QName[] SUPPORTED_ACTIVITIES = { NORMALIZE_ACTIVITY };

	private JAXBContext context;
	
	public void setContext(JAXBContext context) {
		this.context = context;
	}

	public QName[] getSupportedActivities() {
		return SUPPORTED_ACTIVITIES;
	}

	public ActivityPerformer createPerformer(Node definition) throws GException {
		log.trace("<%=props.component%>ActivityPerformerFactory.createPerformer(Node)");
		try {
			Unmarshaller unmarshaller = this.context.createUnmarshaller();
			<%=props.component%>ActivityPerformer perf = new <%=props.component%>ActivityPerformer();
			
			<%=props.component%>Activity activity = (<%=props.component%>Activity) unmarshaller.unmarshal(definition);
			
			perf.setActivityConfig(activity);
			
			return perf;			
		} catch (JAXBException e) {
			throw new GException(ErrorCode.ERR_UNMARSHALLING, e);
		}
	}

}
