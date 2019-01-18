package com.toov.consumer;

import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.toov.entity.OrderEntity;
import com.toov.mapper.OrderMapper;

@Component
public class CreateOrderConsumer {
@Autowired
	private OrderMapper orderMapper;

	@RabbitListener(queues = "order_create_queue")
	public void process(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
		String messageId = message.getMessageProperties().getMessageId();
		String msg = new String(message.getBody(), "UTF-8");
		System.out.println("补单消费者" + msg + ",消息id:" + messageId);
		JSONObject jsonObject = JSONObject.parseObject(msg);
		String orderId = jsonObject.getString("orderId");
		// 判断订单是否存在，如果不存在 实现自动补单机制
		OrderEntity orderEntityResult = orderMapper.findOrderId(orderId);
		if (orderEntityResult != null) {
			System.out.println("订单已经存在 无需补单  orderId:" + orderId);
			return;
		}
		// 订单不存在 ，则需要进行补单

		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setName("黄焖鸡米饭");
		// 价格是300元
		orderEntity.setOrderMoney(300d);
		//订单号
		orderEntity.setOrderId(orderId);
		// ##################################################
		// 1.先下单，创建订单 (往订单数据库中插入一条数据)
		try {
			int orderResult = orderMapper.addOrder(orderEntity);
			System.out.println("orderResult:" + orderResult);
			if (orderResult >= 0) {
				// 手动签收消息,通知mq服务器端删除该消息
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
		} catch (Exception e) {
			// 丢弃该消息
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		}

	}
}
