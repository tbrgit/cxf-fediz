<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.apache.cxf.BusFactory"%>
<%@page import="org.apache.cxf.Bus"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<html>
<head>
<title>IDP SignIn Request Form</title>
</head>
<body>
	<h1>IDP SignIn Request Form</h1>
	<form:form method="POST" id="signinform" name="signinform" >
		<br />
		userid   : <input type="text" name="username" size="32" /><br />
		password : <input type="password" name="password" size="32" /><br />
		<input type="hidden" id="execution" name="execution" value="${flowExecutionKey}"/>
		<input type="submit" name="_eventId_authenticate" value="Authenticate as resident" /><br />
	</form:form>
</body>
</html>