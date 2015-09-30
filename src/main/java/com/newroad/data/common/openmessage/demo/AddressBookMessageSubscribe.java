package com.newroad.data.common.openmessage.demo;

import com.newroad.data.common.openmessage.utils.ConfigLoader;
import com.newroad.data.common.openmessage.config.AppConfig;
import com.newroad.data.common.openmessage.lib.ADDRESSBOOKMessage;

public class AddressBookMessageSubscribe {

	public static void main(String[] args) {

		AppConfig config = ConfigLoader.load(ConfigLoader.ConfigType.Message);
		ADDRESSBOOKMessage addressbook = new ADDRESSBOOKMessage(config);
		//addressbook.setAddress("186****7150");
		addressbook.subscribe();
	}	
}
