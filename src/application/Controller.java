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
    //������

    @FXML
    private Label HumiLabel;
    //ʪ�ȴ�����

    @FXML
    private Label LightsLabel;
    //���մ�����

    @FXML
    private Label Lamp1;
    //��1

    @FXML
    private Label Lamp2;
    //��2
    
    @FXML
    private Label Lamp3;
    //��3

    @FXML
    private Label TempLabel;
    //�¶ȴ�����
    
    @FXML
    private ImageView warnImage;
    //�������ͼƬ
    
    @FXML
    private ImageView lightsimage;
    //��յ�����ͼƬ
    
    @FXML
    private Button zigbeelightbutton123;
	private boolean zigbeeLightOpen = false;
    //zigbee�ƹ����
	
    @FXML
    private Button linelightbutton234;
    private boolean lineLight1Open = false;
    @FXML
    private Button linelightbutton345;
    private boolean lineLight2Open = false;
    //һ������ģ��ֵ��Ƶ���
	
	private static final String LOGIN_URL = "http://api.nlecloud.com/Users/Login";
	//��ȡ�豸���������ַ
	private static final String DEVIDE_DATA_URL = "http://api.nlecloud.com/Devices/Datas?devIds=397844";
	//�����豸�����ַ
	private static final String DEVIDE_CONTROL_URL = "http://api.nlecloud.com/Cmds?deviceId=397844&apiTag=";

	private static final String ACCOUNT = "17850811503";
	private static final String PASSWORD = "minecraft0";

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// �������߳�
		new HttpThread().start();
	}

	class HttpThread extends Thread {
		@Override
		public void run() {
			// ����Http����
			// url ---> Э�� + ���� + �˿ں� + �ļ�·��
			try {
				String json = "{\r\n" + "  \"Account\": \"" + ACCOUNT + "\",\r\n" + "  \"Password\": \"" + PASSWORD
						+ "\"\r\n" + "}";
				String result = OkHttpHelper.post(LOGIN_URL, json);
				parseLoginResult(result);

				// ��¼��Ȩ�ɹ�
				if (OkHttpHelper.accessToken != null) {
					// ��ʱ����ÿ��1�룬���ͻ�ȡ��������
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							// ���ͻ�ȡ�豸��������
							String dataResult;
							try {
								dataResult = OkHttpHelper.get(DEVIDE_DATA_URL);
								parseDeviceDataResult(dataResult);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}, 1000, 1000);// �ӳ�1�뷢�����󣬼��1��
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ������¼���
	private void parseLoginResult(String result) {
		// json����
		JSONObject jsonObject = JSONObject.parseObject(result);
		// ���ж�Status�Ƿ���0
		int status = jsonObject.getIntValue("Status");
		if (status != 0) {// ��¼ʧ��
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("��¼ʧ�ܣ����������˺��������ã�");
			alert.show();
			return;
		}
		// ��¼�ɹ��Ĵ���
		JSONObject resultObj = jsonObject.getJSONObject("ResultObj");
		OkHttpHelper.accessToken = resultObj.getString("AccessToken");
		//System.out.println("���������ǣ�" + OkHttpHelper.accessToken);
	}

		// �����豸���ݽ��
	private void parseDeviceDataResult(String result) {
		JSONObject jsonObject = JSONObject.parseObject(result);
		// ���ж�Status�Ƿ���0
		int status = jsonObject.getIntValue("Status");
		if (status != 0) {// ��ȡ�豸����ʧ��
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("��ȡ���������������ݣ������豸id�Ƿ���ȷ��");
			alert.show();
			return;
		}

		// status״̬��������Ĵ���
		JSONObject deviceData = jsonObject.getJSONArray("ResultObj").getJSONObject(0);// ���ResultObj�ĵ�һ�����ݾ��������豸����
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

		// �޸ĳ��Լ��Ĵ�����api
		String lightValue = map.get("z_light_zigbee");// zigbee���մ�������ʶ��
		String tempValue = map.get("z_temperature");// zigbee�������¶ȴ�������ʶ��
		String humiValue = map.get("z_humidity");// zigbee������ʪ�ȴ�������ʶ��
		String lamp1 = map.get("m_lamp_4150");//�̵����ƹ�1
		String lamp2 = map.get("m_lamp2_4150");//�̵����ƹ�2
		String lamp3 = map.get("z_light_shuru");//zigbee�ƹ�
		//String alarm = map.get("m_alarmlight");//����
		
		// ��������������ʾ�ڽ�����
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
					
				final String LAMPWORD = "�ֵ�";
					
				LightsLabel.setText(lightValue + "lx");
				TempLabel.setText(tempValue + "��C");
				HumiLabel.setText(humiValue + "%RH");
				if(lamp1 == "true") {
					Lamp1.setText(LAMPWORD + "�ѿ�");
				} else {
					Lamp1.setText(LAMPWORD + "����");
				}
				if(lamp2 == "true") {
					Lamp2.setText(LAMPWORD + "�ѿ�");
				} else {
					Lamp2.setText(LAMPWORD + "����");
				}
				if(lamp3 == "true") {
					Lamp3.setText(LAMPWORD + "�ѿ�");
				} else {
					Lamp3.setText(LAMPWORD + "����");
				}
					
				//Ĭ��
				warnImage.setImage(new Image("file:images/yesf.png"));
				//����ֻҪ��һյ�ƹ¶��������߹¶����𣬾ͱ���������
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
		String HaohanyhNo1 = "һ��Zigbee";
	    String text=zigbeelightbutton123.getText();
	    if(text.equals(HaohanyhNo1 + "�ƿ�")) {
	    	zigbeelightbutton123.setText(HaohanyhNo1 + "�ƹ�");
	    	zigbeeLightOpen = false;
	    } else {
	    	zigbeelightbutton123.setText(HaohanyhNo1 + "�ƿ�");
	    	zigbeeLightOpen = true;
	    }
	    new zigbeelightControlThread().start();
	}
	

    @FXML
    void linelightbuttonone(ActionEvent event) {
    	String HaohanyhNo1 = "һ�����߽�";
	    String text=linelightbutton234.getText();
	    if(text.equals(HaohanyhNo1 + "�ƿ�")) {
	    	linelightbutton234.setText(HaohanyhNo1 + "�ƹ�");
	    	lineLight1Open = false;
	    } else {
	    	linelightbutton234.setText(HaohanyhNo1 + "�ƿ�");
	    	lineLight1Open = true;
	    }
	    new linelightControlThread().start();
    }

    @FXML
    void linelightbuttontwo(ActionEvent event) {
    	String HaohanyhNo1 = "�������߽�";
	    String text=linelightbutton345.getText();
	    if(text.equals(HaohanyhNo1 + "�ƿ�")) {
	    	linelightbutton345.setText(HaohanyhNo1 + "�ƹ�");
	    	lineLight2Open = false;
	    } else {
	    	linelightbutton345.setText(HaohanyhNo1 + "�ƿ�");
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
