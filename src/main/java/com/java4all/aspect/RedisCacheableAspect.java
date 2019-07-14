package com.java4all.aspect;

import com.java4all.annotation.ParameterCacheable;
import com.java4all.annotation.RedisCacheable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.http.HttpServletRequest;
import jdk.nashorn.internal.scripts.JO;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * description:
 * 切面，拦截缓存注解
 * @author wangzx
 * @version v1.0
 * @date 2018/11/22 9:14
 */
@Aspect
@Component
public class RedisCacheableAspect {

  private Logger logger = Logger.getLogger("");

  @Pointcut("@annotation(com.java4all.annotation.RedisCacheable)")
  public void pointCut(){}

  @Before("pointCut()")
  public void beforeExecute(JoinPoint joinPoint){
    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();
    Signature signature = joinPoint.getSignature();

    logger.info("======>【baseInfo】");
    logger.info("======>url；"+request.getRequestURL().toString());
    logger.info("======>ip: "+ request.getRemoteAddr());
    logger.info("======>class: " + signature.getDeclaringTypeName());
    logger.info("======>httpMethod: "+request.getMethod());
    logger.info("======>method: " + signature.getName());
    logger.info("======>args："+ Arrays.toString(joinPoint.getArgs()));
  }


  @Around("pointCut()")
  public Object aroundExecute(ProceedingJoinPoint joinPoint)throws Throwable{
    String cacheName = "";
    Object cacheValue = null;

    Signature signature = joinPoint.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;

    List args = Arrays.asList(joinPoint.getArgs());
    List<String> parameterNames = Arrays.asList(methodSignature.getParameterNames());

    Method method = methodSignature.getMethod();
    if(method.isAnnotationPresent(RedisCacheable.class)){
      RedisCacheable redisCacheable = method.getAnnotation(RedisCacheable.class);
      cacheName = redisCacheable.cacheName();

      for(int i = 0,length = args.size();i < length; i ++){
        int indexOf = parameterNames.indexOf(cacheName);
        cacheValue = args.get(indexOf);
      }

    }

    parseParameterAnnotation(joinPoint);

    logger.info("注解字段为："+cacheName+",该字段值为："+cacheValue.toString());
    logger.info("======>proceed前");
    Object proceed = joinPoint.proceed();
    logger.info("【======>proceed方法执行结果】"+proceed.toString());
    logger.info("======>proceed后");

    return "返回结果被修改："+proceed.toString();
  }

  @After("pointCut()")
  public void after(JoinPoint joinPoint){
    logger.info("======>@After");
  }

  @AfterReturning("pointCut()")
  public void afterReturing(JoinPoint joinPoint){
    logger.info("======>@AfterReturning");
  }


  /**
   * 获取参数注解的值
   * @param joinPoint
   * @return
   */
  public Object parseParameterAnnotation(JoinPoint joinPoint){
    Object[] args = joinPoint.getArgs();
    Signature signature = joinPoint.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;

    Method method = methodSignature.getMethod();
    Annotation[][] annotations = method.getParameterAnnotations();
    List<String> parameterNames = Arrays.asList(methodSignature.getParameterNames());

    for(int i = 0,length = annotations.length;i < length;i ++){
      Annotation[] annotation = annotations[i];
      for (int j = 0,length2 = annotation.length;j < length2;j ++){
        Annotation annotation1 = annotation[j];
        if(annotation1 instanceof ParameterCacheable){
          ParameterCacheable parameterCacheable = (ParameterCacheable) annotation1;
          String key = parameterCacheable.key();
          logger.info("======>@ParameterCacheable注解参数为："+key+",值为："+args[i]);
        }
      }
    }

    return null;
  }

  /**
   * 解析注解中的sple表达式
   * parse spel expression of annotation
   *
   * @param spel spel expression,the specified field's value of annotation
   * @param method the method with specified annotation
   * @param args method‘s args
   * @return
   */
  public Object parseSpelValue(String spel,Method method,Object[]args){
    LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
    String[] parameterNames = discoverer.getParameterNames(method);
    SpelExpressionParser spelParser = new SpelExpressionParser();
    StandardEvaluationContext context = new StandardEvaluationContext();

    for (int i = 0,length = parameterNames.length;i < length;i++){
      context.setVariable(parameterNames[i],args[i]);
    }
    Object value = spelParser.parseExpression(spel).getValue(context, Object.class);
    logger.info("spel解析后的值:"+value.toString());
    return value;
  }


  /**
   * method info
   * @param joinPoint
   * @param methodSignature
   */
  private void parametersInfo(JoinPoint joinPoint,MethodSignature methodSignature){
    //方法名称
    String methodName = methodSignature.getName();
    //获取参数名称
    String[] parameterNames = methodSignature.getParameterNames();
    //参数类型
    Class[] parameterTypes = methodSignature.getParameterTypes();
    //参数值
    Object[] args = joinPoint.getArgs();

    logger.info(methodName);
    logger.info(Arrays.asList(parameterNames).toString());
    logger.info(Arrays.asList(parameterTypes).toString());
    logger.info(Arrays.asList(args).toString());
  }
}
