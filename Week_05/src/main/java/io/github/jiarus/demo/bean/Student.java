package io.github.jiarus.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangjiaru
 * @date: 2021/02/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    
    private String id;
    
    private String name;
    
    private Integer age;
    
}
