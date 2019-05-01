package com.github.jnoee.xo.ienum;

import lombok.Data;

@Data
public class TestBean {
  private TestEnum num = TestEnum.ONE;

  private JavaEnum javaEnum = JavaEnum.ONE;

  public TestEnum getNum() {
    return num;
  }

  public void setNum(TestEnum num) {
    this.num = num;
  }
}
