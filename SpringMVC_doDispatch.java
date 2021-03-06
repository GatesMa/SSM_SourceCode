------------------------------------------------------------------------------------------------------------------
大致流程：
1）、所有的请求过来DispatcherServlet处理
2）、调用doDispatch()方法进行处理
    1、getHandler()：根据当前请求在HandlerMapping中找到这个请求的映射信息，找到能够处理这个请求的目标处理器类
    2、getHandlerAdfapter()：根据当前处理器类获取到能执行这个处理器方法的适配器
    3、使用刚才的适配器（AnnotationMethodHandlerAdpater）执行目标方法
    4、目标方法执行后返回一个ModelAndView对象
    5、根据ModelAndView的信息转发到具体的页面，并可以在请求域中取出ModelMap中的数据
------------------------------------------------------------------------------------------------------------------

源码标注版：

doDispatch()：
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;

    // 拿到一个异步请求的管理器，如果有异步请求，该怎么办
    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

    try {
        ModelAndView mv = null;
        Exception dispatchException = null;

        try {
            //（1）检查是否文件上传请求（大概看，不是重要的）
            // 检查当前请求是否文件上传请求，检查完后把request包装成新的processedRequest
            processedRequest = checkMultipart(request);
            // 根据processedRequest和request判断是不是文件上传请求，结果保存在multipartRequestParsed（Boolean）中
            multipartRequestParsed = processedRequest != request;

            //（2）根据当前请求哪个类可以处理
            // Determine handler for the current request.
            // 决定哪一个handler（Controller）可以处理当前请求，根据当前请求找到哪个类，哪个方法可以处理当前请求
            // *注意：*
            // 1. 如果没有为请求配置处理方法（映射），且没有配置default-servlet-handler，这里取出来mappedHandler的值就是null
            // 2. mappedHandler是一个HandlerExecutionChain类型的对象，包含handler（目标处理方法信息）、interceptorList（拦截器信息）等
            mappedHandler = getHandler(processedRequest);

            //（3）如果没有找到，就会404，抛异常
            if (mappedHandler == null || mappedHandler.getHandler() == null) {
                // 如果没有请求可以处理，就会抛异常，跳转到错误页面
                noHandlerFound(processedRequest, response);
                return;
            }

            // Determine handler adapter for the current request.
            // （4）拿到能执行这个类所有方法的适配器（反射工具）
            // *注意：*
            // 如果没有配置annotation-driven，这个Handler是AnnotationMethodHandlerAdpater类型的
            // 如果配置了的话这个Handler就是RequestMappingHandlerAdpater类型的
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            // Process last-modified header, if supported by the handler.
            // 判断请求的方式（Get、Post...），涉及到缓存更新，lastModified等等（这一块不用管，了解）
            String method = request.getMethod();
            boolean isGet = "GET".equals(method);
            if (isGet || "HEAD".equals(method)) {
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (logger.isDebugEnabled()) {
                    String requestUri = urlPathHelper.getRequestUri(request);
                    logger.debug("Last-Modified value for [" + requestUri + "] is: " + lastModified);
                }
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }

            // 拦截器的preHandle方法
            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }

            try {
                // 调用适配器（handlerAdpater）执行handler（Controller）的目标方法
                // （5）适配器调用目标方法，返回一个ModelAndView对象 
                // 目标方法无论怎么写，最终适配器执行完成后<都会>返回一个ModelAndView对象
                mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
            }
            finally {
                // 异步处理器判断是不是异步方法，如果是，直接请求就完了 
                if (asyncManager.isConcurrentHandlingStarted()) {
                    return;
                }
            }

            // 如果没有视图名，整一个默认的视图名，默认值是<请求的地址名>
            applyDefaultViewName(request, mv);
            mappedHandler.applyPostHandle(processedRequest, response, mv);
        }
        catch (Exception ex) {
            dispatchException = ex;
        }
        // 得到最终的页面，页面渲染成功，转发到目标页面
        // （6）根据ModelAndView，转发到目标页面
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }
    catch (Exception ex) {
        triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
    }
    catch (Error err) {
        triggerAfterCompletionWithError(processedRequest, response, mappedHandler, err);
    }
    finally {
        if (asyncManager.isConcurrentHandlingStarted()) {
            // Instead of postHandle and afterCompletion
            mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
            return;
        }
        // Clean up any resources used by a multipart request.
        if (multipartRequestParsed) {
            cleanupMultipart(processedRequest);
        }
    }
}
------------------------------------------------------------------------------------------------------------------
细节：
3）、getHandler()细节：怎么根据当前请求找到哪个类能来处理
    - 返回HandlerExecutionChain类型的对象，包含handler（目标处理方法信息）、interceptorList（拦截器信息）等

