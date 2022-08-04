package net.gnfe.bin.rest.controller;


import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.rest.exception.HTTP401Exception;
import net.gnfe.bin.rest.service.SessaoHttpRequestService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


public abstract class SuperController {

	@Autowired private SessaoHttpRequestService sessaoHttpRequestService;

	protected SessaoHttpRequest getSessaoHttpRequest(HttpServletRequest request) throws HTTP401Exception {

		SessaoHttpRequest sessaoHttpRequest = sessaoHttpRequestService.findByJSessionId(request);
		if(sessaoHttpRequest == null){
			throw new HTTP401Exception("http401.exception");
		}
		return sessaoHttpRequest;
	}
}