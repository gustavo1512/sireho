import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../eventos.test-samples';

import { EventosFormService } from './eventos-form.service';

describe('Eventos Form Service', () => {
  let service: EventosFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventosFormService);
  });

  describe('Service methods', () => {
    describe('createEventosFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEventosFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreEvento: expect.any(Object),
            fechaHora: expect.any(Object),
            responsable: expect.any(Object),
            capacidad: expect.any(Object),
            participantes: expect.any(Object),
          })
        );
      });

      it('passing IEventos should create a new form with FormGroup', () => {
        const formGroup = service.createEventosFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreEvento: expect.any(Object),
            fechaHora: expect.any(Object),
            responsable: expect.any(Object),
            capacidad: expect.any(Object),
            participantes: expect.any(Object),
          })
        );
      });
    });

    describe('getEventos', () => {
      it('should return NewEventos for default Eventos initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEventosFormGroup(sampleWithNewData);

        const eventos = service.getEventos(formGroup) as any;

        expect(eventos).toMatchObject(sampleWithNewData);
      });

      it('should return NewEventos for empty Eventos initial value', () => {
        const formGroup = service.createEventosFormGroup();

        const eventos = service.getEventos(formGroup) as any;

        expect(eventos).toMatchObject({});
      });

      it('should return IEventos', () => {
        const formGroup = service.createEventosFormGroup(sampleWithRequiredData);

        const eventos = service.getEventos(formGroup) as any;

        expect(eventos).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEventos should not enable id FormControl', () => {
        const formGroup = service.createEventosFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEventos should disable id FormControl', () => {
        const formGroup = service.createEventosFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
