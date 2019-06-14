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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EmbeddedQpidBrokerTest {


    private EmbeddedQpidBroker embeddedQpidBroker;

    private EmbeddedQpidBrokerProperties embeddedQpidBrokerProperties;

    private SystemLauncher systemLauncher;

    private Resource qpidResource;

    @Before
    public void init() throws IOException {
        qpidResource = mock(Resource.class);
        doReturn(getResourceFileConfig()).when(qpidResource).getFile();
        embeddedQpidBrokerProperties = new EmbeddedQpidBrokerProperties();
        embeddedQpidBrokerProperties.getAuth().setUsername("XXXXX");
        embeddedQpidBrokerProperties.getAuth().setPassword("YYYYY");
        embeddedQpidBrokerProperties.setPort(-32454);

        systemLauncher = mock(SystemLauncher.class);

        embeddedQpidBroker = new EmbeddedQpidBroker(embeddedQpidBrokerProperties, qpidResource, systemLauncher);
    }

    private File getResourceFileConfig() {
        return new File(this.getClass().getClassLoader().getResource("qpid-config.json").getFile());
    }

    @After
    public void destroy(){
        embeddedQpidBroker.shutdown();
    }

    @Test
    public void quando_inicia_o_broker_entao_inicia_o_system_laucher_com_propriedades_corretas() throws Exception {

        embeddedQpidBroker.start();

        InOrder inOrder = Mockito.inOrder(qpidResource, systemLauncher);

        ArgumentCaptor<Map<String, Object>> argCaptor = ArgumentCaptor.forClass(Map.class);

        inOrder.verify(qpidResource).getFile();
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
    public void quando_inicia_o_broker_mas_ele_ja_esta_iniciado_entao_mata_ele_e_inicia_de_novo() throws Exception {
        quando_inicia_o_broker_entao_inicia_o_system_laucher_com_propriedades_corretas();

        embeddedQpidBrokerProperties.getAuth().setUsername("AAAAA");
        embeddedQpidBrokerProperties.getAuth().setPassword("BBBBB");
        embeddedQpidBrokerProperties.setPort(-1);

        embeddedQpidBroker.start();

        InOrder inOrder = Mockito.inOrder(qpidResource, systemLauncher);

        ArgumentCaptor<Map<String, Object>> argCaptor = ArgumentCaptor.forClass(Map.class);

        inOrder.verify(qpidResource).getFile();
        inOrder.verify(systemLauncher).startup(argCaptor.capture());

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
}