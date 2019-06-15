package br.uff.sbeqpid;

import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.model.SystemConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class EmbeddedQpidBrokerTest {


    private EmbeddedQpidBroker embeddedQpidBroker;

    private EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties;

    private SystemLauncher systemLauncher;

    private Resource qpidResource;

    @Before
    public void init() throws IOException {
        qpidResource = mock(Resource.class);
        doReturn(getResourceUrlConfig()).when(qpidResource).getURL();
        embeddedQpidBrokerProperties = new EmbeddedQpidBrokerProperties();
        embeddedQpidBrokerProperties.getAuth().setUsername("XXXXX");
        embeddedQpidBrokerProperties.getAuth().setPassword("YYYYY");
        embeddedQpidBrokerProperties.setPort(-32454);

        systemLauncher = mock(SystemLauncher.class);

        embeddedQpidBroker = new EmbeddedQpidBroker(embeddedQpidBrokerProperties, qpidResource, systemLauncher);
    }

    private URL getResourceUrlConfig() {
        try {
            return this.getClass().getClassLoader().getResource("qpid-config.json").toURI().toURL();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao gerar url", e);
        }
    }


    @After
    public void destroy(){
        embeddedQpidBroker.shutdown();
    }

    @Test
    public void when_start_broker_then_start_systemLauncher_with_correct_properties() throws Exception {

        embeddedQpidBroker.start();

        InOrder inOrder = Mockito.inOrder(qpidResource, systemLauncher);

        ArgumentCaptor<Map<String, Object>> argCaptor = ArgumentCaptor.forClass(Map.class);

        inOrder.verify(qpidResource).getURL();
        inOrder.verify(systemLauncher).startup(argCaptor.capture());

        inOrder.verifyNoMoreInteractions();

        Map<String, Object> map = argCaptor.getValue();

        assertThat(map.get(SystemConfig.INITIAL_CONFIGURATION_LOCATION).toString()).matches(".+/.*/qpid-config.json");
        assertThat(map.get(SystemConfig.STARTUP_LOGGED_TO_SYSTEM_OUT)).isEqualTo(false);

        assertThat(map.get("context")).isInstanceOf(Map.class);

        Map<String, String> mapContext = (Map<String, String>) map.get("context");

        assertThat(mapContext.get("qpid.port")).isEqualTo("-32454");
        assertThat(mapContext.get("qpid.auth.username")).isEqualTo("XXXXX");
        assertThat(mapContext.get("qpid.auth.password")).isEqualTo("YYYYY");
    }

    @Test
    public void when_try_shutdown_a_shutdowned_broker_then_ignore_operation() throws Exception {
        this.when_start_broker_then_start_systemLauncher_with_correct_properties();

        InOrder inOrder = Mockito.inOrder(qpidResource, systemLauncher);

        this.embeddedQpidBroker.shutdown();

        //close again
        this.embeddedQpidBroker.shutdown();

        inOrder.verify(systemLauncher).shutdown();

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void quando_inicia_o_broker_mas_ele_ja_esta_iniciado_entao_mata_ele_e_inicia_de_novo() throws Exception {
        when_start_broker_then_start_systemLauncher_with_correct_properties();

        embeddedQpidBrokerProperties.getAuth().setUsername("AAAAA");
        embeddedQpidBrokerProperties.getAuth().setPassword("BBBBB");
        embeddedQpidBrokerProperties.setPort(-1);

        embeddedQpidBroker.start();

        InOrder inOrder = Mockito.inOrder(qpidResource, systemLauncher);

        ArgumentCaptor<Map<String, Object>> argCaptor = ArgumentCaptor.forClass(Map.class);

        inOrder.verify(systemLauncher, times(1)).shutdown();
        inOrder.verify(qpidResource, times(1)).getURL();
        inOrder.verify(systemLauncher, times(1)).startup(argCaptor.capture());

        inOrder.verifyNoMoreInteractions();

        Map<String, Object> map = argCaptor.getValue();

        assertThat(map.get(SystemConfig.INITIAL_CONFIGURATION_LOCATION).toString()).matches(".+/.*/qpid-config.json");
        assertThat(map.get(SystemConfig.STARTUP_LOGGED_TO_SYSTEM_OUT)).isEqualTo(false);

        assertThat(map.get("context")).isInstanceOf(Map.class);

        Map<String, String> mapContext = (Map<String, String>) map.get("context");

        assertThat(mapContext.get("qpid.port")).isEqualTo("-1");
        assertThat(mapContext.get("qpid.auth.username")).isEqualTo("AAAAA");
        assertThat(mapContext.get("qpid.auth.password")).isEqualTo("BBBBB");
    }

    @Test
    public void when_fail_to_start_systemLaucher_then_fail() throws Exception {
        doThrow(new IOException("fail 123")).when(systemLauncher).startup(any());

        assertThatThrownBy(()-> embeddedQpidBroker.start())
                .hasMessage("Cannot start systemLauncher")
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasCauseExactlyInstanceOf(IOException.class);
    }
}