package hyman.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * ViewResolver 的主要职责是根据Controller所返回的ModelAndView中的逻辑视图名，为DispatcherServlet返回一个可用的View实例。
 * SpringMVC 中用于把View对象呈现给客户端的是View对象本身，而ViewResolver只是把逻辑视图名称解析为对象的View对象。
 *
 * ViewResolver实现类目录：
 * 1，AbstractCachingViewResolver：
 * 这是一个抽象类，这种视图解析器会把它曾经解析过的视图保存起来，然后每次要解析视图的时候先从缓存里面找，如果找到了对应的视图就
 * 直接返回，如果没有就创建一个新的视图对象，然后把它放到一个用于缓存的map中，接着再把新建的视图返回。使用这种视图缓存的方式可
 * 以把解析视图的性能问题降到最低。
 *
 * 2，UrlBasedViewResolver：
 * 它是对 ViewResolver的一种简单实现，而且继承了AbstractCachingViewResolver，主要就是提供的一种拼接URL的方式来解析视图，它可
 * 以让我们通过 prefix 属性指定一个指定的前缀，通过 suffix 属性指定一个指定的后缀，然后把返回的逻辑视图名称加上指定的前缀和后
 * 缀就是指定的视图URL了。
 *
 * 3，XmlViewResolver：
 * 它继承自AbstractCachingViewResolver抽象类，所以它也是支持视图缓存的。XmlViewResolver需要给定一个xml配置文件，该文件将使用
 * 和Spring的bean工厂配置文件一样的DTD定义，所以其实该文件就是用来定义视图的bean对象的。在该文件中定义的每一个视图的bean对象都
 * 给定一个名字，然后XmlViewResolver将根据Controller处理器方法返回的逻辑视图名称到XmlViewResolver指定的配置文件中寻找对应名称
 * 的视图bean用于处理视图。
 *
 * 4，BeanNameViewResolver：
 * 这个视图解析器跟XmlViewResolver有点类似，也是通过把返回的逻辑视图名称去匹配定义好的视图bean对象。
 *
 * 5，InternalResourceViewResolver：
 * 它是URLBasedViewResolver的子类，所以URLBasedViewResolver支持的特性它都支持。在实际应用中InternalResourceViewResolver也是
 * 使用的最广泛的一个视图解析器。它会把返回的视图名称都解析为InternalResourceView对象，InternalResourceView会把Controller处理
 * 器方法返回的模型属性都存放到对应的request属性中，然后通过RequestDispatcher在服务器端把请求forword重定向到目标URL。
 *
 * 6，ResourceBundleViewResolver：
 * 它也继承自AbstractCachingViewResolver，但它缓存的不是视图。和XmlViewResolver一样也需要有一个配置文件来定义逻辑视图名称和真
 * 正的View对象的对应关系，不同的是ResourceBundleViewResolver的配置文件是一个属性文件，而且必须是放在classpath路径下面的，默认
 * 情况下这个配置文件是在classpath根目录下的views.properties文件，如果不使用默认值的话，则可以通过属性baseName或baseNames来指定。
 *
 * 7，FreeMarkerViewResolver：
 * 它是UrlBasedViewResolver的子类。FreeMarkerViewResolver会把Controller处理方法返回的逻辑视图解析为FreeMarkerView。
 *
 *
 * ViewResolver分类
 * 1，面向单一视图类型的ViewResolver：
 * 该类别ViewResolver的正宗名称应该是UrlBasedViewResolver（它们都直接地或者间接地继承自该类）。使用该类别的ViewResolver，我们
 * 不需要为它们配置具体的逻辑视图名到具体View的映射关系。通常只要指定一下视图模板所在的位置，这些ViewResolver就会按照逻辑视图名，
 * 抓取相应的模板文件、构造对应的View实例并返回。之所有又将它们称之为面向单一视图类型的ViewResolver，是因为该类别中，每个具体的
 * ViewResolver实现都只负责一种View类型的映射，ViewResolver与View之间的关系是一比一。比如一直使用的InternalResourceViewResolver，
 * 它通常就只负责到指定位置抓取JSP模板文件，并构造InternalResourceView类型的View实例并返回。而VelocityViewResolver则只关心指定
 * 位置的Velocity模板文件（.vm），并会将逻辑视图名映射到视图模板的文件名，然后构造VelocityView类型的View实例返回，诸如此类。
 *
 * 该类型包含InternalResourceViewResolver，FreeMarkerViewResolver，XsltViewResolver这3个实现类。
 *
 * 2，面向多视图类型的ViewResolver：
 * 使用面向单一视图类型的ViewResolver，我们不需要指定明确的逻辑视图名与具体视图之间的映射关系，对应的ViewResolver将自动到指定位
 * 置匹配自己所管辖的那种视图模板，并构造具体的View实例。面向多视图类型的ViewResolver则不然。使用面向多视图类型的ViewResolver，
 * 我们需要通过某种配置方式明确指定逻辑视图名与具体视图之间的映射关系，这可能带来配置上的烦琐。但好处是面向多视图类型的ViewResolver
 * 可以顾及多种视图类型的映射管理。如果你的逻辑视图名想要映射到InternalResourceView，那么面向多视图类型的ViewResolver可以做到。
 * 如果你的逻辑视图名想要映射到VelocityView，那么，面向多视图类型的ViewResolver也可以做到。相对于只支持单一视图类型映射的情况，
 * 面向多视图类型的ViewResolver更加灵活。
 *
 * 该类型包含ResourceBundleViewResolver、XmlViewResolver，BeanNameViewResolver这3个实现类。
 */
// 自定义视图解析，通过配置实现多视图整合，如 jsp,velocity,freemarker,pdf,excel... 等等
public class CustomViewResolver implements ViewResolver {

    private static Logger logger = LoggerFactory.getLogger(CustomViewResolver.class);

    private Map<Set<String>,ViewResolver> viewResolverMap = new HashMap<Set<String>,ViewResolver>();

    private ViewResolver defaultViewResolver = null;

    /**
     * 在自定义视图解析器(其实是视图中转器)中，通过对视图文件的后缀判断(而不是请求地址的后缀)而返回给不同的视图解析器处理。
     * @param viewName
     * @param locale
     * @return
     * @throws Exception
     */
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {

        for(Map.Entry<Set<String>, ViewResolver> map : viewResolverMap.entrySet()){
            Set<String> suffixs = map.getKey();
            for(String suffix : suffixs){
                if (viewName.endsWith(suffix)){
                    ViewResolver viewResolver = map.getValue();
                    if(null != viewResolver){
                        if (logger.isDebugEnabled()) {
                            logger.debug("found viewResolver '" + viewResolver + "' for viewName '" + viewName+ "'");
                        }
                        return viewResolver.resolveViewName(viewName, locale);
                    }
                }
            }
        }
        if(defaultViewResolver != null){
            return defaultViewResolver.resolveViewName(viewName, locale);
        }
        // to allow for ViewResolver chaining
        return null;
    }

    public Map<Set<String>, ViewResolver> getViewResolverMap() {
        return viewResolverMap;
    }

    public void setViewResolverMap(Map<Set<String>, ViewResolver> viewResolverMap) {
        this.viewResolverMap = viewResolverMap;
    }

    public ViewResolver getDefaultViewResolver() {
        return defaultViewResolver;
    }

    public void setDefaultViewResolver(ViewResolver defaultViewResolver) {
        this.defaultViewResolver = defaultViewResolver;
    }
}
