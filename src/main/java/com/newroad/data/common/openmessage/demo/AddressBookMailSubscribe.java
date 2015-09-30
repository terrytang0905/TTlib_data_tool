package com.newroad.data.common.openmessage.demo;

import com.newroad.data.common.openmessage.utils.ConfigLoader;
import com.newroad.data.common.openmessage.config.AppConfig;
import com.newroad.data.common.openmessage.lib.ADDRESSBOOKMail;

public class AddressBookMailSubscribe {

	public static void main(String[] args) {

		AppConfig config = ConfigLoader.load(ConfigLoader.ConfigType.Mail);
		ADDRESSBOOKMail addressbook = new ADDRESSBOOKMail(config);
		addressbook.setAddress("leo@apple.cn", "leo");
		addressbook.subscribe();
	}	
}
