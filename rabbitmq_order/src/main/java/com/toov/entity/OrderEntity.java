/**
 * 功能说明:
 * 功能作者:
 * 创建日期:
 * 版权归属:每特教育|蚂蚁课堂所有 www.itmayiedu.com
 */
package com.toov.entity;

import lombok.Data;

@Data
public class OrderEntity {

	private Long id;
	// 订单名称
	private String name;
	// 下单金额
	private Double orderMoney;
	// 订单id
	private String orderId;
}
