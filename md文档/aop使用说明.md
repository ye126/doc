### 面向切面编程（AOP）简介



#### 实现步骤
##### 1.在类上面加上注解 Aspect  Component
```
@Aspect
@Component
public class MyAspect {

}
```

##### 2.写个空的方法确定要拦截的点
```
	
    @Pointcut("execution(* com.jfeat.am.modular.system.api.OauthEndpoint.login(..))")
    public void loginPointCut(){
    }
```

##### 3.写个增强方法
```
	//@Around注解用于环绕增强，可在方法执行前后增加更改的内容
	//joinPoint.proceed();执行方法
	//@Around注解中填带有拦截点的方法 即上面的loginPointCut（）

    @Around("loginPointCut()")
    public Object login(ProceedingJoinPoint joinPoint) throws Throwable {
		//TODO 此处可以在方法执行前进行操作
		
        //执行被拦截的方法
        Object responObject = joinPoint.proceed();
		
		//TODO 此处可以在方法执行后进行操作
		
		return responObject;
}
```
##### 另一种写法（不写空的方法 直接将要拦截的点加在Around注解之后 ）
即：
```
@Around("execution(* com.jfeat.am.modular.system.api.OauthEndpoint.login(..))")
```

#### 切点示例
##### 1.execution（常用 易用）
由于Spring切面粒度最小是达到方法级别，而execution表达式可以用于明确指定方法返回类型，类名，方法名和参数名等与方法相关的部件，并且在Spring中，大部分需要使用AOP的业务场景也只需要达到方法级别即可，因而execution表达式的使用是最为广泛的。如下是 execution表达式的语法：
> execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
> 这里问号表示当前项可以有也可以没有，其中各项的语义如下：

> modifiers-pattern：方法的可见性，如public，protected；
> ret-type-pattern：方法的返回值类型，如int，void等；
> declaring-type-pattern：方法所在类的全路径名，如com.spring.Aspect；
> name-pattern：方法名类型，如buisinessService()；
> param-pattern：方法的参数类型，如java.lang.String；
> throws-pattern：方法抛出的异常类型，如java.lang.Exception；
> 简单示例如下
```
 指定切入点表达式，拦截那些方法，即为那些类生成代理对象
 @Pointcut("execution(* com.aop.UserDao.save(..))")  ..代表所有参数
 @Pointcut("execution(* com.aop.UserDao.*())")  指定所有的方法
 @Pointcut("execution(* com.aop.UserDao.save())") 指定save方法
```

##### 2.within
within表达式的粒度为类，其参数为全路径的类名（可使用通配符），表示匹配当前表达式的所有类都将被当前方法环绕。
例如：
> within表达式路径和类名都可以使用通配符进行匹配，比如如下表达式将匹配com.spring.service包下的所有类，不包括子包中的类：
> within(com.spring.service.*)

> 如下表达式表示匹配com.spring.service包及子包下的所有类：
> within(com.spring.service..*)

##### 3.args（匹配参数个数类型用）

args表达式的作用是匹配指定参数类型和指定参数数量的方法，无论其类路径或者是方法名是什么。这里需要注意的是，args指定的参数必须是全路径的。如下是args表达式的语法：

> args(param-pattern)
> 如下示例表示匹配所有只有一个参数，并且参数类型是java.lang.String类型的方法：

> args(java.lang.String)
> 也可以使用通配符，但这里通配符只能使用..，而不能使用*。如下是使用通配符的实例，该切点表达式将匹配第一个参数为java.lang.String，最后一个参数为java.lang.Integer，并且中间可以有任意个数和类型参数的方法：

> args(java.lang.String,..,java.lang.Integer)

##### 4.annotation (用于注解)
例如 :
> @annotation(com.jfeat.am.module.log.annotation.BusinessLog)


#### 常见问题解决方法

##### 1.通过ProceedingJoinPoint的实体 执行方法
```
//用于执行被拦截的方法 然后在该语句前后增加内容来进行前置增强和后置增强
Object result = point.proceed();
```

##### 2.通用的获取request的方法
```
HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
```
##### 3.获取请求类型GET POST ……
```
		String method = request.getMethod();
```

##### 4.获得拦截方法名
```
		//例子为crud-log中的AOP方法
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();
```
附上一些网上找到的结果：
```
//左侧为方法 右侧为打印结果,getString是测试类的方法名，TestController是类名
		point.getSignature().toString():String com.fast.web.controller.TestController.getString()
		point.getSignature().toShortString():TestController.getString()
		point.getSignature().toLongString():
							public java.lang.String com.fast.web.controller.TestController.getString()
		point.getSignature().getName():getString
		point.getSignature().getModifiers():1
		point.getSignature().getDeclaringType():class com.fast.web.controller.TestController
		point.getSignature().getDeclaringTypeName():com.fast.web.controller.TestController
```
##### 5.切点为注解如何获得注解的名字和属性
```
		Signature sig = point.getSignature();
		MethodSignature msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();
        //获取操作名称
        BusinessLog annotation = currentMethod.getAnnotation(BusinessLog.class);
        String businessName = annotation.name();
        String businessValue = annotation.value();
```
##### 6.POST方法获取body参数
```
        //先执行原先登录方法
        Object responObject = joinPoint.proceed();
		//获得参数
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];
		//下面两句主要为了将Object转换成JSONObject
        String s = JSONObject.toJSONString(arg);
        JSONObject jsonObject = JSONObject.parseObject(s);
		//获得想要的参数
        String account = jsonObject.get("account").toString();
		//data里面的数据为对应接口返回的数据
        String responseString = JSON.toJSONString(responObject);
        JSONObject responseJSON = JSONObject.parseObject(responseString);
        JSONObject data = (JSONObject)responseJSON.get("data");
```


##### 7.待补充 关于AOP拦截异常的实现




