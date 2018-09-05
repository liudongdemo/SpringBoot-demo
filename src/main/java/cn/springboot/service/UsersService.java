package cn.springboot.service;

import java.util.List;
import java.util.Map;

import cn.springboot.domain.ServiceStation;
import cn.springboot.domain.Users;

public interface UsersService {

	public List<ServiceStation> selectAll();

	public int insertServiceStation(String userNa, String core);

}
