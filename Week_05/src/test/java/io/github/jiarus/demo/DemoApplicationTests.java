package io.github.jiarus.demo;

import io.github.jiarus.demo.bean.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootTest()
class DemoApplicationTests {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    //    @Qualifier("studentC")
    @Resource
    private Student studentC;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Resource
    private DataSource dataSource;
    
    @Test
    void testLoadBean() {
       /* //xml方式注入
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("spring.xml");
        //set
        Student studentA =(Student) applicationContext.getBean("studentA");
        //构造函数
        Student studentB =(Student) applicationContext.getBean("studentB");
        System.out.println(studentA);
        System.out.println(studentB);*/
        
        //通过容器获取实例
        Student studentC = applicationContext.getBean("studentC", Student.class);
        Student studentD = applicationContext.getBean("studentD", Student.class);
        //调用实例中的属性
        System.out.println(studentC);
        System.out.println(studentD);
        System.out.println(studentC);
    }
    
    @Test
    void testJDBCAdd() {
        String addSql = "INSERT INTO student(id, name,age) VALUES(?, ?, ?)";
        Student student = applicationContext.getBean("studentC", Student.class);
        jdbcTemplate.update(addSql, student.getId(), student.getName(), student.getAge());
    }
    
    @Test
    void testJDBCPre() {
        String addSql = "INSERT INTO student(id, name,age) VALUES(?, ?, ?)";
        Student student = applicationContext.getBean("studentC", Student.class);
        jdbcTemplate.execute(addSql, (PreparedStatementCallback<? extends Object>) preparedStatement -> {
            preparedStatement.setString(1, student.getId());
            preparedStatement.setString(2, student.getName());
            preparedStatement.setInt(3, student.getAge());
            return preparedStatement.executeUpdate() > 0;
        });
        
    }
    
    @Test
    void testJDBCHikari() throws SQLException {
        String addSql = "INSERT INTO student(id, name,age) VALUES(?, ?, ?)";
        Student student = applicationContext.getBean("studentC", Student.class);
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(addSql, new String[]{student.getId(), student.getName(), String.valueOf(student.getAge())});
        preparedStatement.executeUpdate();
        connection.commit();
        connection.close();
    }
    
}
