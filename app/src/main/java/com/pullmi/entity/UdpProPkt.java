package com.pullmi.entity;

public class UdpProPkt {
	// GlobalDataPacket
	public static enum PKT_TYPE {
		PT_TCP(0), PT_U485(1), PT_CAN_US(2), PT_UDP(3);

		private int value;

		PKT_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	public static enum IS_ACK {
		NOT_ACK(0), IS_ACK(1), NOW_ACK(2);

		private int value;

		IS_ACK(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// UDPPROPKT
	public static enum E_UDP_RPO_DAT {
		e_udpPro_getRcuInfo(0), e_udpPro_setRcuInfo(1), e_udpPro_handShake(2), e_udpPro_getDevsInfo(
				3), e_udpPro_ctrlDev(4), e_udpPro_addDev(5), e_udpPro_editDev(6), e_udpPro_delDev(7), e_udpPro_getBoards(
				8), e_udpPro_editBoards(9), e_udpPro_delBoards(10), e_udpPro_getKeyOpItems(11), e_udpPro_setKeyOpItems(
				12), e_udpPro_delKeyOpItems(13), e_udpPro_getChnOpItems(14), e_udpPro_setChnOpItems(
				15), e_udpPro_delChnOpItems(16), e_udpPro_getTimerEvents(17), e_udpPro_addTimerEvents(
				18), e_udpPro_editTimerEvents(19), e_udpPro_delTimerEvents(20), e_udpPro_exeTimerEvents(
				21), e_udpPro_getSceneEvents(22), e_udpPro_addSceneEvents(23), e_udpPro_editSceneEvents(
				24), e_udpPro_delSceneEvents(25), e_udpPro_exeSceneEvents(26), e_udpPro_getEnvEvents(
				27), e_udpPro_addEnvEvents(28), e_udpPro_editEnvEvents(29), e_udpPro_delEnvEvents(
				30), e_udpPro_exeEnvEvents(31), e_udpPro_security_info(32), e_udpPro_getRcuInfoNoPwd(
				33), e_udpPro_pwd_error(34), e_udpPro_chns_status(35), e_udpPro_keyInput_info(36), e_udpPro_getIOSet_input(
				37), e_udpPro_getIOSet_output(38), e_udpPro_saveIOSet_input(39), e_udpPro_saveIOSet_output(
				40), e_udpPro_studyIR_cmd(41), e_udpPro_studyIR_cmd_ret(42), e_udpPro_report_output(
				43), e_udpPro_report_cardInput(44), e_udpPro_irDev_exeRet(45), e_udpPro_quick_setDevKey(
				46), e_udpPro_quick_delDevKey(47), e_udpPro_get_modulePos(48),e_udpPro_soft_iap_data(49),
				e_udpPro_get_soft_version(50),e_udpPro_bc_key_ctrl(51),e_udpPro_ctrl_allDevs(52),e_udpPro_hotel_signal(53),
				e_udpPro_confirm_signal(54);

		private int value;

		E_UDP_RPO_DAT(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// 设备类型
	public static enum E_WARE_TYPE {
		e_ware_airCond(0), e_ware_tv(1), e_ware_tvUP(2), e_ware_light(3), e_ware_curtain(4), e_ware_lock(
				5), e_ware_valve(6), e_ware_fresh_air(7), e_ware_multiChn(8), e_ware_other(9);

		private int value;

		E_WARE_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	// 设备控制类型
	public static enum E_DEV_TYPE {
		e_dev_IR(0), e_dev_315M(1), e_dev_Chn(2);

		private int value;

		E_DEV_TYPE(int value) {
			// TODO Auto-generated constructor stub
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_AIR_MODE {
		e_air_auto(0), e_air_hot(1), e_air_cool(2), e_air_dry(3), e_air_wind(4), e_air_mode_total(5);

		private int value;

		E_AIR_MODE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_AIR_CMD {
		e_air_pwrOn(0), e_air_pwrOff(1), e_air_spdLow(2), e_air_spdMid(3), e_air_spdHigh(4), e_air_spdAuto(
				5), e_air_drctUpDn1(6), // 上下摇摆
		e_air_drctUpDn2(7), e_air_drctUpDn3(8), e_air_drctUpDnAuto(9), e_air_drctLfRt1(10), // 左右摇摆
		e_air_drctLfRt2(11), e_air_drctLfRt3(12), e_air_drctLfRtAuto(13), e_air_temp14(14), e_air_temp15(
				15), e_air_temp16(16), e_air_temp17(17), e_air_temp18(18), e_air_temp19(19), e_air_temp20(
				20), e_air_temp21(21), e_air_temp22(22), e_air_temp23(23), e_air_temp24(24), e_air_temp25(
				25), e_air_temp26(26), e_air_temp27(27), e_air_temp28(28), e_air_temp29(29), e_air_temp30(
				30), e_air_cmd_total(31);

		private int value;

		E_AIR_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_TV_CMD {
		e_tv_offOn(0), e_tv_mute(1), e_tv_numTvAv(2), e_tv_num1(3), e_tv_num2(4), e_tv_num3(5), e_tv_num4(
				6), e_tv_num5(7), e_tv_num6(8), e_tv_num7(9), e_tv_num8(10), e_tv_num9(11), e_tv_numMenu(
				12), e_tv_numUp(13), e_tv_num0(14), e_tv_numLf(15), e_tv_enter(16), e_tv_numRt(17), e_tv_numRet(
				18), e_tv_numDn(19), e_tv_numLookBack(20), e_tv_userDef1(21), e_tv_userDef2(22), e_tv_userDef3(
				23), e_tv_cmd_total(24);

		private int value;

		E_TV_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_TVUP_CMD {
		e_tvUP_offOn(0), e_tvUP_mute(1), e_tvUP_numPg(2), e_tvUP_num1(3), e_tvUP_num2(4), e_tvUP_num3(
				5), e_tvUP_num4(6), e_tvUP_num5(7), e_tvUP_num6(8), e_tvUP_num7(9), e_tvUP_num8(10), e_tvUP_num9(
				11), e_tvUP_numDemand(12), e_tvUP_numUp(13), e_tvUP_num0(14), e_tvUP_numLf(15), e_tvUP_enter(
				16), e_tvUP_numRt(17), e_tvUP_numInteract(18), e_tvUP_numDn(19), e_tvUP_numBack(20), e_tvUP_numVInc(
				21), e_tvUP_numInfo(22), e_tvUP_numPInc(23), e_tvUP_numVDec(24), e_tvUP_numLive(25), e_tvUP_numPDec(
				26), e_tvUP_userDef1(26), e_tvUP_userDef2(27), e_tvUP_userDef3(28), e_tvUP_cmd_total(
				29);

		private int value;

		E_TVUP_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_LGT_CMD {
		e_lgt_offOn(0), e_lgt_onOff(1), e_lgt_dark(2), e_lgt_bright(3), e_lgt_cmd_total(4);

		private int value;

		E_LGT_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_CURT_CMD {
		e_curt_offOn(0), e_curt_offOff(1), e_curt_stop(2), e_curt_cmd_total(3);

		private int value;

		E_CURT_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// 阀门设备
	public static enum E_VALVE_CMD {
		e_valve_offOn(0), e_valve_onOff(1), e_valve_stop(2), e_valve_cmd_total(3);

		private int value;

		E_VALVE_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// 门锁设备
	public static enum E_LOCK_CMD {
		e_lock_open(0), e_lock_close(1), e_lock_stop(2),e_lock_lockOut(3), e_lock_cmd_total(4);

		private int value;

		E_LOCK_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_FRESHAIR_CMD {
		e_freshair_open(0), e_freshair_spd_low(1), e_freshair_spd_mid(2), e_freshair_spd_high(3), e_freshair_close(
				4), e_freshair_cmd_total(5);

		private int value;

		E_FRESHAIR_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// 多通道控制命令
	public static enum E_MULTICHN_CMD {
		e_multiChn_offOn(0), e_multiChn_onOff(1), e_multiChn_cmd_total(2);

		private int value;

		E_MULTICHN_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_86KEY_AIR_CMD {
		e_86key_air_power(0), e_86key_air_mode(1), e_86key_air_spd(2), e_86key_air_tempInc(3), e_86key_air_tempDec(
				4);

		private int value;

		E_86KEY_AIR_CMD(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum E_86KEY_MUTEX_TYPE {
		e_86keyMutex_null(0), e_86keyMutex_on(1), e_86keyMutex_off(2), e_86keyMutex_stop(3), e_86keyMutex_loop(
				4);

		private int value;

		E_86KEY_MUTEX_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum E_86KEY_CTRL_TYPE {
		e_86keyCtrl_null(0), e_86keyCtrl_offOn(1), e_86keyCtrl_onOff(2), e_86keyCtrl_power(3), e_86keyCtrl_dark(
				4), e_86keyCtrl_bright(5), e_86keyCtrl_cmd_total(6);

		private int value;

		E_86KEY_CTRL_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public int isAck;
	public byte[] uidDst;
	public byte[] pwdDst;
	public byte[] uidSrc;
	public byte[] dstIp;
	public byte[] srcIp;
	public int snPkt;
	public int sumPkt;
	public int currPkt;
	public int datType;
	public int subType1;
	public int subType2;
	public int dataLen;

	public int rev;
	public byte[] dat;

	public UdpProPkt() {
	}

	public UdpProPkt(byte[] srcIp, byte[] dstIp, byte[] uidDst, byte[] pwdDst, byte[] uidSrc,
			int snPkt, int sumPkt, int currPkt, int isAck, int datType, int subType1, int subType2,
			int dataLen, int rev) {

		this.uidDst = uidDst;
		this.pwdDst = pwdDst;
		this.uidSrc = uidSrc;
		this.srcIp = srcIp;
		this.dstIp = dstIp;
		this.snPkt = snPkt;
		this.sumPkt = sumPkt;
		this.currPkt = currPkt;
		this.isAck = isAck;
		this.datType = datType;
		this.subType1 = subType1;
		this.subType2 = subType2;
		this.rev = rev;
		this.dataLen = dataLen;

	}

	public byte[] getData() {
		byte[] data = new byte[56 + dat.length];

		byte[] header = "head".getBytes();
		for (int i = 0; i < header.length; i++) {
			data[i] = header[i];
		}

		// src addr
		data[4] = this.srcIp[0];
		data[5] = this.srcIp[1];
		data[6] = this.srcIp[2];
		data[7] = this.srcIp[3];

		// dst addr
		data[8] = this.dstIp[0];
		data[9] = this.dstIp[1];
		data[10] = this.dstIp[2];
		data[11] = this.dstIp[3];

		// udiDst
		for (int i = 0; i < 12; i++) {
			data[i + 12] = this.uidDst[i];
		}
		// pwdDst
		for (int i = 0; i < 8; i++) {
			data[i + 12 + 12] = this.pwdDst[i];
		}

		// uidSrc
		for (int i = 0; i < 12; i++) {
			data[i + 12 + 12 + 8] = this.uidSrc[i];
		}

		// snPkt
		data[44] = (byte) snPkt;
		data[45] = 0;

		// sumPkt
		data[46] = (byte) (this.sumPkt & 0xff);

		// currPkt
		data[47] = (byte) (this.currPkt & 0xff);

		// isAck
		data[48] = (byte) (this.isAck & 0xff);

		// datType
		data[49] = (byte) (this.datType & 0xff);

		// subType1
		data[50] = (byte) (this.subType1 & 0xff);

		// subType2
		data[51] = (byte) (this.subType2 & 0xff);

		// dataLen
		data[52] = (byte) dataLen;
		data[53] = 0x0;

		// rev
		data[54] = 0x0;
		data[55] = 0x0;

		for (int i = 0; i < this.dataLen; i++) {
			data[56 + i] = dat[i];
		}

		return data;
	}
}
