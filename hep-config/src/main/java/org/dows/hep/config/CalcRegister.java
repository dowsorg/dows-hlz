package org.dows.hep.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 计算器注册
 */
//@Configuration
public class CalcRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //创建对象，参数true代表使用默认过滤规则，例如@component注解，false代表使用自己定义的使用注解的过滤规则
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            //如果这个类添加了@CalcCode
            return metadataReader.getAnnotationMetadata().hasAnnotation("org.dows.hep.api.annotation.CalcCode");
        });
        //扫描目标对象所在的包
        String pkg = environment.getProperty("dows.calc.basePackage");
        scanner.scan(StrUtil.isBlank(pkg) ? "org.dows.hep.biz.calc" : pkg);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}

