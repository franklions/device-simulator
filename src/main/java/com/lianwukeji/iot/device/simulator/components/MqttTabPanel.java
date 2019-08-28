package com.lianwukeji.iot.device.simulator.components;

import com.lianwukeji.iot.device.simulator.RunApplication;
import com.lianwukeji.iot.device.simulator.service.DeviceGatewayService;
import com.lianwukeji.iot.device.simulator.service.RdsService;
import com.lianwukeji.iot.device.simulator.utils.ByteOp;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;
import java.util.Properties;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-14
 * @since Jdk 1.8
 */
public class MqttTabPanel extends JPanel implements ActionListener, MqttCallback, Runnable {

    private static final String DEVICE_SUB_UNSAFE = "device-sub-unsafe";

    private static final Logger logger = LoggerFactory.getLogger(MqttTabPanel.class);
    protected static final Insets TEXT_MARGINS = new Insets(3, 3, 3, 3);
    private Properties props;
    private JFrame owner = null;
    private RunApplication parent;
    private RdsService rdsService;
    private DeviceGatewayService deviceService;

    private JTextField txtIpAddress;
    private JTextField txtDeviceId;
    private JTextField txtDataType;
    private JTextField txtSid;
    private JTextArea pubData;
    private JTextArea subData;
    private JButton connect;
    private JButton disconnect;
    private JButton history;
    private JButton btnPostData;
    private JButton btnPostData2;
    private LED led;
    private MQTTHist historyComp = null;
    private MqttClient mqtt = null;
    private MqttConnectOptions opts = null;
    private boolean connected = false;
    private Object connLostWait = new Object();

