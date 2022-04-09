package com.skeqi.autotransmission;

import com.skeqi.autotransmission.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author CHUNHAO LIU
 */
@SpringBootApplication
@MapperScan("com.skeqi.autotransmission.mapper")
public class AutoTransmissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoTransmissionApplication.class, args);
    }

}
