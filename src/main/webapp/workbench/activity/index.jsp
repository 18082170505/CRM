<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<link href="../../jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="../../jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="../../jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="../../jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../../jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="../../jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<link rel="stylesheet" type="text/css" href="../../jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="../../jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="../../jquery/bs_pagination/en.js"></script>
<script type="text/javascript">
	/*
	   对于所有的关系型数据库，做前端的分页相关操作的基础组件
	   就是pageNo和pageSize
	   pageNo:页码
	   pageSize:每页展现的记录数

	   pageList方法：就是发出ajax请求到后台，从后台取得最新的市场活动信息列表数据，
	                通过响应回来的数据，局部刷新市场活动信息列表

	   在哪些情况下，需要调用pageList方法来局部刷新市场活动信息列表：
	   (1)：点击左侧菜单栏中的"市场活动"超链接时；
	   (2)：创建、修改、删除之后；
	   (3)：点击查询按钮的时候；
	   (4)：点击分页组件的时候。

	   以上为pageList方法制定了六个入口，也就是说，在以上6个操作执行完毕后，必须调用pageList方法
	* */
	function pageList(pageNo,pageSize){
		//每次局部刷新时将左侧的全选复选框的勾去掉
		$("#qx").prop("checked",false)

		//查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中
		$("#search-name").val($.trim($("#hidden-name").val()))
		$("#search-owner").val($.trim($("#hidden-owner").val()))
		$("#search-startTime").val($.trim($("#hidden-startDate").val()))
		$("#search-endTime").val($.trim($("#hidden-endDate").val()))

		$.ajax({
			url:"pageList.do",
			data: {
				"pageNo":pageNo,
				"pageSize":pageSize,
				"name":$.trim($("#search-name").val()),
				"owner":$.trim($("#search-owner").val()),
				"startDate":$.trim($("#search-startTime").val()),
				"endDate":$.trim($("#search-endTime").val())
			},
			type:"get",
			dataType:"json",
			success:function (data){
				/*
				data:
				我们需要的:市场活动信息列表
				[{市场活动1},{市场活动2},{市场活动3},...]
				一会儿分页插件需要的，查询出来的总记录条数
				{"total":100}

				{"total":100,"dataList":[{市场活动1},{市场活动2},{市场活动3},...]}
				 */
				var html = ""
				//每一个n就是一个市场活动对象
				$.each(data.dataList,function (i,n) {
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';
				})
				$("#ActivityBody").html(html)

				//计算总页数，注意如果结果是小数不能直接进行加法运算
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1
				//数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//当点击分页组件的时候触发，执行pageList方法，局部刷新市场活动信息列表
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})
	}

	$(function(){
		//为创建按钮绑定时间，打开添加操作的模态窗口
		$("#addbtn").click(function () {
			//加载bootstrap日历控件
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});

			//走后台，目的是取得用户信息列表，为下拉框赋值
			$.ajax({
				url:"getUserList.do",
				type:"get",
				dataType:"json",
				success:function (data){
					//data为用户信息列表组成的json数组
					//即[{"id":?,"name":?,loginAct:?,.....},{},{}]
					var html = "<option></option>"
					//使用$.each遍历json数组,其中i为索引，n为每个User对象
					//将每个用户对象的id作为value，name作为下拉框中的值
					$.each(data,function (i,n) {
						html+="<option value='"+n.id+"'>"+n.name+"</option>"
					})
					//将下拉框加入<select>标签
					$("#create-marketActivityOwner").html(html)

					//给下拉框设置默认选项，用户名为当前用户的名称
					//通过获取<select>标签并且给val赋指定option的value即可
					//注意在js中使用el表达式需要加" "，否则无法使用
					var id = "${user.id}"
					$("#create-marketActivityOwner").val(id)

					//赋值完之后打开模态窗口
					//操作模态窗口的方式
					//需要操作的模态窗口的jQuery对象，调用modal方法，为该方法传递参数 show:打开模态窗口  hide:关闭模态窗口
					$("#createActivityModal").modal("show")
				}
			})

		})

		//为保存按钮绑定事件，执行添加操作
		$("#savebtn").click(function () {
			$.ajax({
				url:"save.do",
				data:{
					"owner":$.trim($("#create-marketActivityOwner").val()),
					"name":$.trim($("#create-marketActivityName").val()),
					"startDate":$.trim($("#create-startTime").val()),
					"endDate":$.trim($("#create-endTime").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-describe").val())
				},
				type: "post",
				dataType: "json",
				success:function (data) {
					/*
					data:{"success":true/false}
					* */
					if (data.success){
						//添加成功后刷新市场活动信息列表
						//pageList(1,2)

						/*
						* $("#activityPage").bs_pagination('getOption','currentPage'):
						*   设置操作后停留在当前页
						* $("#activityPage").bs_pagination('getOption','rowsPerPage'):
						*   操作后维持已经设置好的每页展现记录数
						* 这两个参数不需要我们进行任何修改，直接使用即可
						* */
						//执行完插入操作后，应该回到第一页，维持每页展现的记录条数
						pageList(1,$("#activityPage").bs_pagination('getOption','rowsPerPage'))
						//清空模态窗口的内容，使用原生js中表单对应dom对象的reset()方法
						//jQuery-->dom：jQuery对象[0]
						//dom--->jQuery：$(dom对象)
						$("#ActivityAddForm")[0].reset()
						//关闭模态窗口
						$("#createActivityModal").modal("hide")
					}else {
						alert("添加市场活动失败！")
					}
				}
			})
		})

		//页面加载完毕后触发一个方法
		//默认展开市场活动列表的第一页，每页显示2条记录
		pageList(1,2);

		//为查询按钮绑定事件，执行pageList方法
		$("#searchbtn").click(function () {
			/*
			  点击查询按钮的时候，我们应该将搜索框中的信息保存起来，保存到隐藏域中
			  否则当你改变搜索框内容但没有点击查询按钮而是点击分页组件时，会展示搜索框改变后的搜索内容，是不合理的
			  注意:单击分页组件时也会用到搜索框，因为条件查询和分页查询都在ActivityController中的方法pageList中。
			 */
			$("#hidden-name").val($.trim($("#search-name").val()))
			$("#hidden-owner").val($.trim($("#search-owner").val()))
			$("#hidden-startDate").val($.trim($("#search-startTime").val()))
			$("#hidden-endDate").val($.trim($("#search-endTime").val()))

			pageList(1,2)
		})

		//为全选按钮绑定事件，实现全选全不选
		$("#qx").click(function () {
			//选中多个标签可以使用name属性，因为name可以重复
			//prop方法为指定属性设值，第一个参数为属性名，第二个参数为属性值
			$("input[name=xz]").prop("checked",this.checked)
		})

		//为子复选框绑定事件
		// $("input[name=xz]").click(function () {
		//
		// })
		//以上方法不可行，因为动态生成的标签不能以普通绑定事件的方式来操作。
		/*
		      动态生成的元素，要以on的方式来绑定事件
		      语法：
		        $(需要绑定元素的有效外层元素).on("绑定事件的方式", 需要绑定元素的jQuery对象, 回调函数)
		        其中有效外层元素是指非动态生成的外层标签
		 */
		$("#ActivityBody").on("click",$("input[name=xz]"),function (){
			//判断子复选框的数量和子复选框已选中的个数是否相等，相等则让全选框选中，否则不选中
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length)
		})
		
		//为删除按钮绑定事件
		$("#deletebtn").click(function () {
			//找到复选框所以挑勾的复选框的jQuery对象
			var $xz = $("input[name=xz]:checked")
			if ($xz.length == 0){
				alert("请选择需要删除的记录！")
			}else {
				if (confirm("确定删除所选中的记录吗？")){
					//拼接多个id,将拼接的字符串作为ajax请求的参数传递给后台，实现批量删除
					var param = ""
					//将$xz中的每个dom对象遍历出来，取其value值，就相当于取得了需要删除记录的id
					for (var i=0;i<$xz.length;i++){
						param += "id="+$($xz[i]).val()
						//如果不是最后一个元素则在后面加&符
						if (i<$xz.length-1){
							param += "&"
						}
					}
					//alert(param)
					$.ajax({
						url:"delete.do",
						data:param,
						type:"post",
						dataType:"json",
						success:function (data){
							/*
                            data :{success:true/false}
                             */
							if (data.success){
								//pageList(1,2)
								//删除后应该回到第一页，维持每页记录数
								pageList(1,$("#activityPage").bs_pagination('getOption','rowsPerPage'))
							}else {
								alert("删除市场活动信息失败！")
							}
						}
					})
				}
			}
		})

		//为修改按钮绑定事件，将需要的信息返回到修改操作的模态窗口中并打开模态窗口
		$("#editbtn").click(function () {
			//获取选中的复选框
			var $xz = $("input[name=xz]:checked")
			if ($xz.length == 0){
				alert("请选中需要修改的记录！")
			}else if ($xz.length > 1){
				alert("一次只能选择一条记录进行修改！")
			}else {
				var id = $xz.val()
				$.ajax({
					url:"getUserListAndActivity.do",
					data: {"id":id},
					type:"get",
					dataType:"json",
					success:function (data) {
						/*
						返回 用用户信息列表和一条市场活动记录
						data:{userList:[{用户1},{2},{3}], a:{市场活动对象}}
						 */
						//处理所有者下拉框
						var html = "<option></option>"
						$.each(data.userList,function (i,n) {
							html += "<option value='"+n.id+"'>"+n.name+"</option>"
						})
						//将option标签添加到select标签中
						$("#edit-marketActivityOwner").html(html)


						//将获取到的要修改的市场活动记录中的属性赋给文本框
						//注意不要忘了取id，id是每条记录的唯一标识，可以将id赋给隐藏域标签，不需要展现给用户
						$("#edit-id").val(data.a.id)
						$("#edit-marketActivityName").val(data.a.name)
						$("#edit-marketActivityOwner").val(data.a.owner)
						$("#edit-startTime").val(data.a.startDate)
						$("#edit-endTime").val(data.a.endDate)
						$("#edit-cost").val(data.a.cost)
						$("#edit-describe").val(data.a.description)

						//显示完数据后打开修改操作的模态窗口
						$("#editActivityModal").modal("show")
					}
				})
			}
		})

		//为模态窗口的修改按钮添加事件，修改市场活动信息，可以复制插入操作
		$("#updatebtn").click(function (){
			$.ajax({
				url:"update.do",
				data:{
					"id":$.trim($("#edit-id").val()),
					"owner":$.trim($("#edit-marketActivityOwner").val()),
					"name":$.trim($("#edit-marketActivityName").val()),
					"startDate":$.trim($("#edit-startTime").val()),
					"endDate":$.trim($("#edit-endTime").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-describe").val())
				},
				type: "post",
				dataType: "json",
				success:function (data) {
					/*
					data:{"success":true/false}
					* */
					if (data.success){
						//刷新模态窗口
						//pageList(1,2)
						//修改后停留在当前页，并且维持已经设置好的每页展现记录数
						pageList($("#activityPage").bs_pagination('getOption','currentPage'),
								 $("#activityPage").bs_pagination('getOption','rowsPerPage'))
						//关闭模态窗口
						$("#editActivityModal").modal("hide")
					}else {
						alert("修改市场活动失败！")
					}
				}
			})
		})
	});

</script>
</head>
<body>
    <!--隐藏域-->
	<input type="hidden" id="hidden-name">
	<input type="hidden" id="hidden-owner">
	<input type="hidden" id="hidden-startDate">
	<input type="hidden" id="hidden-endDate">

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="ActivityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startTime" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endTime" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="savebtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id">
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">

								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startTime">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endTime">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<!--
								   关于文本域textarea:
								    (1):一定是以标签对的形式出现，正常状态下标签对要紧紧挨着
								    (2):textarea属于表单元素范畴
								    所有的对于textarea的取值和赋值操作，应该统一使用val()方法(而不是html()方法)
								-->
								<textarea class="form-control" rows="3" id="edit-describe"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updatebtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startTime" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endTime">
				    </div>
				  </div>
				  
				  <button type="button" id="searchbtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addbtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editbtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deletebtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="ActivityBody">

					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage">

				</div>
			</div>
			
		</div>
		
	</div>
</body>
</html>