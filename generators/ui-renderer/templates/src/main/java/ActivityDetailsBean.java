package <%= props.rendererPackage %>;

import com.digev.fw.exception.ErrorCode;
import com.digev.fw.exception.GException;
import com.digev.fw.log.Log;
import com.soa.console.activity.VariableInfo;
import com.soa.container.configuration.virtualservices.Variable;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import <%= props.modelPackage %>.*;

@SuppressWarnings("serial")
public class <%= props.component %>ActivityDetailsBean extends BaseActivityDetailsBean implements Serializable {

	private static final Log log = Log.getLog(<%= props.component %>ActivityDetailsBean.class);

	public static final String[] SUPPORTED_ACTIVITY_VARIABLES = {Variable.MESSAGE};
	public static final String[] SUPPORTED_HEADER_VARIABLES = {Variable.STRING};

	
	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	private String messageName;

	@Override
	protected void extractValuesFromParameters(HttpServletRequest request) {
		//part of error handling routine. form gets re-populated here
		this.messageName = (String) request.getAttribute("messageName");
	}

	@Override
	protected void setupActivity(Element activityElem) throws GException {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			<%= props.component %>Activity activity = (<%= props.component %>Activity) unmarshaller.unmarshal(activityElem);
			this.messageName = activity.getMessageName();

		} catch (JAXBException je) {
			log.error(je);
			throw new GException(ErrorCode.ERR_UNMARSHALLING, new Object[] { je.getLocalizedMessage() });
		}
	}

	public List<VariableInfo> getActivityVariables() {
		return getSupportedVariables(Arrays.asList(SUPPORTED_ACTIVITY_VARIABLES));
	}

	public List<VariableInfo> getHeaderVariables() {
		return getSupportedVariables(Arrays.asList(SUPPORTED_HEADER_VARIABLES));
	}

}
