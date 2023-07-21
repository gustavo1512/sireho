package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReservacionesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reservaciones.class);
        Reservaciones reservaciones1 = new Reservaciones();
        reservaciones1.setId(1L);
        Reservaciones reservaciones2 = new Reservaciones();
        reservaciones2.setId(reservaciones1.getId());
        assertThat(reservaciones1).isEqualTo(reservaciones2);
        reservaciones2.setId(2L);
        assertThat(reservaciones1).isNotEqualTo(reservaciones2);
        reservaciones1.setId(null);
        assertThat(reservaciones1).isNotEqualTo(reservaciones2);
    }
}
