package io.github.jiarus.demo.config;

import io.github.jiarus.demo.bean.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangjiaru
 * @date: 2021/02/21
 */
@Configuration
public class BeanConfig {
    
    @Bean(name = "studentC")
    public Student getStudent() {
        Student student = new Student();
        student.setName("abc");
        student.setAge(20);
        student.setId("3");
        return student;
    }
}
