import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ColaboradorDetailComponent } from './colaborador-detail.component';

describe('Colaborador Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColaboradorDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ColaboradorDetailComponent,
              resolve: { colaborador: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(ColaboradorDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load colaborador on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ColaboradorDetailComponent);

      // THEN
      expect(instance.colaborador).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
