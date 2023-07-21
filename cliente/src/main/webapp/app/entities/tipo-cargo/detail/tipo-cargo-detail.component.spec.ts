import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TipoCargoDetailComponent } from './tipo-cargo-detail.component';

describe('TipoCargo Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TipoCargoDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TipoCargoDetailComponent,
              resolve: { tipoCargo: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(TipoCargoDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load tipoCargo on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TipoCargoDetailComponent);

      // THEN
      expect(instance.tipoCargo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
