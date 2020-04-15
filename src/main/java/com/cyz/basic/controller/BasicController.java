package com.cyz.basic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cyz.basic.valid.service.ValidService;

public abstract class BasicController {
	
    protected final static Logger logger = LoggerFactory.getLogger(BasicController.class);
	
	@Autowired
	protected ValidService validUtil;
	
}
