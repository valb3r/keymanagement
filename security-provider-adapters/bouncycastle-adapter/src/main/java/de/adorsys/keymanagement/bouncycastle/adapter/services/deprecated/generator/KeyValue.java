package de.adorsys.keymanagement.bouncycastle.adapter.services.deprecated.generator;

import lombok.Value;

@Value
public class KeyValue {
	private final String key;
	private final Object value;
	public KeyValue(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public boolean isNull(){
		return null == value;
	}
}
