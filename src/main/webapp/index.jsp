<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="base" scope="request" value="${pageContext.request.contextPath}/"></c:set>
<html>
<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <title>Title</title>
</head>
<body>
Hello World!
<a href="${base}login.jsp">登录</a>
</body>
</html>
