package com.github.jnoee.xo.ienum.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.github.jnoee.xo.exception.SysException;
import com.github.jnoee.xo.ienum.IEnum;
import com.github.jnoee.xo.ienum.utils.IEnumUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * IEnum枚举反序列化组件。
 */
@Slf4j
public class IEnumContextualDeserializer extends JsonDeserializer implements ContextualDeserializer {

  @Override
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    return null;  //此方法不会被调用,可忽略
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
    Class<?> clazz = ctxt.getContextualType().getRawClass();
    return new JsonDeserializer<Object>() {
      @Override
      @SuppressWarnings("unchecked")
      public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (IEnum.class.isAssignableFrom(clazz)) {
          IEnum iEnum = IEnumUtils.getIEnumByValue((Class<? extends IEnum>) clazz, p.getText());
          return iEnum != null ? iEnum : IEnumUtils.getIEnumByText((Class<? extends IEnum>) clazz, p.getText());
        }
        if (Enum.class.isAssignableFrom(clazz)) {
          try {
            return Enum.valueOf((Class<? extends Enum>) clazz, p.getText());
          } catch (IllegalArgumentException e) {
            return ((Class<? extends Enum>) clazz).getEnumConstants()[p.getValueAsInt()];
          }
        }
        throw new SysException(String.format("无法将枚举值[%s]转换为[%s]", p.getText(), clazz));
      }
    };
  }
}