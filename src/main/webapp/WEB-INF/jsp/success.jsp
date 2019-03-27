
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="base" scope="request" value="${pageContext.request.contextPath}/"></c:set>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="${base}js/jquery-1.11.1.min.js"></script>
</head>
<body>
欢迎光临！！！

<%-- 只有在当前页面中有权限的需求时才会去查询权限。--%>
<shiro:hasRole name="admin">
    欢迎 admin 角色的用户！<shiro:principal />
</shiro:hasRole>

<shiro:hasPermission name="stud:create">
    当前用户可以创建学生！<shiro:principal />
</shiro:hasPermission>

<div>
    <a href="${base}auth/user?id=1&name=test">测试 ModelAndView（GET）</a>
    <a href="javascript:;" onclick="test()">测试 ModelAndView（POST）</a>
</div>
<div>
    <a href="${base}logout">退出（自定义）</a>
    <a href="${base}logout2">退出（系统自带）</a>
</div>
</body>

<script>
    /**
     * Guest 标签：用户没有身份验证时显示相应信息，即游客访问信息；
     * User 标签：用户已经身份验证/记住我登录后显示相应的信息；
     *
     * Authenticated 标签：用户已经身份验证通过，即 Subject.login 登录成功，不是记住我登录的。
     * notAuthenticated 标签：用户没有身份验证通过，即没有调用 Subject.login 进行登录，包括记住我自动登录的也属于未进行身份验证。
     * principal 标签 显示用户身份信息，默认调用 Subject.getPrincipal()获取，即 Primary Principal。
     *
     * hasRole 标签 如果当前 Subject 有角色将显示 body 体内容。
     * lacksRole 标签 如果当前 Subject 没有角色将显示 body 体内容。
     * hasAnyRoles 标签 如果当前 Subject 有任意一个角色（或的关系）将显示 body 体内容。
     *
     * hasPermission 标签 如果当前 Subject 有权限将显示 body 体内容。
     * lacksPermission 标签 如果当前 Subject 没有权限将显示 body 体内容。
     */

    // post，get 方式都可以正常请求，但是由于返回的是页面，而不是正常的 json 数据，所以会报 parsererror。
    function test() {
        console.log("== 1 ==");
        $.ajax({
            // type: 'post',
            type: 'get',
            async: false,
            data: {
                pid:1,
                pname:'test'
            },
            url: '${base}auth/model',
            dataType: 'json',
            error : function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(XMLHttpRequest.status);
                console.log(XMLHttpRequest.readyState);
                console.log(textStatus);
            },
            success: function (data) {
                console.log("== 2 =="+data);
            }
        });
    }
</script>
</html>
