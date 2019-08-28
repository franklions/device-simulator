package com.lianwukeji.iot.device.simulator.components;

import com.lianwukeji.iot.device.simulator.entity.DeviceInfoEntity;
import com.lianwukeji.iot.device.simulator.entity.odata.*;
import com.lianwukeji.iot.device.simulator.service.DeviceGatewayService;
import com.lianwukeji.iot.device.simulator.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author flsh
 * @version 1.0
 * @date 2019-08-16
 * @since Jdk 1.8
 */
public class OptionTabPanel extends JPanel implements ActionListener, ListSelectionListener, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(OptionTabPanel.class);

    protected static final Insets TEXT_MARGINS = new Insets(3, 3, 3, 3);

    private static final ConcurrentHashMap<String, DeviceInfoEntity> onlineList = new ConcurrentHashMap<>();

    private Properties props;
    private JFrame owner = null;
    private DeviceGatewayService deviceService;

    private JComboBox cbxDevice;
    private JButton btnOnline;
    private JButton btnOffline;
    private JButton btnPostData;
    private JButton btnPostData2;
    private JTextArea postData;
    private JTextArea logData;
    private JList<String> deviceList;
    private JTextField txtDataType;
    private JTextField txtSid;

    public OptionTabPanel(JFrame theOwner, DeviceGatewayService device, Properties props) {
        this.owner = theOwner;
        this.deviceService = device;
        this.props = props;

        init();

        if (this.deviceService.getGroupList() != null
                && this.deviceService.getGroupList().getDevices() != null) {
            this.deviceService.getGroupList().getDevices().stream().forEach(d -> {
                cbxDevice.addItem(d.getDid() + "[" + d.getType() + "]");
            });

        }
    }

    private void init() {
        this.setLayout(new BorderLayout());

        cbxDevice = new JComboBox();
        cbxDevice.setPreferredSize(new Dimension(300, 25));
        cbxDevice.setEditable(false);

        JPanel devicePanel = new JPanel();
        devicePanel.add(new JLabel("Select Device: "));
        devicePanel.add(cbxDevice);

        JPanel buttons = new JPanel();
        buttons.setPreferredSize(new Dimension(110, 30));

        btnOnline = new JButton("Online");
        btnOnline.setPreferredSize(new Dimension(95, 20));
        btnOnline.setEnabled(false);
        btnOnline.addActionListener(this);

        btnOffline = new JButton("Offline");
        btnOffline.setPreferredSize(new Dimension(95, 20));
        btnOffline.setEnabled(false);
        btnOffline.addActionListener(this);

        btnPostData = new JButton("PostData");
        btnPostData.setPreferredSize(new Dimension(95, 20));
        btnPostData.setEnabled(false);
        btnPostData.addActionListener(this);

        btnPostData2 = new JButton("PostData2");
        btnPostData2.setPreferredSize(new Dimension(95, 20));
        btnPostData2.setEnabled(false);
        btnPostData2.addActionListener(this);

        buttons.add(btnOnline);
        buttons.add(btnOffline);
        buttons.add(btnPostData);
        buttons.add(btnPostData2);

        JPanel postDataPanel = new JPanel();
        postDataPanel.setLayout(new BorderLayout());
        postDataPanel.setBorder(new EtchedBorder());


        JPanel typePanel = new JPanel();
        txtDataType = new JTextField();
        txtDataType.setPreferredSize(new Dimension(100, 20));
        typePanel.add(new JLabel("Data Type:/Device Type Code:"));
        typePanel.add(txtDataType);

        txtSid = new JTextField();
        txtSid.setPreferredSize(new Dimension(150, 20));
        txtSid.setText(String.valueOf(System.currentTimeMillis() * 1000));
        typePanel.add(new JLabel("SID: "));
        typePanel.add(txtSid);


        postData = new JTextArea(5, 50);
        postData.setBorder(new BevelBorder(BevelBorder.LOWERED));
        postDataPanel.add(typePanel, BorderLayout.NORTH);
        postDataPanel.add(new JScrollPane(postData), BorderLayout.CENTER);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(new EtchedBorder());
        listPanel.setPreferredSize(new Dimension(200, 30));
        JLabel lbList = new JLabel("Online List: ");
        Font f = lbList.getFont();
        lbList.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 1));

        deviceList = new JList<String>();
        deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceList.addListSelectionListener(this);
        listPanel.add(lbList, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(deviceList), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(new EtchedBorder());
        buttonPanel.add(buttons, BorderLayout.CENTER);

        JPanel pubPanel = new JPanel();
        pubPanel.setLayout(new BorderLayout());

        pubPanel.add(postDataPanel, BorderLayout.CENTER);
        pubPanel.add(buttonPanel, BorderLayout.EAST);

        JSplitPane pubSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                listPanel , pubPanel );
        pubSplitPanel.setOneTouchExpandable(true);
        pubSplitPanel.setDividerLocation(200);
        pubSplitPanel.setDividerSize(5);
        pubSplitPanel.setResizeWeight(0.5);

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.setBorder(new EtchedBorder());
        JLabel lbLog = new JLabel(" Log Data");
        lbLog.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 1));

        logData = new JTextArea(15, 50);
        logData.setBorder(new BevelBorder(BevelBorder.LOWERED));
        logData.setEditable(false);
        logData.setBackground(Color.lightGray);
        logPanel.add(lbLog, BorderLayout.NORTH);
        logPanel.add(new JScrollPane(logData), BorderLayout.CENTER);

        JSplitPane pubsub = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                pubSplitPanel, logPanel);
        pubsub.setOneTouchExpandable(true);
        pubsub.setDividerSize(10);
        pubsub.setResizeWeight(0.5);

        JPanel optPanel = new JPanel();
        optPanel.setLayout(new GridLayout(1, 1));
        optPanel.setBorder(new EtchedBorder());
        optPanel.add(devicePanel);

        add(optPanel, BorderLayout.NORTH);
        add(pubsub, BorderLayout.CENTER);
    }

    public void setConnected(boolean b) {
        this.btnPostData.setEnabled(b);
        this.btnPostData2.setEnabled(b);
        this.btnOnline.setEnabled(b);
        this.btnOffline.setEnabled(b);
        if (b) {
            CompletableFuture.runAsync(() -> {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error("将CP设备JOIN到智能网关时异常：",e);
                }

                if (this.deviceService.getGroupList() != null
                        && this.deviceService.getGroupList().getDevices() != null) {
                    this.deviceService.getGroupList().getDevices().stream().forEach(d -> {
                        deviceService.postJoin(d.getDid());
                        onlineList.put(d.getDid(), d);
                    });

                    refreshOnlineListComp();
                }
            }).exceptionally((e)->{
                logger.error("将CP设备JOIN到智能网关时异常：",e);
                return null ;
            });

        }else{
            deviceList.clearSelection();
        }
    }

    private void refreshOnlineListComp() {
        List<String> list = onlineList.values().stream()
                .map(d -> d.getDid() + "[" + d.getType() + "]").collect(Collectors.toList());

        deviceList.clearSelection();
        deviceList.setListData(list.toArray(new String[0]));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Online")) {
            onlineEventHandle();
        } else if (e.getActionCommand().equals("Offline")) {
            offlineEventHandle();
        } else if (e.getActionCommand().equals("PostData")) {
            postdataEventHandle();
        }else if (e.getActionCommand().equals("PostData2")) {
            postdata2EventHandle();
        }
    }

    private void postdata2EventHandle() {
        if(this.cbxDevice.getSelectedIndex() <0){
            JOptionPane.showMessageDialog(this.owner,"Please select device！","Info ",1);
            return ;
        }

        String did = (String) this.cbxDevice.getSelectedItem();
        did = did.replace("[AP]", "").replace("[CP]", "");
        String dataType = this.txtDataType.getText();

        if(dataType== null || dataType.isEmpty()){
            JOptionPane.showMessageDialog(this.owner,"Please input device type code！","Info ",1);
            return;
        }

        String sid = this.txtSid.getText();
        String data = this.postData.getText();

        if(data== null || data.isEmpty()){
            JOptionPane.showMessageDialog(this.owner,"Please input post data！","Info ",1);
            return;
        }

        BinaryData binaryData = new UTF8StringData(data.toString());
        OPacket oPacket = new OPacket();
        oPacket.setData(binaryData);
        //TODO 目前已存在设备数据类型 0x00F0 数据类型
        oPacket.setDataType(240);
        oPacket.setDataVersion(0);

        OData oData = new OData();
        oData.setPacketList(Arrays.asList(oPacket));
        oData.setDid(did);
        oData.setChannel(Integer.valueOf(1).byteValue());
        oData.setDeviceType(ByteUtils.getIntFrom2Bytes(ByteUtils.hexToBytes(dataType), 0, true));

        BData bData = new BData();
        bData.setOData(oData);
        bData.setVersion(0);


        String result =  deviceService.postData2(bData.toBytes().toBase64());
        this.logData.append(result + System.lineSeparator());

    }

    private void postdataEventHandle() {

        if(this.cbxDevice.getSelectedIndex() <0){
            JOptionPane.showMessageDialog(this.owner,"Please select device！","Info ",1);
            return ;
        }

        String did = (String) this.cbxDevice.getSelectedItem();
        did = did.replace("[AP]", "").replace("[CP]", "");
        String dataType = this.txtDataType.getText();
        String sid = this.txtSid.getText();
        String data = this.postData.getText();

        if(data== null || data.isEmpty()){
            JOptionPane.showMessageDialog(this.owner,"Please input post data！","Info ",1);
            return;
        }

        String result =  deviceService.postData(did,data,dataType,sid);
        this.logData.append(result + System.lineSeparator());
    }

    private void offlineEventHandle() {

        if(this.cbxDevice.getSelectedIndex() <0){
            JOptionPane.showMessageDialog(this.owner,"Please select device！","Info ",1);
            return ;
        }

        String did = ((String) this.cbxDevice.getSelectedItem()).replace("[AP]", "").replace("[CP]", "");

        String apid = props.getProperty("DeviceID", "");
        if (did.equals(apid)) {
            this.logData.append("当前AP设备<" + did + ">不能离线." + System.lineSeparator());
            return;
        }

        String result = this.deviceService.postDisjoin(did);
        if (onlineList.containsKey(did)) {
            onlineList.remove(did);
            refreshOnlineListComp();
        }

        this.logData.append(result + System.lineSeparator());
    }

    private void onlineEventHandle() {

        if(this.cbxDevice.getSelectedIndex() <0){
            JOptionPane.showMessageDialog(this.owner,"Please select device！","Info ",1);
            return ;
        }

        final String did = ((String) this.cbxDevice.getSelectedItem()).replace("[AP]", "").replace("[CP]", "");
        String apid = props.getProperty("DeviceID", "");
        if (did.equals(apid)) {
            this.logData.append("<" + did + ">设备已经在线." + System.lineSeparator());
            return;
        }

        String result = this.deviceService.postJoin(did);

        if (!onlineList.containsKey(did)) {
            Optional<DeviceInfoEntity> entity = this.deviceService.getGroupList().getDevices()
                    .stream().filter(d -> d.getDid().equals(did)).findFirst();
            if (entity.isPresent()) {
                onlineList.put(did, entity.get());
            }
            refreshOnlineListComp();
        }

        this.logData.append(result + System.lineSeparator());

    }

    @Override
    public void run() {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting() ==false){
            String did = deviceList.getSelectedValue();
            cbxDevice.setSelectedItem(did);
        }
    }
}