getHandler():
handlerMappings：处理器映射，它里面保存了每一个处理器可以处理哪些请求（默认两个）
    - BeanNameUrlHandlerMapping 通过配置文件
    - *DefaultAnnotationHandlerMapping 通过注解，通过注解标注的RequestMapping映射关系都在这个类的handlerMap中
为什么handlerMappings保存了映射呢？
    IOC启动会扫描所有的Controller，介时将RequestMapping映射保存在DefaultAnnotationHandlerMapping中
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    for (HandlerMapping hm : this.handlerMappings) {
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
        }
        HandlerExecutionChain handler = hm.getHandler(request);
        if (handler != null) {
            return handler;
        }
    }
    return null;
}
------------------------------------------------------------

4）、getHandlerAdapter()细节：怎么根据当前请求找到哪个类能来处理
> HandlerAdapter用于调用处理器方法，并且为处理器方法提供参数解析、返回值处理等适配工作，使使用者专心于业务逻辑的实现。

getHandlerAdapter():
遍历所有的handlerAdapters，查看哪个adapter可以support这个handler，（handlerAdapters默认有3个handlerAdapter）
    - HttpRequestHandlerAdapter 实现HttpRequestHandler接口，用于处理Http requests，其类似于一个简单的Servlet
    - SimpleControllerHandlerAdapter 实现Controller接口的处理器，
        你的自定义Controller如果实现的是Controller接口，则需要使用SimpleControllerHandlerAdapter适配器来执行自定义Controller
    - *AnnotationMethodHandlerAdapter 能解析注解方法的适配器（没有配置annotation-driven就是这个）
protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
    Iterator var2 = this.handlerAdapters.iterator();

    HandlerAdapter ha;
    do {
        if (!var2.hasNext()) {
            throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
        }

        ha = (HandlerAdapter)var2.next();
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Testing handler adapter [" + ha + "]");
        }
    } while(!ha.supports(handler));

    return ha;
}
------------------------------------------------------------------------------------------------------------------
！！！DispatcherSrvlet中有几个引用类型的属性：SpringMVC的九大组件！！！
SpringMVC在工作的时候，关键位置都是由这些组件完成的：
共同点：九大组件全都是《接口》，<接口就是规范>，接口提供了扩展性

SpringMVC的九大组件的工作原理（大佬级别）

/** MultipartResolver used by this servlet */
/** 文件上传解析器 */
private MultipartResolver multipartResolver;//多部件解析器

/** LocaleResolver used by this servlet */
/** 区域信息解析器，和国际化有关 */
private LocaleResolver localeResolver;

/** ThemeResolver used by this servlet */
/** 主题解析器，强大的主题效果更换*/
private ThemeResolver themeResolver;

/** List of HandlerMappings used by this servlet */
/** Handler映射信息 */
private List<HandlerMapping> handlerMappings;

/** List of HandlerAdapters used by this servlet */
/** Handler的适配器 */
private List<HandlerAdapter> handlerAdapters;

/** List of HandlerExceptionResolvers used by this servlet */
/** SpringMVC支持的异常解析功能 */
private List<HandlerExceptionResolver> handlerExceptionResolvers;

/** RequestToViewNameTranslator used by this servlet */
/** 不用了解，没什么用 */
private RequestToViewNameTranslator viewNameTranslator;

/** FlashMapManager used by this servlet */
/** SpringMVC中允许重定向携带数据的功能 */
private FlashMapManager flashMapManager;

/** List of ViewResolvers used by this servlet */
/** 视图解析器 */
private List<ViewResolver> viewResolvers;

------------------------------------------------------------------------------------------------------------------
～！～
DispatcherServlet 实现了onRefresh()方法，这个方法刚好对应SpringIOC容器 refresh()中留给子类实现的一步！
所以这个 onRefresh()方法会在初始化IOC容器的时候被执行，用于初始化九大组件：

@Override
protected void onRefresh(ApplicationContext context) {
    initStrategies(context);
}
/**
    * Initialize the strategy objects that this servlet uses.
    * <p>May be overridden in subclasses in order to initialize further strategy objects.
    */
组件的初始化：
    有些组件在容器中是使用类型找的，有些组件是使用ID找的
    去容器中找这个组件，如果没有找到就用默认配置
