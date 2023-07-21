package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoCargoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoCargo.class);
        TipoCargo tipoCargo1 = new TipoCargo();
        tipoCargo1.setId(1L);
        TipoCargo tipoCargo2 = new TipoCargo();
        tipoCargo2.setId(tipoCargo1.getId());
        assertThat(tipoCargo1).isEqualTo(tipoCargo2);
        tipoCargo2.setId(2L);
        assertThat(tipoCargo1).isNotEqualTo(tipoCargo2);
        tipoCargo1.setId(null);
        assertThat(tipoCargo1).isNotEqualTo(tipoCargo2);
    }
}
