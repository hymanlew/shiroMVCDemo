<%--
  Created by IntelliJ IDEA.
  User: LUHUAIMIN
  Date: 2018/9/24
  Time: 10:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<h2>${error}</h2>
<form action="login" method="post">
    username:<input type="text" name="name" /><br/>
    password:<input type="password" name="password" /><br/>
    <input type="submit" value="submit" />
</form>
</body>
</html>
