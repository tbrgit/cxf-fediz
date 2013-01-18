<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.apache.cxf.BusFactory"%>
<%@page import="org.apache.cxf.Bus"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<title>IDP SignIn Request Form</title>
</head>
<body>
	<h1>IDP SignIn Request Form</h1>
	<form:form method="POST" name="signinform" >
		If you are resident, fill your credentials.
		<i>(It means you have an user account known of this current Identity Provider)</i>
		<br />
		userid   : <input type="text" name="username" size="32" /><br />
		password : <input type="password" name="password" size="32" /><br />
		<input type="hidden" id="execution" name="execution" value="${flowExecutionKey}"/>
		<input type="submit" name="_eventId_authenticate" value="Authenticate as resident" /><br />
	</form:form>
	<c:if test="${empty requestScope.whr}">
	    <%
	    Bus bus = BusFactory.getDefaultBus();
	    ApplicationContext applicationContext = (ApplicationContext) bus
	           .getExtension(ApplicationContext.class);
		List<String> idpPartnerUrls = (List<String>)applicationContext.getBean("idpPartnerUrls"); 
		%>
		<form:form method="POST" name="delegateform" >
			If you are partner, where are you from ?
			<i>(It means if you have no user account known of this current Identity Provider, you can select below the Identity Provider you belong to)</i>
			<br />
			<select name="whr">
				<% for (String idpPartnerUrl : idpPartnerUrls) { %>
				<option value="<%=idpPartnerUrl%>"><%=idpPartnerUrl%></option>
				<% } %>
			</select>
<!-- 			Warning : because the submit event of this form is targeted for external redirection, it is mandatory this form not contains the (commented) line below ! -->
<!-- 			<input type="hidden" id="execution" name="execution" value="${flowExecutionKey}"/> -->
			<br />
			<input type="submit" name="_eventId_authenticate-remote" value="Authenticate as partner" />
		</form:form>
	</c:if>
</body>
</html>