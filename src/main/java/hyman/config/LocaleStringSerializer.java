package hyman.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.NonTypedScalarSerializerBase;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Locale;

@JacksonStdImpl
@Component
public class LocaleStringSerializer extends NonTypedScalarSerializerBase<Object> {

    /**
     * 序列化版本id
     */
    private static final long serialVersionUID = 1L;

    /**
     * 国际化资源
     */
    @Resource
    private MessageSource messageSource;

    public LocaleStringSerializer() {
        super(String.class, false);
    }
    @Override
    public boolean isEmpty(SerializerProvider prov, Object value) {
        String str = (String) value;
        return (str == null) || (str.length() == 0);
    }
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Locale locale = LocaleContextHolder.getLocale();
        gen.writeString(messageSource.getMessage((String) value, null, (String) value, locale));
    }
    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("string", true);
    }
    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException {
        visitStringFormat(visitor, typeHint);
    }
}
