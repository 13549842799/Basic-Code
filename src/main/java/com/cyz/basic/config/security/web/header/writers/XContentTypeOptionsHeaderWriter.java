package com.cyz.basic.config.security.web.header.writers;

public final class XContentTypeOptionsHeaderWriter extends StaticHeadersWriter {
	/**
	 * Creates a new instance
	 */
	public XContentTypeOptionsHeaderWriter() {
		super("X-Content-Type-Options", "nosniff");
	}
}
