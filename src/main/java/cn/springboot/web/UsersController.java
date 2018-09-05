package cn.springboot.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.springboot.domain.ServiceStation;
import cn.springboot.domain.Users;
import cn.springboot.service.UsersService;
import cn.springboot.util.CreateCode;
import cn.springboot.util.SpringContextUtils;
import net.minidev.json.JSONObject;

@Controller
@RequestMapping("/user")
public class UsersController {
	@Autowired
	private UsersService usersService;
	
	@Value(value="${liudong.name}")
	private String name;
	
	/**
	 * 显示list集合
	 * @param map
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value="list")
	public String getIndex(Map<String, Object> map) throws JsonProcessingException{
		List<ServiceStation> serviceStations = usersService.selectAll();
		map.put("serviceStations", serviceStations);
		return "/index";
	}
	/**
	 * 添加页面
	 * @return
	 */
	@RequestMapping("addUI")
	public String addUI(){
		return "addUI";
	}
	/**
	 * 添加
	 * @param userNa
	 * @param core
	 * @param response
	 * @throws JsonProcessingException
	 */
	@RequestMapping("add")
	public void add(String userNa,String core, HttpServletResponse response) throws JsonProcessingException{
		String str = "";
		ObjectMapper ob = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		int i = usersService.insertServiceStation(userNa,core);
		if(i>0){
			map.put("type", 0);
			map.put("massage", "添加成功");
		}else{
			map.put("type", -1);
			map.put("massage", "添加失败");
			
		}
		str = ob.writeValueAsString(map);
		responseOutWithStr(response, str);
	}
	@RequestMapping("createCore")
	public void createCore(HttpServletResponse response) throws JsonProcessingException{
		String str = "";
		ObjectMapper ob = new ObjectMapper();
		Map<String, Object> map = new HashMap<>();
		List<ServiceStation> serviceStations = usersService.selectAll();
		int count = 0;
		try {
		for(ServiceStation s : serviceStations){
			String con = "http://wx2.htjs.net/wxgzh/fwpj/fwpjRY.html?CZRY_DM="+s.getCore();
			String str1 = "F:"+File.separator+"co"+File.separator+s.getUserNa()+".png";
			String str2 = "F:"+File.separator+"co"+File.separator;
			if(isEmpty(str2)){
				CreateCode.createZxing(con, "F:/co", ""+s.getUserNa());
				upLoadAccessory(str1,s.getUserNa()+".png");//将本地文件上传到服务器上
			}else{
				delAllFile(str2);
				CreateCode.createZxing(con, "F:/co", ""+s.getUserNa());
				upLoadAccessory(str1,s.getUserNa()+".png");//将本地文件上传到服务器上
				
			}
			count++;
		}
		map.put("type", 0);
		map.put("massage", "生成成功");
		map.put("info", "生成成功，生成"+count+"个二维码。请到（F：/co）目录下查看");
		} catch (Exception e) {
			System.out.println(e);
			map.put("type", -1);
			map.put("massage", "生成失败");
		}
		str = ob.writeValueAsString(map);
		responseOutWithStr(response, str);
	}
	/**
	 * 
	 * @param pathfile 本地文件路径，
	 * @param filename 文件名
	 * @param dateName 新名字
	 * @throws IOException
	 */
	public void upLoadAccessory(String pathfile, String filename) throws IOException{
		System.out.println(pathfile+"###"+filename);
		File file = new File(pathfile);
		
		FileInputStream in_file = null;
		try {
			in_file = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MultipartFile multi = new MockMultipartFile(filename, in_file);
		WebApplicationContext webApplicationContext = (WebApplicationContext)SpringContextUtils.applicationContext; 
		System.out.println(webApplicationContext+"%%%%");
        ServletContext servletContext = webApplicationContext.getServletContext();
        System.out.println(servletContext+"%%%%");
        String realPath = servletContext.getRealPath("/");
        System.out.println(realPath+"%%%%");
        String filePath = realPath + "image"+File.separator+filename;
        String filePath1 = realPath + "image"+File.separator;
        System.out.println("绝对路径:"+filePath);
        if(isEmpty(filePath1)){
        	File newFile = new File(filePath);
        	multi.transferTo(newFile);
        }else{
        	delAllFile(filePath);
        	File newFile = new File(filePath);
        	multi.transferTo(newFile);
        }
        
        
	}
	/**
	 * 判断文件夹是否为空
	 * String path = "目标文件夹路径";
	 * @param path
	 * @return
	 */
	public boolean isEmpty(String path){
		File file = new File(path);
		File[] listFiles = file.listFiles();
		if(listFiles.length > 0){
		return false;
		} else {
			return true;
		}
	}
	/**
	 * 删除文件夹下面的所有文件
	 * @param path
	 * @return
	 */
	 public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             flag = true;
	          }
	       }
	       return flag;
	     }
	/** 
	 * 以JSON格式输出 
	 * @param response 
	 */  
	protected void responseOutWithStr(HttpServletResponse response,  
	        String str) {  
	    //将实体对象转换为JSON Object转换  
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");  
	    PrintWriter out = null;  
	    try {  
	        out = response.getWriter();  
	        out.append(str);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    }  
	} 
}
