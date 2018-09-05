package cn.springboot.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import cn.springboot.domain.ServiceStation;
import cn.springboot.domain.Users;

@Mapper
public interface UsersDao {

	public List<ServiceStation> selectAll();

	public int insertServiceStation(Map<String, Object> map);
	

}