protected void initStrategies(ApplicationContext context) {
    initMultipartResolver(context);
    initLocaleResolver(context);
    initThemeResolver(context);
    initHandlerMappings(context);
    initHandlerAdapters(context);
    initHandlerExceptionResolvers(context);
    initRequestToViewNameTranslator(context);
    initViewResolvers(context);
    initFlashMapManager(context);
}

例如：初始化HanderMappings：
会加载DispatcherServlet.properties 里九大组件的默认类，
例如HadlerMapping就是BeanNameUrlHandlerMapping和DefaultAnnotationHandlerMapping
private void initHandlerMappings(ApplicationContext context) {
    this.handlerMappings = null;

    // 可以在web.xml 中修该DispatcherServlet的默认属性，例如detectAllHandlerMappings

    // 在IOC容器中找到所有HandlerMapping的子类
    if (this.detectAllHandlerMappings) {
        // Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
        Map<String, HandlerMapping> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
            // We keep HandlerMappings in sorted order.
            OrderComparator.sort(this.handlerMappings);
        }
    }
    else {
        // 如果没有找到，就在容器中找ID是“handlerMapping”的元素
        try {
            HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
            this.handlerMappings = Collections.singletonList(hm);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // Ignore, we'll add a default HandlerMapping later.
        }
    }

    // Ensure we have at least one HandlerMapping, by registering
    // a default HandlerMapping if no other mappings are found.
    // 如果还是没有，就使用默认的handlerMapping <getDefaultStrategies>
    // ！！！会加载DispatcherServlet.properties里九大组件的默认类！！！
    // 
    if (this.handlerMappings == null) {
        this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
        if (logger.isDebugEnabled()) {
            logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
        }
    }
}

------------------------------------------------------------------------------------------------------------------
锁定到目标方法的执行，handle()的细节（反射如何确定参数等）：
mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

@Override
public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
    // 获取handler所在类的类型（@Controller标注的类的类型）
    Class<?> clazz = ClassUtils.getUserClass(handler);
    // 判断这个类是不是标注了SessionAttribute注解
    Boolean annotatedWithSessionAttributes = this.sessionAnnotatedClassesCache.get(clazz);
    if (annotatedWithSessionAttributes == null) {
        annotatedWithSessionAttributes = (AnnotationUtils.findAnnotation(clazz, SessionAttributes.class) != null);
        this.sessionAnnotatedClassesCache.put(clazz, annotatedWithSessionAttributes);
    }

    if (annotatedWithSessionAttributes) {
        // Always prevent caching in case of session attribute management.
        checkAndPrepare(request, response, this.cacheSecondsForSessionAttributeHandlers, true);
        // Prepare cached set of session attributes names.
    }
    else {
        // Uses configured default cacheSeconds setting.
        // 不是核心，直接放过，和缓存有关
        checkAndPrepare(request, response, true);
    }

    // Execute invokeHandlerMethod in synchronized block if required.
    if (this.synchronizeOnSession) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                return invokeHandlerMethod(request, response, handler);
            }
        }
    }

    return invokeHandlerMethod(request, response, handler);
}




protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
    // 拿到类的方法解析器
    ServletHandlerMethodResolver methodResolver = getMethodResolver(handler);
    // 根据request请求解析用哪个方法来执行
    Method handlerMethod = methodResolver.resolveHandlerMethod(request);
    // 整一个方法执行器
    ServletHandlerMethodInvoker methodInvoker = new ServletHandlerMethodInvoker(methodResolver);
    // request、response包装一下
    ServletWebRequest webRequest = new ServletWebRequest(request, response);
    // 获取implicitModel隐含模型（是一个BindingAwareModelMap对象，给这个Map放的东西可以放在请求域中）    implicit：含蓄的、隐含
    ExtendedModelMap implicitModel = new BindingAwareModelMap();

    // ----------
    // 这一步是真正执行方法，获得一个返回值，可能是一个字符串，例如（success）（目标方法执行期间确定参数值，提前执行modelAttribute等所有操作
    // 都在这一步）
    Object result = methodInvoker.invokeHandlerMethod(handlerMethod, handler, webRequest, implicitModel);
    // ----------
    ModelAndView mav =
            methodInvoker.getModelAndView(handlerMethod, handler.getClass(), result, implicitModel, webRequest);
    methodInvoker.updateModelAttributes(handler, (mav != null ? mav.getModel() : null), implicitModel, webRequest);
    return mav;
}

