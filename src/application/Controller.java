package application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Controller implements Initializable {

    @FXML
    private Label WarningLabel;
    //警报器

    @FXML
    private Label HumiLabel;
    //湿度传感器

    @FXML
    private Label LightsLabel;
    //光照传感器

    @FXML
    private Label Lamp1;
    //灯1

    @FXML
    private Label Lamp2;
    //灯2
    
    @FXML
    private Label Lamp3;
    //灯3

    @FXML
    private Label TempLabel;
    //温度传感器
    
    @FXML
    private ImageView warnImage;
    //警报情况图片
    
    @FXML
    private ImageView lightsimage;
    //三盏灯情况图片
    
    @FXML
    private Button zigbeelightbutton123;
	private boolean zigbeeLightOpen = false;
    //zigbee灯光控制
	
    @FXML
    private Button linelightbutton234;
    private boolean lineLight1Open = false;
    @FXML
    private Button linelightbutton345;
    private boolean lineLight2Open = false;
    //一区二区模拟街道灯点亮
	
	private static final String LOGIN_URL = "http://api.nlecloud.com/Users/Login";
	//获取设备数据请求地址
	private static final String DEVIDE_DATA_URL = "http://api.nlecloud.com/Devices/Datas?devIds=397844";
	//控制设备请求地址
	private static final String DEVIDE_CONTROL_URL = "http://api.nlecloud.com/Cmds?deviceId=397844&apiTag=";

	private static final String ACCOUNT = "17850811503";
	private static final String PASSWORD = "minecraft0";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 创建子线程
		new HttpThread().start();
	}

	class HttpThread extends Thread {
		@Override
		public void run() {
			// 发送Http请求
			// url ---> 协议 + 域名 + 端口号 + 文件路径
			try {
				String json = "{\r\n" + "  \"Account\": \"" + ACCOUNT + "\",\r\n" + "  \"Password\": \"" + PASSWORD
						+ "\"\r\n" + "}";
				String result = OkHttpHelper.post(LOGIN_URL, json);
				parseLoginResult(result);

				// 登录鉴权成功
				if (OkHttpHelper.accessToken != null) {
					// 定时器：每隔1秒，发送获取数据请求
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							// 发送获取设备数据请求
							String dataResult;
							try {
								dataResult = OkHttpHelper.get(DEVIDE_DATA_URL);
								parseDeviceDataResult(dataResult);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}, 1000, 1000);// 延迟1秒发送请求，间隔1秒
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 解析登录结果
	private void parseLoginResult(String result) {
		// json解析
		JSONObject jsonObject = JSONObject.parseObject(result);
		// 先判断Status是否是0
		int status = jsonObject.getIntValue("Status");
		if (status != 0) {// 登录失败
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("登录失败，请检查代码的账号密码配置！");
			alert.show();
			return;
		}
		// 登录成功的处理
		JSONObject resultObj = jsonObject.getJSONObject("ResultObj");
		OkHttpHelper.accessToken = resultObj.getString("AccessToken");
		//System.out.println("访问令牌是：" + OkHttpHelper.accessToken);
	}

		// 解析设备数据结果
	private void parseDeviceDataResult(String result) {
		JSONObject jsonObject = JSONObject.parseObject(result);
		// 先判断Status是否是0
		int status = jsonObject.getIntValue("Status");
		if (status != 0) {// 获取设备数据失败
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("获取不到传感器的数据，请检查设备id是否正确！");
			alert.show();
			return;
		}

		// status状态正常情况的处理
		JSONObject deviceData = jsonObject.getJSONArray("ResultObj").getJSONObject(0);// 结果ResultObj的第一条数据就是网关设备数据
		System.out.println(deviceData);
		JSONArray datas = deviceData.getJSONArray("Datas");
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < datas.size(); i++) {
			JSONObject data = datas.getJSONObject(i);
			String ApiTag = data.getString("ApiTag");
			String Value = data.getString("Value");
			System.out.print(ApiTag);
			System.out.print(Value);
			map.put(ApiTag, Value);
		}

		// 修改成自己的传感器api
		String lightValue = map.get("z_light_zigbee");// zigbee光照传感器标识名
		String tempValue = map.get("z_temperature");// zigbee四输入温度传感器标识名
		String humiValue = map.get("z_humidity");// zigbee四输入湿度传感器标识名
		String lamp1 = map.get("m_lamp_4150");//继电器灯光1
		String lamp2 = map.get("m_lamp2_4150");//继电器灯光2
		String lamp3 = map.get("z_light_shuru");//zigbee灯光
		//String alarm = map.get("m_alarmlight");//警报
		
		// 将传感器数据显示在界面上
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
					
				final String LAMPWORD = "街灯";
					
				LightsLabel.setText(lightValue + "lx");
				TempLabel.setText(tempValue + "°C");
				HumiLabel.setText(humiValue + "%RH");
				if(lamp1 == "true") {
					Lamp1.setText(LAMPWORD + "已开");
				} else {
					Lamp1.setText(LAMPWORD + "已灭");
				}
				if(lamp2 == "true") {
					Lamp2.setText(LAMPWORD + "已开");
				} else {
					Lamp2.setText(LAMPWORD + "已灭");
				}
				if(lamp3 == "true") {
					Lamp3.setText(LAMPWORD + "已开");
				} else {
					Lamp3.setText(LAMPWORD + "已灭");
				}
					
				//默认
				warnImage.setImage(new Image("file:images/yesf.png"));
				//街上只要有一盏灯孤独的亮或者孤独的灭，就报警！！！
				/***
				* 000 false
				001 true
				010 true
				011 true
				100 true
				101 true
				110 true
				111 false

				lamp1 == lamp2 && lamp2 != lamp3
				lamp1 != lamp2 && lamp2 == lamp3
				lamp1 == lamp3 && lamp2 != lamp3

				lamp1 == lamp2 && lamp2 == lamp3 && lamp1 == lamp3
				*/
				if((lamp1 == lamp2 && lamp2 != lamp3) || (lamp1 != lamp2 && lamp2 == lamp3) || (lamp1 == lamp3 && lamp2 != lamp3)) {
					warnImage.setImage(new Image("file:images/warning.png"));
				} else if (lamp1 == lamp2 && lamp2 == lamp3 && lamp1 == lamp3) {
					warnImage.setImage(new Image("/iconfont/yesf.png"));
				}
				if (lamp1 == lamp2 && lamp2 == lamp3 && lamp1 == lamp3) {
					lightsimage.setImage(new Image("/iconfont/Shining.png"));
				} else {
					lightsimage.setImage(new Image("/iconfont/dark.png"));
				}
				
			}
		});
	}
	@FXML
	void zigbeelightbutton(ActionEvent event) {
		String HaohanyhNo1 = "一区Zigbee";
	    String text=zigbeelightbutton123.getText();
	    if(text.equals(HaohanyhNo1 + "灯开")) {
	    	zigbeelightbutton123.setText(HaohanyhNo1 + "灯关");
	    	zigbeeLightOpen = false;
	    } else {
	    	zigbeelightbutton123.setText(HaohanyhNo1 + "灯开");
	    	zigbeeLightOpen = true;
	    }
	    new zigbeelightControlThread().start();
	}
	

    @FXML
    void linelightbuttonone(ActionEvent event) {
    	String HaohanyhNo1 = "一区有线街";
	    String text=linelightbutton234.getText();
	    if(text.equals(HaohanyhNo1 + "灯开")) {
	    	linelightbutton234.setText(HaohanyhNo1 + "灯关");
	    	lineLight1Open = false;
	    } else {
	    	linelightbutton234.setText(HaohanyhNo1 + "灯开");
	    	lineLight1Open = true;
	    }
	    new linelightControlThread().start();
    }

    @FXML
    void linelightbuttontwo(ActionEvent event) {
    	String HaohanyhNo1 = "二区有线街";
	    String text=linelightbutton345.getText();
	    if(text.equals(HaohanyhNo1 + "灯开")) {
	    	linelightbutton345.setText(HaohanyhNo1 + "灯关");
	    	lineLight2Open = false;
	    } else {
	    	linelightbutton345.setText(HaohanyhNo1 + "灯开");
	    	lineLight2Open = true;
	    }
	    new linelight2ControlThread().start();
    }
	    
	class zigbeelightControlThread extends Thread {
		@Override
		public void run() {
			if(OkHttpHelper.accessToken!=null) {
				try {
					OkHttpHelper.post(DEVIDE_CONTROL_URL+"z_light_shuru" ,zigbeeLightOpen?"0":"1");
 				}catch (IOException e) {
					e.printStackTrace();
				} 
			}		
			super.run();
 		}
	}
	
	class linelightControlThread extends Thread {
		@Override
		public void run() {
			if(OkHttpHelper.accessToken!=null) {
				try {
					OkHttpHelper.post(DEVIDE_CONTROL_URL+"m_lamp_4150" ,lineLight1Open?"0":"1");
 				}catch (IOException e) {
					e.printStackTrace();
				} 
			}		
			super.run();
 		}
	}
	
	class linelight2ControlThread extends Thread {
		@Override
		public void run() {
			if(OkHttpHelper.accessToken!=null) {
				try {
					OkHttpHelper.post(DEVIDE_CONTROL_URL+"m_lamp2_4150" ,lineLight2Open?"0":"1");
 				}catch (IOException e) {
					e.printStackTrace();
				} 
			}		
			super.run();
 		}
	}
}
