package <%= props.validatorPackage %>;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.soa.container.configuration.virtualservices.ProcessValidator;
import com.soa.process.ProcessVariable;
import com.soa.process.validation.ActivityValidator;
import com.soa.process.validation.ValidationContext;
import com.soa.process.validation.ValidationReportEntry;

import <%= props.modelPackage %>.*;

public class <%= props.component %>ActivityValidator implements ActivityValidator {

	private JAXBContext context;
	private boolean missingFromVar = false;

	private static final QName INTERNAL_NAME = new QName("urn:<%= props.namespace %>",
			"<%= props.component %>Activity");

	public void setContext(JAXBContext context) {
		this.context = context;
	}

	@Override
	public QName getActivityName() {
		return INTERNAL_NAME;
	}

	@Override
	public String getActivityType() {
		return getActivityName().getLocalPart();
	}

	/* InsertContentValidator will check for missing "To" variable, "From" variable 
	 * and mis-matched types.
	 * 
	 * Changes can be made on the variable detail page, which affect processing of
	 * Insert Content activity.  This validator will be used when
	 * saving a process, to make sure variables specified in Insert Content are valid
	 * at the point of saving.
	 * 
	 * If a "From" variable is found to be missing, an error message will be added.
	 * If a "To" variable is found to be missing, an error message will be added.
	 * If both "To" and "From" are missing variables, only 1 message will  be added.
	 *  
	 *  If variable type is not in the supported list of types,
	 *  for either "To" or "From", then add to error messages. This is because
	 *  the "To" and "From" support different types.  So they can both be in error.
	 */
	@Override
	public void validate(ValidationContext validationContext) {
		Element activityElem = validationContext.getActivityDefinition();
		
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			<%= props.component %>Activity activity = (<%= props.component %>Activity) unmarshaller.unmarshal(activityElem);
			validateFromVariable(validationContext, activity);
			validateToVariable(validationContext, activity);
		} catch (JAXBException ex) {
			// can't unmarshall, so can't validate
		}

	}

	private void validateFromVariable(ValidationContext validationContext, <%= props.component %>Activity activity) {

	}

	private void validateToVariable(ValidationContext validationContext, <%= props.component %>Activity activity) {

	}

	private void addMismatchVarToError(ValidationContext validationContext, ProcessVariable var) {
		validationContext.getReport().getEntries().add(
			new ValidationReportEntry(ValidationReportEntry.Level.WARNING,
					ProcessValidator.ACTIVITY_INVALID_TYPE, 
					new Object[] { validationContext.getActivityName(), var.getName(), var.getType() }));
	}

	private void addMissingVarToErrors(ValidationContext validationContext) {
		validationContext.getReport().getEntries().add(
			new ValidationReportEntry(ValidationReportEntry.Level.WARNING,
					ProcessValidator.ACTIVITY_MISSING_VARS, new Object[] { validationContext.getActivityName() }));
	}

}
