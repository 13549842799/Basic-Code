package com.cyz.basic.config.security.context;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Registers the {@link DelegatingFilterProxy} to use the springSecurityFilterChain before
 * any other registered {@link Filter}. When used with
 * {@link #AbstractSecurityWebApplicationInitializer(Class...)}, it will also register a
 * {@link ContextLoaderListener}. When used with
 * {@link #AbstractSecurityWebApplicationInitializer()}, this class is typically used in
 * addition to a subclass of {@link AbstractContextLoaderInitializer}.
 *
 * <p>
 * By default the {@link DelegatingFilterProxy} is registered without support, but can be
 * enabled by overriding {@link #isAsyncSecuritySupported()} and
 * {@link #getSecurityDispatcherTypes()}.
 * </p>
 *
 * <p>
 * Additional configuration before and after the springSecurityFilterChain can be added by
 * overriding {@link #afterSpringSecurityFilterChain(ServletContext)}.
 * </p>
 *
 *
 * <h2>Caveats</h2>
 * <p>
 * Subclasses of AbstractDispatcherServletInitializer will register their filters before
 * any other {@link Filter}. This means that you will typically want to ensure subclasses
 * of AbstractDispatcherServletInitializer are invoked first. This can be done by ensuring
 * the {@link Order} or {@link Ordered} of AbstractDispatcherServletInitializer are sooner
 * than subclasses of {@link AbstractSecurityWebApplicationInitializer}.
 * </p>
 * 在javaconfig方式更加常用的现在，WebApplicationInitializer接口起到了替代web.xmk的作用，可以在它的实现类中添加
 * servel，listence等，在项目启动的时候这个接口会被调用,十分有用，可以在这里添加自己需要的过滤器或监听器等
 * 
 * @author cyz
 *
 */
public abstract class AbstractSecurityWebApplicationInitializer implements WebApplicationInitializer {
	
	private static final String SERVLET_CONTEXT_PREFIX = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.";
	
	public static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";
	
	private final Class<?>[] configurationClasses;
	
	/**
	 * Creates a new instance that assumes the Spring Security configuration is loaded by
	 * some other means than this class. For example, a user might create a
	 * {@link ContextLoaderListener} using a subclass of
	 * {@link AbstractContextLoaderInitializer}.
	 *
	 * @see ContextLoaderListener
	 */
	protected AbstractSecurityWebApplicationInitializer() {
		this.configurationClasses = null;
	}

	/**
	 * Creates a new instance that will instantiate the {@link ContextLoaderListener} with
	 * the specified classes.
	 *
	 * @param configurationClasses
	 */
	protected AbstractSecurityWebApplicationInitializer(
			Class<?>... configurationClasses) {
		this.configurationClasses = configurationClasses;
	}

	/**
	 * 实现ServletContextListener又有什么作用？ServletContextListener接口里的函数会结合Web容器的生命周期被调用。
	 * 因为ServletContextListener是ServletContext的监听者，如果ServletContext发生变化，会触发相应的事件，而监听器一直对事件监听，
	 * 如果接收到了变化，就会做出预先设计好的相应动作。由于ServletContext变化而触发的监听器的响应具体包括：在服务器启动时，ServletContext被创建的时候，
	 * 服务器关闭时，ServletContext将被销毁的时候等，相当于web的生命周期创建与效果的过程。
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		beforeSpringSecurityFilterChain(servletContext);
		if (this.configurationClasses != null) {
		    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		    context.register(this.configurationClasses);
		    servletContext.addListener(new ContextLoaderListener(context));
		}
		insertSpringSecurityFilterChain(servletContext);
		afterSpringSecurityFilterChain(servletContext);
	}
	
	private void insertSpringSecurityFilterChain(ServletContext servletContext) {
		String filterName = DEFAULT_FILTER_NAME;
		DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy(filterName);
		String contextAttribute = getWebApplicationContextAttribute();
		if (contextAttribute != null) {
			springSecurityFilterChain.setContextAttribute(contextAttribute);
		}
		registerFilter(servletContext, true, filterName, springSecurityFilterChain);
	}
	
	/**
	 * Registers the provided filter using the {@link #isAsyncSecuritySupported()} and
	 * {@link #getSecurityDispatcherTypes()}.
	 *
	 * @param servletContext
	 * @param insertBeforeOtherFilters should this Filter be inserted before or after
	 * other {@link Filter}
	 * @param filterName
	 * @param filter
	 */
	private final void registerFilter(ServletContext servletContext,
			boolean insertBeforeOtherFilters, String filterName, Filter filter) {
		Dynamic registration = servletContext.addFilter(filterName, filter);
		if (registration == null) {
			throw new IllegalStateException(
					"Duplicate Filter registration for '" + filterName
							+ "'. Check to ensure the Filter is only configured once.");
		}
		registration.setAsyncSupported(isAsyncSecuritySupported());
		EnumSet<DispatcherType> dispatcherTypes = getSecurityDispatcherTypes();
		registration.addMappingForUrlPatterns(dispatcherTypes, !insertBeforeOtherFilters,
				"/*");
	}
	
	/**
	 * Invoked after the springSecurityFilterChain is added.
	 * @param servletContext the {@link ServletContext}
	 */
	protected void afterSpringSecurityFilterChain(ServletContext servletContext) {

	}
	
	/**
	 * Invoked before the springSecurityFilterChain is added.
	 * @param servletContext the {@link ServletContext}
	 */
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {

	}
	
	/**
	 * Returns the {@link DelegatingFilterProxy#getContextAttribute()} or null if the
	 * parent {@link ApplicationContext} should be used. The default behavior is to use
	 * the parent {@link ApplicationContext}.
	 *
	 * <p>
	 * If {@link #getDispatcherWebApplicationContextSuffix()} is non-null the
	 * {@link WebApplicationContext} for the Dispatcher will be used. This means the child
	 * {@link ApplicationContext} is used to look up the springSecurityFilterChain bean.
	 * </p>
	 *
	 * @return the {@link DelegatingFilterProxy#getContextAttribute()} or null if the
	 * parent {@link ApplicationContext} should be used
	 */
	private String getWebApplicationContextAttribute() {
		String dispatcherServletName = getDispatcherWebApplicationContextSuffix();
		if (dispatcherServletName == null) {
			return null;
		}
		return SERVLET_CONTEXT_PREFIX + dispatcherServletName;
	}
	
	/**
	 * Return the &lt;servlet-name&gt; to use the DispatcherServlet's
	 * {@link WebApplicationContext} to find the {@link DelegatingFilterProxy} or null to
	 * use the parent {@link ApplicationContext}.
	 *
	 * <p>
	 * For example, if you are using AbstractDispatcherServletInitializer or
	 * AbstractAnnotationConfigDispatcherServletInitializer and using the provided Servlet
	 * name, you can return "dispatcher" from this method to use the DispatcherServlet's
	 * {@link WebApplicationContext}.
	 * </p>
	 *
	 * @return the &lt;servlet-name&gt; of the DispatcherServlet to use its
	 * {@link WebApplicationContext} or null (default) to use the parent
	 * {@link ApplicationContext}.
	 */
	protected String getDispatcherWebApplicationContextSuffix() {
		return null;
	}
	
	/**
	 * Get the {@link DispatcherType} for the springSecurityFilterChain.
	 * @return
	 */
	protected EnumSet<DispatcherType> getSecurityDispatcherTypes() {
		return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR,
				DispatcherType.ASYNC);
	}

	/**
	 * Determine if the springSecurityFilterChain should be marked as supporting asynch.
	 * Default is true.
	 *
	 * @return true if springSecurityFilterChain should be marked as supporting asynch
	 */
	protected boolean isAsyncSecuritySupported() {
		return true;
	}
}
