package net.gnfe.util.rest.jackson;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.gnfe.util.DummyUtils;

public class JsonDateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator generator, SerializerProvider serializers) throws IOException, JsonProcessingException {
		if (value == null) {
			generator.writeNull();
		} else {
			generator.writeString(DummyUtils.formatDateTime2(value));
		}
	}
}