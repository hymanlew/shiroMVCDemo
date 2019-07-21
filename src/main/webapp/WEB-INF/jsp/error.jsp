
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
  String id = request.getParameter("id");
  String name = request.getParameter("name");
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h3>${id} , ${name} , 这种方式只适用于接收 map，model 或 request 的 put，add，setAttribute 方式传过来的数据。</h3>

<h3><%=id%> , <%=name%> , java 方式的接收。</h3>

<h3>${param.id} , ${param.name} , EL 表达式方式的接收。</h3>

<h3>${requestScope.id} , ${requestScope.name} ， requestScope.name 等价于 request.getAttribute("name")，一般是从服务器段传过来的,可以传到客户端也可以传到服务器里面(即,方法1传到方法2,服务器内部的传输)</h3>

<h3>${error}</h3>
</body>
</html>
