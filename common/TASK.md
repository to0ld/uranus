# task list
## performance
```
基线：
- public field access
```
```
kpi
- pojo <--> jsonobject
- pojo <--> resultset
- pojo <--> request body
```
### POJO

- [] VOperformance
	- 
	- [v] if(equals) return的性能
		由VOPerformance可知，如果由一组if的block构成(每个block内部返回)，执行很慢
	- [x] enum的性能
	- cglib
	- jdk reflect
	- reflectasm
	- asm

```
asm vs reflecstasm vs beanwrapper vs cglib vs jdk reflect
目前来看jdk reflect相对合适比基线慢40倍
```
## 完备性测试
- 针对可否为空的情况进行分支判断
