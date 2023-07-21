import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../habitaciones.test-samples';

import { HabitacionesFormService } from './habitaciones-form.service';

describe('Habitaciones Form Service', () => {
  let service: HabitacionesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HabitacionesFormService);
  });

  describe('Service methods', () => {
    describe('createHabitacionesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHabitacionesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            piso: expect.any(Object),
            disponible: expect.any(Object),
            facturaHabitaciones: expect.any(Object),
          })
        );
      });

      it('passing IHabitaciones should create a new form with FormGroup', () => {
        const formGroup = service.createHabitacionesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            tipo: expect.any(Object),
            piso: expect.any(Object),
            disponible: expect.any(Object),
            facturaHabitaciones: expect.any(Object),
          })
        );
      });
    });

    describe('getHabitaciones', () => {
      it('should return NewHabitaciones for default Habitaciones initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createHabitacionesFormGroup(sampleWithNewData);

        const habitaciones = service.getHabitaciones(formGroup) as any;

        expect(habitaciones).toMatchObject(sampleWithNewData);
      });

      it('should return NewHabitaciones for empty Habitaciones initial value', () => {
        const formGroup = service.createHabitacionesFormGroup();

        const habitaciones = service.getHabitaciones(formGroup) as any;

        expect(habitaciones).toMatchObject({});
      });

      it('should return IHabitaciones', () => {
        const formGroup = service.createHabitacionesFormGroup(sampleWithRequiredData);

        const habitaciones = service.getHabitaciones(formGroup) as any;

        expect(habitaciones).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHabitaciones should not enable id FormControl', () => {
        const formGroup = service.createHabitacionesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHabitaciones should disable id FormControl', () => {
        const formGroup = service.createHabitacionesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
