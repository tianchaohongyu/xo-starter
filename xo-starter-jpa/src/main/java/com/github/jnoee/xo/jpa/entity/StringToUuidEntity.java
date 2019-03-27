package com.github.jnoee.xo.jpa.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * 字符串转换成UuidEntity转换器工厂。
 */
public class StringToUuidEntity implements ConverterFactory<String, UuidEntity> {

  private static Logger logger = LoggerFactory.getLogger(StringToUuidEntity.class);

  @Override
  public <T extends UuidEntity> Converter<String, T> getConverter(Class<T> targetType) {
    return new StringToUuidEntityConverter<>(targetType);
  }

  /**
   * 字符串转换成UuidEntity转换器。
   *
   * @param <T> 转换类型
   */
  private class StringToUuidEntityConverter<T extends UuidEntity> implements Converter<String, T> {
    private final Class<T> toClass;

    /**
     * 构造方法。
     *
     * @param toClass 转换目标类
     */
    public StringToUuidEntityConverter(Class<T> toClass) {
      this.toClass = toClass;
    }

    /**
     * 将字符串值转换为UuidEntity对象。
     *
     * @param source 字符串值
     * @return 返回UuidEntity对象。
     */
    public T convert(String source) {
      try {
        T entity = toClass.newInstance();
        entity.setId(source);
        return entity;
      } catch (ReflectiveOperationException e) {
        logger.info(String.format("无法获得实例[%s]:%s", toClass, e.getMessage()), e);
        return null;
      }
    }
  }
}