public final Object invokeHandlerMethod(Method handlerMethod, Object handler,
			NativeWebRequest webRequest, ExtendedModelMap implicitModel) throws Exception {

    Method handlerMethodToInvoke = BridgeMethodResolver.findBridgedMethod(handlerMethod);
    try {
        boolean debug = logger.isDebugEnabled();
        // 将SessionAtributes的对象如果有值，加入到隐含模型
        for (String attrName : this.methodResolver.getActualSessionAttributeNames()) {
            Object attrValue = this.sessionAttributeStore.retrieveAttribute(webRequest, attrName);
            if (attrValue != null) {
                implicitModel.addAttribute(attrName, attrValue);
            }
        }
        // 执行所有的ModelAttribute标注的方法，将返回值加入到隐含模型
        for (Method attributeMethod : this.methodResolver.getModelAttributeMethods()) {
            Method attributeMethodToInvoke = BridgeMethodResolver.findBridgedMethod(attributeMethod);
            // 确定ModelAttribute方法执行时要使用的每一个参数的值
            Object[] args = resolveHandlerArguments(attributeMethodToInvoke, handler, webRequest, implicitModel);
            if (debug) {
                logger.debug("Invoking model attribute method: " + attributeMethodToInvoke);
            }
            String attrName = AnnotationUtils.findAnnotation(attributeMethod, ModelAttribute.class).value();
            if (!"".equals(attrName) && implicitModel.containsAttribute(attrName)) {
                continue;
            }
            ReflectionUtils.makeAccessible(attributeMethodToInvoke);
            // 反射执行方法
            Object attrValue = attributeMethodToInvoke.invoke(handler, args);
            if ("".equals(attrName)) {
                Class<?> resolvedType = GenericTypeResolver.resolveReturnType(attributeMethodToInvoke, handler.getClass());
                attrName = Conventions.getVariableNameForReturnType(attributeMethodToInvoke, resolvedType, attrValue);
            }
            if (!implicitModel.containsAttribute(attrName)) {
                implicitModel.addAttribute(attrName, attrValue);
            }
        }
        Object[] args = resolveHandlerArguments(handlerMethodToInvoke, handler, webRequest, implicitModel);
        if (debug) {
            logger.debug("Invoking request handler method: " + handlerMethodToInvoke);
        }
        ReflectionUtils.makeAccessible(handlerMethodToInvoke);
        return handlerMethodToInvoke.invoke(handler, args);
    }
    catch (IllegalStateException ex) {
        // Internal assertion failed (e.g. invalid signature):
        // throw exception with full handler method context...
        throw new HandlerMethodInvocationException(handlerMethodToInvoke, ex);
    }
    catch (InvocationTargetException ex) {
        // User-defined @ModelAttribute/@InitBinder/@RequestMapping method threw an exception...
        ReflectionUtils.rethrowException(ex.getTargetException());
        return null;
    }
}
------------------------------------------------------------------------------------------------------------------