    public MqttTabPanel(RunApplication theParent, JFrame theOwner, DeviceGatewayService device, Properties props, RdsService rds) {
        this.parent = theParent;
        this.owner = theOwner;
        this.deviceService = device;
        this.props = props;
        this.rdsService = rds;

        JPanel mqttConnPanel = new JPanel();

        mqttConnPanel.setLayout(new GridLayout(3, 1));
        mqttConnPanel.setPreferredSize(new Dimension(776, 100));
        mqttConnPanel.setBorder(new EtchedBorder());

        connect = new JButton("Connect");
        disconnect = new JButton("Disconnect");
        disconnect.setEnabled(false);
        history = new JButton("History");

        connect.addActionListener(this);
        disconnect.addActionListener(this);
        history.addActionListener(this);

        txtIpAddress = new JTextField();
        txtIpAddress.setPreferredSize(new Dimension(300, 20));
        txtIpAddress.setEditable(true);
        String mqtt = rdsService.getAddrs(DEVICE_SUB_UNSAFE);
        txtIpAddress.setText(mqtt);

        JPanel mqttAddr = new JPanel();
        mqttAddr.setLayout( new FlowLayout(FlowLayout.CENTER));
        mqttAddr.add(new JLabel("MQTT TCP/IP address: ",SwingConstants.LEFT));
        mqttAddr.add(txtIpAddress);

        txtDeviceId = new JTextField();
        txtDeviceId.setPreferredSize(new Dimension(300, 20));
        txtDeviceId.setEditable(false);
        txtDeviceId.setBackground(Color.lightGray);
        String deviceid = props.getProperty("DeviceID", "");
        txtDeviceId.setText(deviceid);

        JPanel devicePanel = new JPanel();
        devicePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        devicePanel.add(new JLabel("Device ID: ",SwingConstants.LEFT));
        devicePanel.add(txtDeviceId);

        led = new LED();
        led.setRed();
        new Thread(led).start();

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(new JSeparator(SwingConstants.VERTICAL));
        buttons.add(led);
        buttons.add(connect);
        buttons.add(new JLabel("  "));
        buttons.add(disconnect);
        buttons.add(new JSeparator(SwingConstants.VERTICAL));
        buttons.add(history);
        buttons.add(new JSeparator(SwingConstants.VERTICAL));

        mqttConnPanel.add(mqttAddr);
        mqttConnPanel.add(devicePanel);
        mqttConnPanel.add(buttons);


        JPanel pubPanel = new JPanel();
        pubData = new JTextArea(5, 50);
        pubData.setBorder(new BevelBorder(BevelBorder.LOWERED));
        pubData.setMargin(TEXT_MARGINS);

        JPanel pubLabelAndType = new JPanel();
        pubLabelAndType.setLayout( new GridLayout(2,1));
        JLabel pubLabel = new JLabel(" Post Data: ");
        Font f = pubLabel.getFont();
        pubLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 1));

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        txtDataType = new JTextField();
        txtDataType.setPreferredSize(new Dimension(100, 20));
        typePanel.add(new JLabel("Data Type:"));
        typePanel.add(txtDataType);

        txtSid = new JTextField();
        txtSid.setPreferredSize(new Dimension(150, 20));
        txtSid.setText(String.valueOf(System.currentTimeMillis() * 1000));
        typePanel.add(new JLabel("SID: "));
        typePanel.add(txtSid);

        pubLabelAndType.add(pubLabel);
        pubLabelAndType.add(typePanel);

        btnPostData = new JButton("PostData");
        btnPostData.setPreferredSize(new Dimension(95,20));
        btnPostData.setEnabled(false);
        btnPostData.addActionListener(this);

        btnPostData2 = new JButton("PostData2");
        btnPostData2.setPreferredSize(new Dimension(95,20));
        btnPostData2.setEnabled(false);
        btnPostData2.addActionListener(this);

        JPanel buttonLayout = new JPanel();
        buttonLayout.setPreferredSize(new Dimension(110, 20));
        buttonLayout.setBorder(new EtchedBorder());
        buttonLayout.add(btnPostData);
        buttonLayout.add(btnPostData2);

        pubPanel.setLayout(new BorderLayout());
        pubPanel.add(pubLabelAndType, BorderLayout.NORTH);
        pubPanel.add(new JScrollPane(pubData), BorderLayout.CENTER);
        pubPanel.add(buttonLayout, BorderLayout.EAST);

        subData = new JTextArea(15, 50);
        subData.setBorder(new BevelBorder(BevelBorder.LOWERED));
        subData.setEditable(false);
        subData.setBackground(Color.lightGray);
        subData.setMargin(TEXT_MARGINS);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout());

        JLabel subLabel = new JLabel("Receive Message: ");
        subLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 1));

        subPanel.add(subLabel, BorderLayout.NORTH);
        subPanel.add(new JScrollPane(subData), BorderLayout.CENTER);

        JSplitPane pubsub = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                pubPanel, subPanel);
        pubsub.setOneTouchExpandable(true);
        pubsub.setDividerSize(10);
        pubsub.setResizeWeight(0.5);

        historyComp = new MQTTHist(this.owner);

        this.setLayout(new BorderLayout());
        add(mqttConnPanel, BorderLayout.NORTH);
        add(pubsub, BorderLayout.CENTER);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Connect")) {

            connect.setEnabled(false);
            led.setAmber();
            connected = false;
            new Thread(this).start();
        } else if (e.getActionCommand().equals("Disconnect")) {
            if (connected) {
                // Disconnect from the broker
                disconnect();
            } else {
                writeLogln("MQTT client not connected !");
            }
        } else if (e.getActionCommand().equals("History")) {
            historyComp.enableHistory();
        } else if (e.getActionCommand().equals("PostData")) {
            postData();
        } else if (e.getActionCommand().equals("PostData2")) {
            postData2();
        }
    }

    /**
     * AP上报data2数据  数据内容应该为data2的base64的字符串数据
     */
    private void postData2() {
        String data = pubData.getText();
        if(data== null || data.isEmpty()){
            JOptionPane.showMessageDialog(this.owner,"Please input post data！","Info ",1);
            return;
        }
        synchronized (this) {
            String result = deviceService.postData2(data);
            writeLogln(" Post Data2 Result:");
            writeLogln("   --> " + result);
        }
    }

    private void postData() {
        String data = pubData.getText();

        if(data== null || data.isEmpty()){
            JOptionPane.showMessageDialog(this.owner,"Please input post data！","Info ",1);
            return;
        }

        String did = txtDeviceId.getText();
        String datatype = txtDataType.getText();
        String sid = txtDataType.getText();

        if(sid == null || sid.isEmpty()){
            sid = String.valueOf(System.currentTimeMillis() * 1000);
        }
        synchronized (this) {
            String result = deviceService.postData(did, data, datatype, sid);
            writeLogln(" Post Data Result:");
            writeLogln("   --> " + result);
        }
    }

    @Override
    public void run() {
        // Connect to the broker
        String ipAddr = txtIpAddress.getText();
        String deviceId = txtDeviceId.getText();

        String token = null;
        try {
            token = deviceService.getToken();
        } catch (Exception e) {
            logger.error("MQTT连接时获取token异常：",e);
            return;
        }
        String connStr = "";
        try {

            if (ipAddr.indexOf("://") < 0) {
                connStr = "tcp://" + ipAddr;
            } else {
                connStr = ipAddr;
            }

            connect(connStr, deviceId, token);

            connected = true;
            led.setGreen();
            setConnected(true);
            this.parent.optionPanel.setConnected(true);
            subscription(deviceId, 1, true);

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this.owner, "Invalid port number !", "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE);
        } catch (MqttException mqe) {
            Throwable e = mqe.getCause();
            String msg = "";
            if (e == null) {
                e = mqe;
            } else if (mqe.getMessage() != null) {
                msg += mqe.getMessage() + "\n";
            }
            msg += e;
            JOptionPane.showMessageDialog(this.owner, msg, "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE);
            logger.error("MQTT连接异常：",e);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this.owner, ex, "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE);
            logger.error("MQTT连接未知的异常：",ex);
        }

        if (!connected) {
            led.setRed();
            setConnected(false);
            this.parent.optionPanel.setConnected(false);
        }

        synchronized (this) {
            if (connected) {
                writeLogln("WebSphere MQ Telemetry transport connected to " + mqtt.getServerURI());
            } else {
                writeLogln("ERROR:WebSphere MQ Telemetry transport failed to connect to " + connStr);
            }
        }
    }

    private void connect(String connStr, String deviceId, String token) throws MqttException {
        if ((mqtt != null) &&
                (!connStr.equals(mqtt.getServerURI()) /*||
		      (usePersistence != (mqtt.getPersistence() != null) )*/)) {
            //mqtt.terminate();
            mqtt = null;
        }
        if (mqtt == null) {
            String clientId = "ABCDEF1234567890" + deviceId;
            mqtt = new MqttClient(connStr, clientId, null);
            mqtt.setCallback(this);
        }

        opts = new MqttConnectOptions();
        opts.setCleanSession(true);
        opts.setKeepAliveInterval(60);
        opts.setUserName(deviceId);
        opts.setPassword(token.toCharArray());

        mqtt.connect(opts);
    }

    public void subscription(String topic, int qos, boolean sub) {

        if (connected) {
            try {
                String[] theseTopics = new String[1];
                int[] theseQoS = new int[1];

                theseTopics[0] = topic;
                theseQoS[0] = qos;

                synchronized (this) {
                    if (sub) {
                        writeLogln("  --> SUBSCRIBE,        TOPIC:" + topic + ", Requested QoS:" + qos);
                    } else {
                        writeLogln("  --> UNSUBSCRIBE,      TOPIC:" + topic);
                    }
                }

                if (sub) {
                    mqtt.subscribe(theseTopics, theseQoS);
                } else {
                    mqtt.unsubscribe(theseTopics);
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this.owner, ex.getMessage(), "MQTT Subscription Exception", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            writeLogln("MQTT client not connected !");
        }
    }

    public void disconnect() {
        connected = false;

        // Notify connectionLost to give up. It may be running..
        synchronized (connLostWait) {
            connLostWait.notify();
        }

        // Disconnect from the broker
        if (mqtt != null) {
            try {
                mqtt.disconnect();
            } catch (Exception ex) {
                writeLogln("MQTT disconnect error !");
                logger.error("MQTT断开连接时异常：",ex);
                System.exit(1);
            }
        }

        // Set the LED state correctly
        // If the led is flashing then turn it off
        // This only occurs if disconnect is hit during connection lost
        if (led.isFlashing()) {
            led.setFlash();
        }
        led.setRed();

        setConnected(false);
        this.parent.optionPanel.setConnected(false);
        synchronized (this) {
            writeLogln("WebSphere MQ Telemetry transport disconnected");
        }
    }

    private void setConnected(boolean b) {
        btnPostData.setEnabled(b);
        btnPostData2.setEnabled(b);
        disconnect.setEnabled(b);
        connect.setEnabled(!b);
    }

    /**
     * Write to the history dialog window and append a newline character after the text
     *
     * @param logdata The line of text to display in the history log
     */
    public void writeLogln(String logdata) {
        writeLog(logdata + System.getProperty("line.separator"));
    }

    /**
     * Write to the history dialog window
     *
     * @param logdata The line of text to display in the history log
     */
    public void writeLog(String logdata) {
        if (historyComp != null) {
            try {
                historyComp.write(logdata);
            } catch (Exception e) {
                logger.error("写入厉害消息日志时异常：",e);
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        int rc = -1;

        if(!props.getProperty("Reconnect","falase").equals("true")){
            connected=false;
            led.setRed();
            setConnected(false);
            this.parent.optionPanel.setConnected(false);
            return;
        }

        // Flip the LED to Amber and set it flashing
        led.setAmber();
        led.setFlash();

        writeLogln("Connection Lost!....Reconnecting");
        synchronized (this) {
            writeLogln("MQTT Connection Lost!....Reconnecting to " + mqtt.getServerURI());
        }

        try {
            // While we have failed to reconnect and disconnect hasn't
            // been called by another thread retry to connect
            while ((rc == -1) && connected) {

                try {
                    synchronized (connLostWait) {
                        connLostWait.wait(10000);
                    }
                } catch (InterruptedException iex) {
                    // Don't care if we are interrupted
                }

                synchronized (this) { // Grab the log synchronisation lock
                    if (connected) {
                        writeLog("MQTT reconnecting......");
                        try {

                            String ipAddr = txtIpAddress.getText();
                            String deviceId = txtDeviceId.getText();
                            String token = null;
                            try {
                                token = deviceService.getToken();
                            } catch (Exception e) {
                                logger.error("MQTT重连时获取token异常：",e);
                                return;
                            }
                            String connStr = "";


                            if (ipAddr.indexOf("://") < 0) {
                                connStr = "tcp://" + ipAddr;
                            } else {
                                connStr = ipAddr;
                            }

                            connect(connStr, deviceId, token);

                            rc = 0;
                        } catch (MqttException mqte) {
                            // Catch any MQTT exceptions, set rc to -1 and retry
                            rc = -1;
                            logger.error("MQTT连接异常：",mqte);
                        }
                        if (rc == -1) {
                            writeLogln("failed");
                        } else {
                            writeLogln("success !");
                        }
                    }
                }
            }
            // Remove title text once we have reconnected

        } catch (Exception ex) {
            writeLogln("MQTT connection broken !");
            logger.error("MQTT连接异常：",ex);
            disconnect();
            //throw ex;
        } finally {
            // Set the flashing off whatever happens
            if (led.isFlashing()) {
                led.setFlash(); // Flash off
            }
        }

        // If we get here and we are connected then set the led to green
        if (connected) {
            led.setGreen();
            setConnected(true);
            this.parent.optionPanel.setConnected(true);
            subscription(txtDeviceId.getText(), 1, true);
        } else {
            led.setRed();
            setConnected(false);
            this.parent.optionPanel.setConnected(false);
        }

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        logger.info("  --> PUBLISH received, TOPIC:" + topic + ", QoS:" + message.getQos() + ", Retained:" + message.isRetained());
        logger.info(" --> Payload:" + new String(message.getPayload()));
        try {
            synchronized (this) {
                writeLogln("  --> PUBLISH received, TOPIC:" + topic + ", QoS:" + message.getQos() + ", Retained:" + message.isRetained());
                writeLog("                        DATA:");
                writeLogln(new String(message.getPayload()));
            }

            String splitStr = ",";
            StringBuilder data = new StringBuilder();
            String payload = new String(message.getPayload());
            if (payload.startsWith("BB01")) {
                String bDataStr = payload.substring(8);
                byte[] databuf = Base64.getDecoder().decode(bDataStr);
                ByteOp bdataOp = ByteOp.fromByteArray(databuf);

                data.append(bdataOp.getIntFrom1Byte()).append(splitStr);
                data.append(bdataOp.getIntFrom2Bytes(true)).append(splitStr);
                data.append(bdataOp.getString(16)).append(splitStr);
                data.append(bdataOp.getIntFrom1Byte()).append(splitStr);
                data.append(bdataOp.getIntFrom2Bytes(true)).append(splitStr);
                data.append(bdataOp.getIntFrom1Byte()).append(splitStr);

                data.append(bdataOp.getIntFrom2Bytes(true)).append(splitStr);
                data.append(bdataOp.getIntFrom1Byte()).append(splitStr);
                int dataLength = bdataOp.getIntFrom2Bytes(true);
                data.append(dataLength).append(splitStr);
                if (bdataOp.remain() < dataLength) {
                    data.append("非法的OPacket数据, payload部不足" + dataLength + "个字节");
                } else {
                    ByteOp subByteOp = bdataOp.slice(dataLength);
                    data.append(new String(subByteOp.getBytes()));
                }

            } else if (payload.startsWith("BA01")) {
                //不支持加密数据
            } else if (payload.startsWith("JS01")) {
                data.append(payload);
            } else if (payload.startsWith("JA01")) {
                //暂不支持加密数据
            } else {
                data.append(payload);
            }

            subData.append(data.toString() + System.getProperty("line.separator"));

        } catch (Exception ex) {
            logger.error("MQTT接收消息处理异常：",ex);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
