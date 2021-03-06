package com.github.jnoee.xo.ienum.bridge;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.search.bridge.StringBridge;

import com.github.jnoee.xo.ienum.IEnum;
import com.github.jnoee.xo.utils.CollectionUtils;
import com.github.jnoee.xo.utils.StringUtils;

/**
 * IEnumList枚举类型字段对text属性进行全文索引的桥接器。
 */
public class IEnumListTextBridge implements StringBridge {
  @Override
  public String objectToString(Object object) {
    @SuppressWarnings("unchecked")
    List<IEnum> ienums = (List<IEnum>) object;
    if (CollectionUtils.isEmpty(ienums)) {
      return "";
    }
    List<String> enumTexts = new ArrayList<>();
    for (IEnum ienum : ienums) {
      enumTexts.add(ienum.getText());
    }
    return StringUtils.join(enumTexts, " ");
  }
}
