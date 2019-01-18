package com.toov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toov.base.BaseApiService;
import com.toov.base.ResponseBase;
import com.toov.service.OrderService;

@RestController
public class OrderController extends BaseApiService {
	@Autowired
	private OrderService orderService;

	@RequestMapping("/addOrder")
	public ResponseBase addOrder() {
		return orderService.addOrderAndDispatch();
	}

}
