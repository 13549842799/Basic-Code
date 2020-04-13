package com.cyz.basic.config.security.config.annotation.web.configurers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.cyz.basic.config.security.config.annotation.web.HttpSecurityBuilder;
import com.cyz.basic.config.security.web.header.HeaderWriter;
import com.cyz.basic.config.security.web.header.HeaderWriterFilter;
import com.cyz.basic.config.security.web.header.writers.XContentTypeOptionsHeaderWriter;

/**
 * <p>
 * Adds the Security HTTP headers to the response. Security HTTP headers is activated by
 * default when using {@link WebSecurityConfigurerAdapter}'s default constructor.
 * </p>
 *
 * <p>
 * The default headers include are:
 * </p>
 *
 * <pre>
 * Cache-Control: no-cache, no-store, max-age=0, must-revalidate
 * Pragma: no-cache
 * Expires: 0
 * X-Content-Type-Options: nosniff
 * Strict-Transport-Security: max-age=31536000 ; includeSubDomains
 * X-Frame-Options: DENY
 * X-XSS-Protection: 1; mode=block
 * </pre>
 *
 * @author Rob Winch
 * @author Tim Ysewyn
 * @author Joe Grandja
 * @author Edd煤 Mel茅ndez
 * @author Vedran Pavic
 * @since 3.2
 */
public class HeadersConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractHttpConfigurer<HeadersConfigurer<H>, H> {
	
	private List<HeaderWriter> headerWriters = new ArrayList<>();
	
	private final ContentTypeOptionsConfig contentTypeOptions = new ContentTypeOptionsConfig();
	
	/**
	 * Creates a new instance
	 *
	 * @see HttpSecurity#headers()
	 */
	public HeadersConfigurer() {
	}
	
	/**
	 * Configures the {@link XContentTypeOptionsHeaderWriter} which inserts the <a href=
	 * "https://msdn.microsoft.com/en-us/library/ie/gg622941(v=vs.85).aspx"
	 * >X-Content-Type-Options</a>:
	 *
	 * <pre>
	 * X-Content-Type-Options: nosniff
	 * </pre>
	 *
	 * @return the ContentTypeOptionsConfig for additional customizations
	 */
	public ContentTypeOptionsConfig contentTypeOptions() {
		return contentTypeOptions.enable();
	}

	public final class ContentTypeOptionsConfig {
		private XContentTypeOptionsHeaderWriter writer;

		private ContentTypeOptionsConfig() {
			enable();
		}

		/**
		 * Removes the X-XSS-Protection header.
		 *
		 * @return {@link HeadersConfigurer} for additional customization.
		 */
		public HeadersConfigurer<H> disable() {
			writer = null;
			return and();
		}

		/**
		 * Allows customizing the {@link HeadersConfigurer}
		 * @return the {@link HeadersConfigurer} for additional customization
		 */
		public HeadersConfigurer<H> and() {
			return HeadersConfigurer.this;
		}

		/**
		 * Ensures that Content Type Options is enabled
		 *
		 * @return the {@link ContentTypeOptionsConfig} for additional customization
		 */
		private ContentTypeOptionsConfig enable() {
			if (writer == null) {
				writer = new XContentTypeOptionsHeaderWriter();
			}
			return this;
		}
	}
	
	/**
	 * Adds a {@link HeaderWriter} instance
	 *
	 * @param headerWriter the {@link HeaderWriter} instance to add
	 * @return the {@link HeadersConfigurer} for additional customizations
	 */
	public HeadersConfigurer<H> addHeaderWriter(HeaderWriter headerWriter) {
		Assert.notNull(headerWriter, "headerWriter cannot be null");
		this.headerWriters.add(headerWriter);
		return this;
	}
	
	@Override
	public void configure(H http) throws Exception {
		HeaderWriterFilter headersFilter = createHeaderWriterFilter();
		http.addFilter(headersFilter);
	}
	
	/**
	 * Creates the {@link HeaderWriter}
	 *
	 * @return the {@link HeaderWriter}
	 */
	private HeaderWriterFilter createHeaderWriterFilter() {
		List<HeaderWriter> writers = getHeaderWriters();
		if (writers.isEmpty()) {
			throw new IllegalStateException(
					"Headers security is enabled, but no headers will be added. Either add headers or disable headers security");
		}
		HeaderWriterFilter headersFilter = new HeaderWriterFilter(writers);
		headersFilter = postProcess(headersFilter);
		return headersFilter;
	}
	
	/**
	 * Gets the {@link HeaderWriter} instances and possibly initializes with the defaults.
	 *
	 * @return
	 */
	private List<HeaderWriter> getHeaderWriters() {
		List<HeaderWriter> writers = new ArrayList<>();
		addIfNotNull(writers, contentTypeOptions.writer);
		/*addIfNotNull(writers, xssProtection.writer);
		addIfNotNull(writers, cacheControl.writer);
		addIfNotNull(writers, hsts.writer);
		addIfNotNull(writers, frameOptions.writer);
		addIfNotNull(writers, hpkp.writer);
		addIfNotNull(writers, contentSecurityPolicy.writer);
		addIfNotNull(writers, referrerPolicy.writer);
		addIfNotNull(writers, featurePolicy.writer);*/
		writers.addAll(headerWriters);
		return writers;
	}
	
	private <T> void addIfNotNull(List<T> values, T value) {
		if (value != null) {
			values.add(value);
		}
	}
	
	

}
