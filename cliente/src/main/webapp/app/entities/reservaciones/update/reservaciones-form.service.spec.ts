import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../reservaciones.test-samples';

import { ReservacionesFormService } from './reservaciones-form.service';

describe('Reservaciones Form Service', () => {
  let service: ReservacionesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReservacionesFormService);
  });

  describe('Service methods', () => {
    describe('createReservacionesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReservacionesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFinal: expect.any(Object),
            habitacionesReservaciones: expect.any(Object),
            clienteReservaciones: expect.any(Object),
            colaboradorResrvaciones: expect.any(Object),
            eventosReservaciones: expect.any(Object),
          })
        );
      });

      it('passing IReservaciones should create a new form with FormGroup', () => {
        const formGroup = service.createReservacionesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFinal: expect.any(Object),
            habitacionesReservaciones: expect.any(Object),
            clienteReservaciones: expect.any(Object),
            colaboradorResrvaciones: expect.any(Object),
            eventosReservaciones: expect.any(Object),
          })
        );
      });
    });

    describe('getReservaciones', () => {
      it('should return NewReservaciones for default Reservaciones initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createReservacionesFormGroup(sampleWithNewData);

        const reservaciones = service.getReservaciones(formGroup) as any;

        expect(reservaciones).toMatchObject(sampleWithNewData);
      });

      it('should return NewReservaciones for empty Reservaciones initial value', () => {
        const formGroup = service.createReservacionesFormGroup();

        const reservaciones = service.getReservaciones(formGroup) as any;

        expect(reservaciones).toMatchObject({});
      });

      it('should return IReservaciones', () => {
        const formGroup = service.createReservacionesFormGroup(sampleWithRequiredData);

        const reservaciones = service.getReservaciones(formGroup) as any;

        expect(reservaciones).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReservaciones should not enable id FormControl', () => {
        const formGroup = service.createReservacionesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReservaciones should disable id FormControl', () => {
        const formGroup = service.createReservacionesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
