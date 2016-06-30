package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import db.DataBase;
import model.User;
import org.junit.Test;

import pojo.UrlData;
import util.HttpRequestUtils.Pair;
import webserver.Service;

public class HttpRequestUtilsTest {
	@Test
	public void isResource(){
		String s = "/user/create?userId=id_admin&password=3329&name=&email=";
		assertThat(HttpRequestUtils.isResource(s), is(false));
	}

	@Test
	public void showAllUser() throws Exception{
		for(int i=0;i<10;i++){
			DataBase.addUser(new User(String.valueOf(i), String.valueOf(i), String.valueOf(i), String.valueOf(i)));
		}

		for(User user : DataBase.findAll()){
			System.out.println(user.toString());
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(Service.findAll());
	}
	@Test
	public void getRestMethod(){
		String url = "/user/create?userId=id_admin&password=3329&name=&email=";
		UrlData urlData = HttpRequestUtils.getUrlData(url);
		assertThat(urlData.getModel(), is("user"));
		assertThat(urlData.getMethod(), is("create"));
	}

	@Test
	public void userDataToUser(){
		String url = "/user/create?userId=id_admin&password=3329&name=&email=";
		UrlData urlData = HttpRequestUtils.getUrlData(url);
		User user = new User(urlData.getParams());
		assertThat(user.getUserId(), is("id_admin"));
		assertThat(user.getPassword(), is("3329"));
	}

	@Test
	public void parseQueryString() {
		String queryString = "userId=javajigi";
		Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
		assertThat(parameters.get("userId"), is("javajigi"));
		assertThat(parameters.get("password"), is(nullValue()));
		
		queryString = "userId=javajigi&password=password2";
		parameters = HttpRequestUtils.parseQueryString(queryString);
		assertThat(parameters.get("userId"), is("javajigi"));
		assertThat(parameters.get("password"), is("password2"));
	}
	
	@Test
	public void parseQueryString_null() {
		Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
		assertThat(parameters.isEmpty(), is(true));
		
		parameters = HttpRequestUtils.parseQueryString("");
		assertThat(parameters.isEmpty(), is(true));
		
		parameters = HttpRequestUtils.parseQueryString(" ");
		assertThat(parameters.isEmpty(), is(true));
	}
	
	@Test
	public void parseQueryString_invalid() {
		String queryString = "userId=javajigi&password";
		Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
		assertThat(parameters.get("userId"), is("javajigi"));
		assertThat(parameters.get("password"), is(nullValue()));
	}
	
	@Test
	public void parseCookies() {
		String cookies = "logined=true; JSessionId=1234";
		Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
		assertThat(parameters.get("logined"), is("true"));
		assertThat(parameters.get("JSessionId"), is("1234"));
		assertThat(parameters.get("session"), is(nullValue()));
	}
	
	@Test
	public void getKeyValue() throws Exception {
		Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
		assertThat(pair, is(new Pair("userId", "javajigi")));
	}
	
	@Test
	public void getKeyValue_invalid() throws Exception {
		Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
		assertThat(pair, is(nullValue()));
	}
	
	@Test
	public void parseHeader() throws Exception {
		String header = "Content-Length: 59";
		Pair pair = HttpRequestUtils.parseHeader(header);
		assertThat(pair, is(new Pair("Content-Length", "59")));
	}
}
