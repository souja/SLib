package com.souja.lib.widget.cityPicker.style.citypickerview.widget;


public abstract class XCallbackListener {

	public abstract void callback(Object... obj);

	public void call(Object... obj) {
			callback(obj);
	}

}
