
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


<#-- 宏变量使用例子 -->
<#macro list title items>
  <p>${title?cap_first}:
  <ul>
    <#list items as x>
      <li>${x?cap_first}
	</#list>
  </ul>
</#macro>
<@list items=["mouse", "elephant", "python"] title="Animals"/>


<#assign index = 0>
<#macro buildNode childs>
	<#assign index = index+1>

	<#-- list?size>0 等同于 size gt 0 -->
	<#if childs?? && childs?size gt 0>

		<#list childs as l>
			<div class="clear"> ${(l.itemName)!} </div>
		        <div class="clear"></div>

			<#-- 循环子节点 -->
			<#if l.items?? && l.items?size gt 0>
				<@buildNode childs=l.items />
			</#if>
		</#list>
	</#if>
	<div class="clear"> ${index} </div>
</#macro>
<@buildNode childs = list />


<script type="text/javascript">

	<#assign data=['a','b','c','d'] />
	<#if (data?size>0)>
		console.log('${data?size}');
	</#if>
	<#if data?size gt 0>
		console.log('${data?size}');
	</#if>

	// freemarker 的 if 标签也可以声明在 js 代码中：
	<#if is?? && is==1> function(){	<#if is?? && is==1></#if>	} </#if>

	<#list 0..size as i >
		// 当 size 为 n 时，则输出从 0 到 n。注意这种类型只适用于数字。
		console.log(${i});
	</#list>

	<#assign ars=["a","b","c","d"] />
		<#list ars as ar >
			console.log('${ar}');
		</#list>

	<#assign info={"name":"张三","address":"上海"} />
	console.log('${info.name}');

	<#assign text="{'name':'opal','age':'30+','addr':'上海上海'}" />
	// 解析 json
	<#assign data=text?eval />
	console.log('${data.name}');

</script>

</body>
</html>

<#--

1，============================================
创建一个宏变量，宏命名空间（宏变量存储模板片段(称为宏定义体)，可以被用作自定义指令）：
<#macro name param1 param2 ... paramN>
  ...
  <#nested loopvar1, loopvar2, ..., loopvarN>
  ...
  <#return>
  ...
</#macro>

name：是宏变量的名称，它不是表达式。和 顶层变量 的语法相同。

param1，param2 等等：是局部变量的名称，用于存储参数的值 (不是表达式)。局部变量和默认值(是表达式)是可选的。 默认值也可以是另外
		一个参数，比如 <#macro section title label=title>。

paramN：是最后一个参数，可能会有三个点(...)，这就意味着宏接受可变数量的参数，不匹配其它参数的参数可以作为最后一个参数 (也被称
		作笼统参数)。当宏被命名参数调用时，paramN 将会是包含宏的所有未声明的键/值对的哈希表。当宏被位置参数调用，paramN 将是额外参数的序列。(在宏内部，要查找参数，可以使用  myCatchAllParam?is_sequence。)

loopvar1，loopvar2 等等：是可选的，是循环变量的值，是由 nested 指令为嵌套内容创建的。这些都是表达式。

return 和 nested 指令是可选的，而且可以在 <#macro ...> 和 </#macro> 之间被用在任意位置和任意次数。

没有默认值的参数必须在有默认值参数 (paramName=defaultValue) 之前。并且给宏的局部变量赋值，要放在最后。


2，=======================================
freemarker 默认是从 session 中优先取值，所以有时会导致后台传过来的数据没有获取。故要将变量名 user 修改成其他的即可解决这个问题。
之后要注意，session与正常的后台传值到前台要注意变量名不要一致。


3，=======================================

4，=======================================
freemarker 中 seq_contains 和 contains：

contains，是字符串string 上的方法，作用于字符串上，主要用来查找字符串或者字符是否存在于左侧字符串里，返回值为true或者false。
seq_contains，用于查找序列中（集合或是数组中）是否包含指定元素。








-->