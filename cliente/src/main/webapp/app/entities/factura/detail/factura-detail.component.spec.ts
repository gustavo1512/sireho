import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FacturaDetailComponent } from './factura-detail.component';

describe('Factura Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacturaDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: FacturaDetailComponent,
              resolve: { factura: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(FacturaDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load factura on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FacturaDetailComponent);

      // THEN
      expect(instance.factura).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
