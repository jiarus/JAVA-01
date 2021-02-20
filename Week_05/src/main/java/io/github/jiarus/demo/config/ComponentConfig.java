package io.github.jiarus.demo.config;

import io.github.jiarus.demo.bean.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author zhangjiaru
 * @date: 2021/02/21
 */
@Component
public class ComponentConfig {
    
    @Bean(name = "studentD")
    public Student getStudent() {
        Student student = new Student();
        student.setName("abcd");
        student.setAge(21);
        student.setId("4");
        return student;
    }
}
