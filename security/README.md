# security module
## feature list
- [x] rbac v2
	- account service/账号
		- account security strategy/账号安全策略
			- 30天过期
			- 安全密码
			- 二维码登录
			- 双因素登录(微信企业号动态发)
		- 用户要分类型，不同类型的用户，安全级别不同
		- 用户中心的功能不在其列
	- authentication service/鉴权
	- authoriy service/授权
	- audit service/审计
- [v] access token interceptor
	- csrf interceptor
- [x] checkin interceptor
	- [x|?] with sso(without session;just redist/nosql)
