package com.elijah.dubbograystarter;

import com.elijah.dubbograystarter.config.DubboGrayProperties;
import com.elijah.dubbograystarter.model.GrayRule;
import com.elijah.dubbograystarter.service.GrayRouteRulesCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DubboGrayStarterApplicationTests {

    @Test
    public void contextLoads() {
    }

}
