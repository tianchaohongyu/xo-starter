package com.github.jnoee.xo.jpa.usertype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.jnoee.xo.utils.BeanUtils;
import com.github.jnoee.xo.utils.CollectionUtils;
import com.github.jnoee.xo.utils.StringUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Json格式自定义列表类型。
 */
public class JsonListUserType extends AbstractListUserType {
  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session,
                            Object owner) throws SQLException {
    try {
      String value = getValue(rs, names[0], session);
      if (StringUtils.isBlank(value)) {
        value = "[]";
      }
      Field jsonField = getField(rs, names[0], owner);
      Class<?> beanClass = BeanUtils.getGenericFieldType(jsonField);
      JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, beanClass);
      return mapper.readValue(value, type);
    } catch (Exception e) {
      throw new SQLException("转换Json为目标对象时发生异常。", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index,
                          SharedSessionContractImplementor session) throws SQLException {
    try {
      List<Object> values = (ArrayList<Object>) value;
      if (CollectionUtils.isNotEmpty(values)) {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        setValue(st, mapper.writeValueAsString(values), index, session);
      } else {
        setValue(st, "[]", index, session);
      }
    } catch (Exception e) {
      throw new SQLException("转换目标对象为Json时发生异常。", e);
    }
  }
}
