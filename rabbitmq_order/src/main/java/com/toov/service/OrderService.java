package com.toov.service;

import java.util.UUID;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.toov.base.BaseApiService;
import com.toov.base.ResponseBase;
import com.toov.entity.OrderEntity;
import com.toov.mapper.OrderMapper;

@Service
public class OrderService extends BaseApiService implements RabbitTemplate.ConfirmCallback {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public ResponseBase addOrderAndDispatch() {
		//先下单 订单表插入数据
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setName("黄焖鸡米饭");
		// 价格是300元
		orderEntity.setOrderMoney(300d);
		// 商品id
		String orderId = UUID.randomUUID().toString();
		orderEntity.setOrderId(orderId);
		// 1.先下单，创建订单 (往订单数据库中插入一条数据)
		int orderResult = orderMapper.addOrder(orderEntity);
		System.out.println("orderResult:" + orderResult);
		if (orderResult <= 0) {
			return setResultError("下单失败!");
		}
		// 2.订单表插插入完数据后 订单表发送 外卖小哥
		send(orderId);
	//	int i = 1/0;   //发生异常
		return setResultSuccess();
	}

	private void send(String orderId) {
		JSONObject jsonObect = new JSONObject();
		jsonObect.put("orderId", orderId);
		String msg = jsonObect.toJSONString();
		System.out.println("msg:" + msg);
		// 封装消息
		Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
				.setContentEncoding("utf-8").setMessageId(orderId).build();
		// 构建回调返回的数据
		CorrelationData correlationData = new CorrelationData(orderId);
		// 发送消息
		this.rabbitTemplate.setMandatory(true);
		this.rabbitTemplate.setConfirmCallback(this);
		rabbitTemplate.convertAndSend("order_exchange_name", "orderRoutingKey", message, correlationData);

	}

	// 生产消息确认机制 生产者往服务器端发送消息的时候 采用应答机制
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		String orderId = correlationData.getId(); //id 都是相同的哦  全局ID
		System.out.println("消息id:" + correlationData.getId()); 
		if (ack) { //消息发送成功
			System.out.println("消息发送确认成功");
		} else {
			//重试机制
			send(orderId); 
			System.out.println("消息发送确认失败:" + cause);
		}

	}

}
