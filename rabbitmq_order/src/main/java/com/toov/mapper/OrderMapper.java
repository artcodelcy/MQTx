package com.toov.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.toov.entity.OrderEntity;

public interface OrderMapper {

	@Insert(value = "INSERT INTO `order_info` VALUES (#{id}, #{name}, #{orderMoney},#{orderId})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int addOrder(OrderEntity orderEntity);

	@Select("SELECT id as id ,name as name , order_money as orderMoney,orderId as orderId from order_info where orderId=#{orderId};")
	public OrderEntity findOrderId(@Param("orderId") String orderId);

}