ModelAttribute标注的方法提前运行，并且把执行后的返回值加入到隐含模型中（方法执行的细节）：
----resolveHandlerArguments确定方法每一个参数的值----
private Object[] resolveHandlerArguments(Method handlerMethod, Object handler,
			NativeWebRequest webRequest, ExtendedModelMap implicitModel) throws Exception {

    Class<?>[] paramTypes = handlerMethod.getParameterTypes();
    Object[] args = new Object[paramTypes.length];

    for (int i = 0; i < args.length; i++) {
        MethodParameter methodParam = new MethodParameter(handlerMethod, i);
        methodParam.initParameterNameDiscovery(this.parameterNameDiscoverer);
        GenericTypeResolver.resolveParameterType(methodParam, handler.getClass());
        String paramName = null;
        String headerName = null;
        boolean requestBodyFound = false;
        String cookieName = null;
        String pathVarName = null;
        String attrName = null;
        boolean required = false;
        String defaultValue = null;
        boolean validate = false;
        Object[] validationHints = null;
        int annotationsFound = 0;
        Annotation[] paramAnns = methodParam.getParameterAnnotations();

        for (Annotation paramAnn : paramAnns) {
            if (RequestParam.class.isInstance(paramAnn)) {
                RequestParam requestParam = (RequestParam) paramAnn;
                paramName = requestParam.value();
                required = requestParam.required();
                defaultValue = parseDefaultValueAttribute(requestParam.defaultValue());
                annotationsFound++;
            }
            else if (RequestHeader.class.isInstance(paramAnn)) {
                RequestHeader requestHeader = (RequestHeader) paramAnn;
                headerName = requestHeader.value();
                required = requestHeader.required();
                defaultValue = parseDefaultValueAttribute(requestHeader.defaultValue());
                annotationsFound++;
            }
            else if (RequestBody.class.isInstance(paramAnn)) {
                requestBodyFound = true;
                annotationsFound++;
            }
            else if (CookieValue.class.isInstance(paramAnn)) {
                CookieValue cookieValue = (CookieValue) paramAnn;
                cookieName = cookieValue.value();
                required = cookieValue.required();
                defaultValue = parseDefaultValueAttribute(cookieValue.defaultValue());
                annotationsFound++;
            }
            else if (PathVariable.class.isInstance(paramAnn)) {
                PathVariable pathVar = (PathVariable) paramAnn;
                pathVarName = pathVar.value();
                annotationsFound++;
            }
            else if (ModelAttribute.class.isInstance(paramAnn)) {
                ModelAttribute attr = (ModelAttribute) paramAnn;
                attrName = attr.value();
                annotationsFound++;
            }
            else if (Value.class.isInstance(paramAnn)) {
                defaultValue = ((Value) paramAnn).value();
            }
            else if (paramAnn.annotationType().getSimpleName().startsWith("Valid")) {
                validate = true;
                Object value = AnnotationUtils.getValue(paramAnn);
                validationHints = (value instanceof Object[] ? (Object[]) value : new Object[] {value});
            }
        }

        if (annotationsFound > 1) {
            throw new IllegalStateException("Handler parameter annotations are exclusive choices - " +
                    "do not specify more than one such annotation on the same parameter: " + handlerMethod);
        }

        if (annotationsFound == 0) {
            Object argValue = resolveCommonArgument(methodParam, webRequest);
            if (argValue != WebArgumentResolver.UNRESOLVED) {
                args[i] = argValue;
            }
            else if (defaultValue != null) {
                args[i] = resolveDefaultValue(defaultValue);
            }
            else {
                Class<?> paramType = methodParam.getParameterType();
                if (Model.class.isAssignableFrom(paramType) || Map.class.isAssignableFrom(paramType)) {
                    if (!paramType.isAssignableFrom(implicitModel.getClass())) {
                        throw new IllegalStateException("Argument [" + paramType.getSimpleName() + "] is of type " +
                                "Model or Map but is not assignable from the actual model. You may need to switch " +
                                "newer MVC infrastructure classes to use this argument.");
                    }
                    args[i] = implicitModel;
                }
                else if (SessionStatus.class.isAssignableFrom(paramType)) {
                    args[i] = this.sessionStatus;
                }
                else if (HttpEntity.class.isAssignableFrom(paramType)) {
                    args[i] = resolveHttpEntityRequest(methodParam, webRequest);
                }
                else if (Errors.class.isAssignableFrom(paramType)) {
                    throw new IllegalStateException("Errors/BindingResult argument declared " +
                            "without preceding model attribute. Check your handler method signature!");
                }
                else if (BeanUtils.isSimpleProperty(paramType)) {
                    paramName = "";
                }
                else {
                    attrName = "";
                }
            }
        }

        if (paramName != null) {
            args[i] = resolveRequestParam(paramName, required, defaultValue, methodParam, webRequest, handler);
        }
        else if (headerName != null) {
            args[i] = resolveRequestHeader(headerName, required, defaultValue, methodParam, webRequest, handler);
        }
        else if (requestBodyFound) {
            args[i] = resolveRequestBody(methodParam, webRequest, handler);
        }
        else if (cookieName != null) {
            args[i] = resolveCookieValue(cookieName, required, defaultValue, methodParam, webRequest, handler);
        }
        else if (pathVarName != null) {
            args[i] = resolvePathVariable(pathVarName, methodParam, webRequest, handler);
        }
        else if (attrName != null) {
            WebDataBinder binder =
                    resolveModelAttribute(attrName, methodParam, implicitModel, webRequest, handler);
            boolean assignBindingResult = (args.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
            if (binder.getTarget() != null) {
                doBind(binder, webRequest, validate, validationHints, !assignBindingResult);
            }
            args[i] = binder.getTarget();
            if (assignBindingResult) {
                args[i + 1] = binder.getBindingResult();
                i++;
            }
            implicitModel.putAll(binder.getBindingResult().getModel());
        }
    }

    return args;
}
------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------
