import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { HabitacionesDetailComponent } from './habitaciones-detail.component';

describe('Habitaciones Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HabitacionesDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: HabitacionesDetailComponent,
              resolve: { habitaciones: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(HabitacionesDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load habitaciones on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HabitacionesDetailComponent);

      // THEN
      expect(instance.habitaciones).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
