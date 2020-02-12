# SpringBoot 相关原理

## 一、主配置类

```java
@SpringBootApplication
public class HelloWorldApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }
}
```

`@SpringBootApplication`是一个很重要的注解：

**@SpringBootApplication:** Spring Boot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot 就应该运行这个类的main方法来启动SpringBoot应用;

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```

- `@SpringBootConfiguration`

@Configuration：Spring中用到的注解，配置类上来标注这个注解;

配置类 ----->  配置文件

SpringBootConfiguration是SpringBoot定义的配置类注解，

`Configuration`是Spring定义的配置类注解，SpringBootConfiguration内部其实还是用的Configuration，Configuration也是容器中的一个组件;@Component。

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {
}
```

- `@EnableAutoConfiguration`

这个也是SpringBoot非常重要的注解，开启自动配置功能。以前我们需要配置的东西，Spring Boot帮我们自动配置;`@EnableAutoConfiguration`告诉SpringBoot开启自动配置功能;这样自动配置才能生效;

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import({EnableAutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

    Class<?>[] exclude() default {};

    String[] excludeName() default {};
}
```

**两个重要注解：**

1. @AutoConfigurationPackage`

   自动扫描的包，这个是Spring的底层注解**@Import**，给容器中导入一个组件;导入的组件由 `Registrar.class`

   **将主配置类(@SpringBootApplication标注的类)的所在包及下面所有子包里面的所有组件扫描到Spring容器中**

2. `@Import({EnableAutoConfigurationImportSelector.class})`

   - 这个是Spring添加Bean的一种方式。将所有需要导入的组件以全类名的方式返回;这些组件就会被添加到容器中;

   - 会给容器中导入非常多的`自动配置类(xxxAutoConfiguration)``，就是给容器中导入这个场景需要的所有组件， 并配置好这些组件;

   - 有了自动配置类，免去了我们手动编写配置注入功能组件等的工作;

   ![Snipaste_2020-02-12_11-35-41](/Users/gatesma/IdeaProjects/SSM_SourceCode/SpringBoot_img/Snipaste_2020-02-12_11-35-41.png)

   - Spring Boot在启动的时候从类路径下的`META-INF/spring.factories`中获取`EnableAutoConfiguration`指定的值将这些值作为自动配置类加入到容器中，自动配置类就会生效，帮我们进行自动配置工作

   ![Snipaste_2020-02-12_11-38-30](/Users/gatesma/IdeaProjects/SSM_SourceCode/SpringBoot_img/Snipaste_2020-02-12_11-38-30.png)

   -  J2EE的整体整合解决方案和自动配置都在`spring-boot-autoconfigure-1.5.9.RELEASE.jar`

   例如：

   ![Snipaste_2020-02-12_11-42-14](/Users/gatesma/IdeaProjects/SSM_SourceCode/SpringBoot_img/Snipaste_2020-02-12_11-42-14.png)

   ```java
   /** @deprecated */
   @Deprecated
   public class EnableAutoConfigurationImportSelector extends AutoConfigurationImportSelector {
       public EnableAutoConfigurationImportSelector() {
       }
   
       protected boolean isEnabled(AnnotationMetadata metadata) {
           return this.getClass().equals(EnableAutoConfigurationImportSelector.class) ? (Boolean)this.getEnvironment().getProperty("spring.boot.enableautoconfiguration", Boolean.class, true) : true;
       }
   }
   ```











































