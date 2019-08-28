package com.lianwukeji.iot.device.simulator;

import com.lianwukeji.iot.device.simulator.components.MqttTabPanel;
import com.lianwukeji.iot.device.simulator.components.OptionTabPanel;
import com.lianwukeji.iot.device.simulator.service.DeviceGatewayService;
import com.lianwukeji.iot.device.simulator.service.HttpClientTemplate;
import com.lianwukeji.iot.device.simulator.service.RdsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.util.Properties;


/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-14
 * @since Jdk 1.8
 */

public class RunApplication implements ActionListener,Runnable {

    private static final String DEVICE_GATEWAY = "device-gateway";
    private static final String PROP_FILE = "/Users/andon/flsh/IdeaProjects/github/franklions/device-simulator/src/main/resources/simulator.properties";

    private final static Logger logger = LoggerFactory.getLogger(RunApplication.class);

    private static RdsService rdsService;
    private static DeviceGatewayService deviceService;
    private static Properties props = new Properties();
    public JFrame frame = null;
    public MqttTabPanel mqttPanel;
    public OptionTabPanel optionPanel;

    public static void main(String[] args) {

        JFrame mainFrame = null;
        final RunApplication view = new RunApplication();
        mainFrame = view.getJFrame();
        mainFrame.setSize( 800, 600 );
        mainFrame.setLocation(120, 150);
        mainFrame.setResizable(true);
        mainFrame.setTitle("设备模拟器");
        view.init( mainFrame.getContentPane() );

        mainFrame.setVisible(true);


        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void init(Container contentPane) {
        //tab
        FileInputStream propFile = null;


        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch ( Exception ex) {
            // Don't worry if we can't set the look and feel
            logger.error(" Don't worry if we can't set the look and feel",ex );
        }

        // Does a properties file exist from which the GUI can be populated?
        try {
            propFile = new FileInputStream( PROP_FILE );
            props.load( propFile );
        } catch(Exception fe) {
            // If we can't find a properties file then don't worry
            logger.error("读取配置文件错误:",fe);
            propFile = null;
        }

        String rdsUri = props.getProperty("RdsUri","");
        String did = props.getProperty("DeviceID","");
        String privateKey = props.getProperty("PrivateKey","");

        if(rdsUri == null || rdsUri.isEmpty() || did==null || did.isEmpty() ||
            privateKey == null || privateKey.isEmpty()){
            JOptionPane.showMessageDialog(getJFrame(),"配置文件<simulator.properties>缺少必须的配置项.","Error ",0);
            System.exit(0);
        }

        try {
            rdsService = new RdsService(rdsUri, HttpClientTemplate.getInstance());
            String deviceAddr = rdsService.getAddrs(DEVICE_GATEWAY);
            deviceService = new DeviceGatewayService(deviceAddr, did, privateKey);
        }catch (Exception ex){
            logger.error("初始化设备服务异常:",ex);
            JOptionPane.showMessageDialog(getJFrame(),"配置文件<simulator.properties>配置项错误.","Error ",0);
            System.exit(0);
        }

        mqttPanel = new MqttTabPanel(this,getJFrame(),deviceService,props,rdsService);
        optionPanel = new OptionTabPanel(getJFrame(),deviceService,props);
        JTabbedPane tabbedGui = new JTabbedPane();
        tabbedGui.addTab( "MQTT", mqttPanel );
        tabbedGui.addTab( "Options", optionPanel );

        contentPane.add(tabbedGui);
    }

    private JFrame getJFrame() {
        if ( frame == null ) {
            frame = new JFrame();
        }
        return frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void run() {

    }
}
