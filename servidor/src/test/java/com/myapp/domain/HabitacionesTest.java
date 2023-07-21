package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HabitacionesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Habitaciones.class);
        Habitaciones habitaciones1 = new Habitaciones();
        habitaciones1.setId(1L);
        Habitaciones habitaciones2 = new Habitaciones();
        habitaciones2.setId(habitaciones1.getId());
        assertThat(habitaciones1).isEqualTo(habitaciones2);
        habitaciones2.setId(2L);
        assertThat(habitaciones1).isNotEqualTo(habitaciones2);
        habitaciones1.setId(null);
        assertThat(habitaciones1).isNotEqualTo(habitaciones2);
    }
}
