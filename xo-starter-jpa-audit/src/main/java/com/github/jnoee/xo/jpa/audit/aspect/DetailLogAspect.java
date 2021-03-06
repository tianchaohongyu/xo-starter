package com.github.jnoee.xo.jpa.audit.aspect;

import java.lang.reflect.Field;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.github.jnoee.xo.jpa.audit.annotation.DetailLog;
import com.github.jnoee.xo.jpa.audit.entity.BizLog;
import com.github.jnoee.xo.jpa.dao.DaoUtils;
import com.github.jnoee.xo.utils.AspectUtils;
import com.github.jnoee.xo.utils.BeanUtils;
import com.github.jnoee.xo.utils.StringUtils;

/**
 * 详细业务日志切面。
 */
@Aspect
public class DetailLogAspect extends AbstractLogAspect {
  /**
   * 切面处理方法。
   * 
   * @param joinPoint 切入点
   * @param detailLog 日志注解
   * @throws Throwable 切面处理失败时抛出异常。
   * @return 返回方法本身返回的对象。
   */
  @Around("@annotation(detailLog)")
  public Object around(ProceedingJoinPoint joinPoint, DetailLog detailLog) throws Throwable {
    Map<String, Object> params = AspectUtils.getMethodParams(joinPoint);

    Object target = getEntity(params.get(detailLog.target()));

    BizLog bizLog = new BizLog();
    bizLog.setMessage(getMessage(detailLog.code(), detailLog.vars(), params));

    Object result = null;
    switch (detailLog.type()) {
      case ALL:
        result = processAll(bizLog, target, joinPoint);
        break;
      case ORIG:
        result = processOrig(bizLog, target, joinPoint);
        break;
      case NEW:
        result = processNew(bizLog, target, joinPoint);
        break;
      default:
        break;
    }

    saveBizLog(bizLog);
    return result;
  }

  /**
   * 记录原记录和新记录。
   * 
   * @param bizLog 业务日志
   * @param target 目标对象
   * @param joinPoint 切入点
   * @throws Throwable 切面处理失败时抛出异常。
   * @return 返回方法本身返回的对象。
   */
  private Object processAll(BizLog bizLog, Object target, ProceedingJoinPoint joinPoint)
      throws Throwable {
    bizLog.setEntityId(getEntityId(target));
    bizLog.setOrigData(target);
    Object result = joinPoint.proceed();
    target = getEntity(target);
    bizLog.setNewData(target);
    return result;
  }

  /**
   * 记录原记录。
   * 
   * @param bizLog 业务日志
   * @param target 目标对象
   * @param joinPoint 切入点
   * @throws Throwable 切面处理失败时抛出异常。
   * @return 返回方法本身返回的对象。
   */
  private Object processOrig(BizLog bizLog, Object target, ProceedingJoinPoint joinPoint)
      throws Throwable {
    bizLog.setEntityId(getEntityId(target));
    bizLog.setOrigData(target);
    return joinPoint.proceed();
  }

  /**
   * 记录新记录。
   * 
   * @param bizLog 业务日志
   * @param target 目标对象
   * @param joinPoint 切入点
   * @throws Throwable 切面处理失败时抛出异常。.
   * @return 返回方法本身返回的对象。
   */
  private Object processNew(BizLog bizLog, Object target, ProceedingJoinPoint joinPoint)
      throws Throwable {
    Object result = joinPoint.proceed();
    bizLog.setEntityId(getEntityId(target));
    bizLog.setNewData(target);
    return result;
  }

  /**
   * 获取业务实体。
   * 
   * @param target 日志目标对象
   * @return 如果目标对象是UuidEntity返回对应的业务实体，否则返回原目标对象。
   */
  private Object getEntity(Object target) {
    String entityId = getEntityId(target);
    if (StringUtils.isNotBlank(entityId)) {
      return DaoUtils.getEntity(target.getClass(), entityId);
    }
    return target;
  }

  /**
   * 获取业务实体ID。
   * 
   * @param target 日志目标对象
   * @return 返回业务实体ID。
   */
  private String getEntityId(Object target) {
    Field idField = BeanUtils.findField(target.getClass(), "id");
    if (idField != null) {
      Object idValue = BeanUtils.getField(target, idField);
      return idValue != null ? idValue.toString() : null;
    } else {
      return null;
    }
  }
}
