package com.github.jnoee.xo.fastdfs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

import com.github.jnoee.xo.fastdfs.FastFileClient;
import com.github.tobato.fastdfs.FdfsClientConfig;

@Configuration
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FastDFSAutoConfiguration {
  /**
   * 配置FastDFS客户端组件。
   * 
   * @return 返回FastDFS客户端组件。
   */
  @Bean
  FastFileClient fastFileClient() {
    return new FastFileClient();
  }
}
