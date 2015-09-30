package com.newroad.data.common.openmessage.lib.base;

import com.newroad.data.common.openmessage.entity.DataStore;

public abstract class SenderWapper {

	protected DataStore requestData = new DataStore();

	public abstract ISender getSender();
}
