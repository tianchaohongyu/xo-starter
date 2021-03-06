package com.github.jnoee.xo.jpa.converter;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.github.jnoee.xo.utils.CollectionUtils;
import com.github.jnoee.xo.utils.StringUtils;

@Converter(autoApply = true)
public class ListConverter implements AttributeConverter<List<String>, String> {
  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    return StringUtils.join(attribute, ",");
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    return CollectionUtils.toList(dbData.split(","));
  }
}

