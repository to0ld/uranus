## todo

-[] define
	- [v] process define service
-[] engine
	- candidator(group;dept;role)
	- process instance service
	- task instance service(先调通了再说)
		- [?] comment真心不应该只有审批意见；现有的act自带comment表需要评估一下是否有这个识别能力：审批意见，业务交流意见，督办信息
		- [?] 在什么情况下，current task会跟history一起查询？从实现的角度出发，history显然从elasticsearch查询获得结果会更好
		- [?]
		- 扩展task config，确保流程定义尽量简单，变量都尽量外置
			-[?] 并行串行这个有待验证是否可以
		- [v] 需要提供一个api：根据指定的deploy_id将其他的更新了	
	- [L|x] agent
		平行代理(代理人和被代理人都能看见)
			- 在参与者的获取逻辑中切入读取代理人信息和代理模式的功能进行替代
		排他代理(代理人才能看到)
-[] eval
-[] monitor
	目前没有明确的必要性，主要是权限方面的处理