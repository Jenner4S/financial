模块化开发

使用IntelliJ IDEA作为开发工具，Gradle作为构建工具，SpringBoot作为开发框架，使用的是Spring Data JPA操作MySQL数据库

管理端的编码阶段。主要使用RESTful规范设计了URL，实现了添加产品，查询产品的功能。对Spring boot中的统一错误处理通过查看源码文档等，对继承BasicErrorController或者使用ControllerAdvice等几种方式实现统一异常处理进行了详细的介绍。快速开发的前提一定要有质量保证，所以自动化测试也是必须的

```
ReflectionToStringBuilder.toString(this):common-lang包下简化toString()方法的打印
```

```java
@Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
```

import org.springframework.util.Assert;

Spring的Assert断言使用：

```java
/**
     * 产品数据校验
     * 1. 非空数据
     * 2. 收益率要0-30以内
     * 3. 投资步长需为整数
     * @param product
     */
    private void checkProduct(Product product) {
        Assert.notNull(product.getId(), "编号不可为空");
        // 其他非空校验自己写
        
        Assert.isTrue(BigDecimal.ZERO.compareTo(product.getRewardRate()) < 0 && BigDecimal.valueOf(30).compareTo(product.getRewardRate()) >= 0, "收益率范围错误");
        Assert.isTrue(BigDecimal.valueOf(product.getStepAmount().longValue()).compareTo(product.getStepAmount()) == 0, "投资步长需为整数");
    }
```

日志打印，Controller使用info级别，Service使用debug级别

统一异常处理（使用BasicErrorContolelr）：

```java
/**
 * 自定义错误处理controller
 */
public class MyErrorController extends BasicErrorController {
    public MyErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
                             List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);
    }
    /**
      {
         "timestamp": "2018-01-14 10:41:17",
         "status": 500,
         "error": "Internal Server Error",
         "exception": "java.lang.IllegalArgumentException",
         "message": "编号不可为空",
         "path": "/manager/products"
      }
     */
    @Override
    protected Map<String, Object> getErrorAttributes(HttpServletRequest request,
                                                     boolean includeStackTrace) {
        Map<String, Object> attrs = super.getErrorAttributes(request, includeStackTrace);
        attrs.remove("timestamp");
        attrs.remove("status");
        attrs.remove("error");
        attrs.remove("exception");
        attrs.remove("path");
        String errorCode = (String) attrs.get("message");
        ErrorEnum errorEnum = ErrorEnum.getByCode(errorCode);
        attrs.put("message",errorEnum.getMessage());
        attrs.put("code",errorEnum.getCode());
        attrs.put("canRetry",errorEnum.isCanRetry());
        return attrs;
    }
}
```

统一异常处理（使用ControllerAdivce）：

```java
/**
 * 统一错误处理
 */
@ControllerAdvice(basePackages = {"com.zhou.manager.controller"})
public class ErrorControllerAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity handleException(Exception e){
        Map<String, Object> attrs = new HashMap();
        String errorCode = e.getMessage();
        ErrorEnum errorEnum = ErrorEnum.getByCode(errorCode);
        attrs.put("message",errorEnum.getMessage());
        attrs.put("code",errorEnum.getCode());
        attrs.put("canRetry",errorEnum.isCanRetry());
        attrs.put("type","advice");
        Assert.isNull(attrs,"advice");
        return new ResponseEntity(attrs, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

使用RestTemplate测试：

```java
private static RestTemplate rest = new RestTemplate();
Product result = rest.getForObject(baseUrl + "/products/" + product.getId(), Product.class);
            Assert.isTrue(result.getId().equals(product.getId()),"查询失败");
```

@FixMethodOrder注解控制Test类中方法执行顺序

主要介绍了一款非常流行的文档管理工具Swagger在实际项目中的正确使用方式。通过官网对Swagger进行简单了解及快速试用。又针对实际项目中个性化的使用要求进行了优化，如何显示中文、更加详细的注释等。通过springboot的自动配置原理及Enable*的原理，把swagger封装成我们自己的即插即用的插件式模块

进入产品系统的另一个模块，销售端的编码阶段。销售端是用来把管理端的产品通过接口的形式对外提供服务，所以算是中间模块，对内需要与管理端交互，对外需要与套壳公司交互。这里就介绍了Swagger在实际项目开发过程中的使用方式了。对内部系统见的交互方式进行了对比介绍，最终选择了编写方便又高效的JSONRPC

如果参数要求传接口：

```java
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
@JsonDeserialize(as = ProductRpcReq.class)
public interface ParamInf {
     List<String> getIdList();
     BigDecimal getMinRewardRate();
     BigDecimal getMaxRewardRate();
     List<String> getStatusList();
}
```

主要介绍缓存框架Hazelcast。对比了当下非常流行的多款缓存框架包括Memcached、Redis等。对Hazelcast从安装、配置、管理等方面进行了介绍，对Spring缓存中的主要注解@Cacheable/@CachePut/@CacheEvict也进行了非常详细的介绍。选择ActiveMq作为消息系统进行缓存维护

介绍了RSA的原理及如何在实际项目中进行通过加签验签来达到防抵赖及安全的目的。接着就是对账业务的详细介绍，首先了解我们实际的资金流转过程，为什么要对账、怎么对账、对账的过程是怎样的、对账有问题怎么办。我们主要是基于JPA的方式进行讲解，springboot对JPA的自动配置源码，来完成了手动配置JPA多Repository

```java
/**
 * 验签aop
 */
@Component
@Aspect
public class SignAop {

    @Autowired
    private SignService signService;

    @Before(value = "execution(* com.zhou.seller.controller.*.*(..)) && args(authId, sign, text,..)")
    public void verify(String authId, String sign, SignText text){
        String publicKey = signService.getPublicKey(authId);
        Assert.isTrue(RSAUtil.verify(text.toText(), sign,publicKey),"验签失败");
    }
}
```

使用JPA进行多数据源，读写分离的配置

要介绍的是系统开发完成之后的安全问题，使用HTTPS及API网关框架TYK来保护我们的系统。首先详细介绍了HTTPS的原理。然后介绍TYK的安装和使用，把API配置、访问控制、节流限速等非常重要的功能进行了介绍。对其他一些常用的功能也进行了简单介绍