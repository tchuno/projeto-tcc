package net.gnfe.util.rest.jackson;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

	public ObjectMapper() {
		SimpleModule module = new SimpleModule();
		module.addSerializer(Date.class, new JsonDateSerializer());
		module.addDeserializer(Date.class, new JsonDateDeserializer());
		setSerializationInclusion(Include.ALWAYS);
		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		registerModule(module);
	}

	@Override
	public <T> T readValue(String content, Class<T> valueType) {
		try {
			return super.readValue(content, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String writeValueAsString(Object value) {
		try {
			return super.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}