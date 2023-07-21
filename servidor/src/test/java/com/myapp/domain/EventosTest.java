package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventosTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Eventos.class);
        Eventos eventos1 = new Eventos();
        eventos1.setId(1L);
        Eventos eventos2 = new Eventos();
        eventos2.setId(eventos1.getId());
        assertThat(eventos1).isEqualTo(eventos2);
        eventos2.setId(2L);
        assertThat(eventos1).isNotEqualTo(eventos2);
        eventos1.setId(null);
        assertThat(eventos1).isNotEqualTo(eventos2);
    }
}
