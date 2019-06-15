package br.uff.sbeqpid;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class EmbeddedQpidBrokerPropertiesTest {

    @Test
    public void when_try_convert_to_map_properties_then_ok(){
        EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties = new EmbeddedQpidBrokerProperties();
        embeddedQpidBrokerProperties.getAuth().setUsername("qqqq");
        embeddedQpidBrokerProperties.getAuth().setPassword("tttt");

        Map<String, String> map = embeddedQpidBrokerProperties.toMap();

        assertThat(map.get("qpid.port")).isEqualTo("5672");
        assertThat(map.get("qpid.auth.username")).isEqualTo("qqqq");
        assertThat(map.get("qpid.auth.password")).isEqualTo("tttt");
    }

    @Test
    public void when_try_convert_to_map_properties_and_fail_then_throw_IllegalArgumentException() throws IOException {
        EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties = new EmbeddedQpidBrokerProperties();
        embeddedQpidBrokerProperties.getAuth().setUsername("qqqq");
        embeddedQpidBrokerProperties.getAuth().setPassword("tttt");

        JavaPropsMapper propsMapper = mock(JavaPropsMapper.class);
        doThrow(new IOException("exception 5335")).when(propsMapper).writeValueAsProperties(any());

        embeddedQpidBrokerProperties.setPropMapper(propsMapper);

        assertThatThrownBy(embeddedQpidBrokerProperties::toMap)
                .hasMessage("Cannot serialize " + EmbeddedQpidBrokerProperties.class.getSimpleName())
                .isInstanceOf(IllegalStateException.class)
                .hasCauseExactlyInstanceOf(IOException.class);

    }
}