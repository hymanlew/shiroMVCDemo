package hyman.aop;

/**
 * 定义校验分组，并指定校验的顺序，如果 FirstGroup 组失败，则 SecondGroup 组不会再校验。
 * FirstGroup.class, SecondGroup.class 都是自定义的接口。
 */
//@GroupSequence({FirstGroup.class, SecondGroup.class})
public interface Group {

    /**
     *
     高效、合理的使用hibernate-validator校验框架可以提高程序的可读性，以及减少不必要的代码逻辑。接下来会介绍一下常用一些使用方式。

    1，定义校验分组：
     public class ValidateGroup {
         public interface FirstGroup {
         }

         public interface SecondeGroup {
         }

         public interface ThirdGroup {
         }
     }

     2，定义校验 bean：
     @Validated
     @GroupSequence({ValidateGroup.FirstGroup.class, User.class})
     public class User {

         @NotNull(message = "channelType为NULL", groups = ValidateGroup.FirstGroup.class)
         private String channelType;
     }

    3，使用方法：
     @RequestMapping("/test/validator")
     public void test(@Validated User bean){
        ...
        这种使用方式有一个弊端，不能自定义返回异常。spring如果验证失败，则直接抛出异常，一般不可控。
     }

     @RequestMapping("/test/validator")
     public void test(@Validated User bean, BindingResult result){
         result.getAllErrors();
         ...
        如果方法中有BindingResult类型的参数，spring校验完成之后会将校验结果传给这个参数。通过BindingResult控制程序抛出自定义
        类型的异常或者返回不同结果。
     }
     　　
    4，全局校验拦截器，使用自定义 aop，或是自定义拦截器：
     @Aspect
     @Component
     public class ControllerValidatorAspect {
         @Around("execution(* com.*.controller..*.*(..)) && args(..,result)")
         public Object doAround(ProceedingJoinPoint pjp, result result) {
             result.getFieldErrors();
             ...
            这种方式可以减少controller层校验的代码，校验逻辑统一处理，更高效。
         }
     }

     5，借助 ValidatorUtils 工具类（代码实现在 hyman.util.ValidatorUtils）：
     @Bean
     public Validator validator() {
        return new LocalValidatorFactoryBean();
     }

     LocalValidatorFactoryBean 官方示意：
     LocalValidatorFactoryBean 是 Spring 应用程序上下文中 javax.validation（JSR-303）设置的中心类，它引导 javax.validation.ValidationFactory
     并通过 Spring Validator 接口以及 JSR-303 Validator 接口和 ValidatorFactory 实现参数验证。通过 Spring或JSR-303 Validator 接口与该 bean 的
     实例进行通信时，您将与底层 ValidatorFactory 的默认 Validator 进行通信。这非常方便，不必在工厂执行另一个调用。

     假设几乎总是会使用默认的 Validator。这也可以直接注入 Validator 类型的任何目标依赖项！从Spring 5.0开始，这个类需要 Bean Validation 1.1+，
     特别支持Hibernate Validator 5.x（参见setValidationMessageSource（org.springframework.context.MessageSource））。这个类也与 Bean Validation 2.0
     和 Hibernate Validator 6.0运行时兼容。

     有一个特别说明：如果你想调用BV 2.0的getClockProvider（）方法，通过#unwrap（ValidatorFactory.class）获取本机 ValidatorFactory，在那里调用返
     回的本机引用上的getClockProvider（）方法。Spring的MVC配置命名空间也使用此类，如果存在javax.validation API但未配置显式Validator。
     　　
     为什么要使用这个工具类呢？
     1、controller方法中不用加入BindingResult参数
     2、controller方法中需要校验的参数也不需要加入 @Valid 或者 @Validated 注解

     具体使用，在controller方法或者全局拦截校验器中调用 ValidatorUtils.validateResultProcess(需要校验的Bean) 直接获取校验的结果。
     请参考（https://github.com/hjzgg/usually_util/blob/master/spring-validate-demo/validator/ValidatorUtils.java）。


     6，自定义校验标准：定义一个 MessageRequestBean，继承 BaseMessageRequestBean，signature字段需要我们自定义校验逻辑。
     @Validated
     @GroupSequence({ValidateGroup.FirstGroup.class, ValidateGroup.SecondeGroup.class, MessageRequestBean.class})
     @LogicValidate(groups = ValidateGroup.SecondeGroup.class)
     public class MessageRequestBean extends BaseMessageRequestBean {

         //签名信息（除该字段外的其他字段按照字典序排序，将值顺序拼接在一起，进行md5+Base64签名算法）
         @NotBlank(message = "signature为BLANK", groups = ValidateGroup.FirstGroup.class)
         private String signature;
         ...
     }

     1、自定义一个带有 @Constraint注解的注解@LogicValidate，validatedBy 属性指向该注解对应的自定义校验器
     @Target({TYPE})
     @Retention(RUNTIME)
     //指定验证器
     @Constraint(validatedBy = LogicValidator.class)
     @Documented
     public @interface LogicValidate {

         String message() default "校验异常";
         //分组
         Class<?>[] groups() default {};
         Class<? extends Payload>[] payload() default {};
     }

     2、自定义校验器LogicValidator，泛型要关联上自定义的注解和需要校验bean的类型
     public class LogicValidator implements ConstraintValidator<LogicValidate, MessageRequestBean> {

         @Override
         public void initialize(LogicValidate logicValidate) {
         }

         @Override
         public boolean isValid(MessageRequestBean messageRequestBean, ConstraintValidatorContext context) {
             String toSignature = StringUtils.join( messageRequestBean.getBizType()
                 , messageRequestBean.getChannelType()
                 , messageRequestBean.getData()
                 , messageRequestBean.getToUser());

             String signature = new Base64().encodeAsString(DigestUtils.md5(toSignature));

             if (!messageRequestBean.getSignature().equals(signature)) {
                 context.disableDefaultConstraintViolation();
                 context.buildConstraintViolationWithTemplate("signature校验失败")
                 .addConstraintViolation();
                 return false;
             }
             return true;
         }
     }
     可以通过ConstraintValidatorContext禁用掉默认的校验配置，然后自定义校验配置，比如校验失败后返回的信息。


     7，springboot 自定义国际化信息配置：https://www.cnblogs.com/hujunzheng/p/9952563.html
     */
}