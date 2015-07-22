<%@ taglib uri="/strutslogic" prefix="logic"%>
<%@ taglib uri="/features" prefix="feat"%>
<%@ taglib uri="/strutsbean" prefix="bean"%>
<%@ taglib uri="/workbench" prefix="workbench"%>
<%@ page import="<@=props.rendererPackage@>.<@=props.component@>ActivityDetailsBean"%>
<%@ page import="com.soa.container.configuration.virtualservices.Variable"%>
<%@ page import="java.util.List"%>

<%
	<@=props.component@>ActivityDetailsBean bean = new <@=props.component@>ActivityDetailsBean();
	bean.setup(request);
	request.setAttribute("bean", bean);
%>

<style>
.dynTableBodySelector td {
	border-right: 0 none;
	border-color: #c5c5c5 #c5c5c5 -moz-use-text-color -moz-use-text-color;
    border-left: 0 none;
    border-style: solid solid none none;
    border-width: 1px 1px 0 0;
}

.dynTableBodySelector input {
    border: 0 none;
}

.dynTableBodySelector .staticVar {
    border: 1px solid #c5c5c5;
    line-height: 14px;
}

</style>
<script type="text/javascript" src="<%=request.getContextPath()%>/common/glb_dynamicTables.js"></script>
<table style="width:100%"><tr><td>
<fieldset><legend><workbench:message key="<@=props.namespace@>.detail.label"/></legend>
	<div>
		<logic:notEmpty name="bean" property="errorMessage"><span class="error"><%= com.soa.console.util.Encoder.escapeHTML(bean.getErrorMessage())%></span>
		</logic:notEmpty>
	</div>
	<!--p><%--
		<span class="fieldname_nowrap col1"><workbench:message key="<@=props.namespace@>.message"/>:</span>
		<input type="hidden" name="messageName" value="<bean:write name="bean" property="messageName"/>"/>
		<select name="messageNameSelect">
			<logic:iterate id="variable" name="bean" property="activityVariables">
				<option title="<bean:write name="variable" property="type" />"
					value="<bean:write name="variable" property="name"/>">
					<bean:write name="variable" property="displayValue" />
				</option>
			</logic:iterate>
		</select>
	</p--> --%>

</fieldset>
</td></tr></table>
<script>
$(document).ready(function() {
	/*
	$("select[name=messageNameSelect]").each(function () {
		selectDropDownValueWithDefault(this, $(this).parent().find("input[name=messageName]").val());
		$(this).parent().find("input[name=messageName]").val(this.value);
		//listener for the select
		$(this).change (function() {
			$(this).parent().find("input[name=messageName]").val(this.value);
		});
	});
	*/

});

</script>
