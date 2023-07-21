import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EventosDetailComponent } from './eventos-detail.component';

describe('Eventos Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventosDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: EventosDetailComponent,
              resolve: { eventos: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(EventosDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load eventos on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EventosDetailComponent);

      // THEN
      expect(instance.eventos).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
