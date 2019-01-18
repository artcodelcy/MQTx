package com.toov.mapper;

import org.apache.ibatis.annotations.Insert;

import com.toov.entity.DispatchEntity;

public interface DispatchMapper {

	/**
	 * 新增派单任务
	 */
	@Insert("INSERT into platoon values (null,#{orderId},#{takeoutUserId});")
	public int insertDistribute(DispatchEntity distributeEntity);

}
