import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservacionesDetailComponent } from './reservaciones-detail.component';

describe('Reservaciones Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservacionesDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ReservacionesDetailComponent,
              resolve: { reservaciones: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(ReservacionesDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load reservaciones on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReservacionesDetailComponent);

      // THEN
      expect(instance.reservaciones).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
