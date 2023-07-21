import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tipo-cargo.test-samples';

import { TipoCargoFormService } from './tipo-cargo-form.service';

describe('TipoCargo Form Service', () => {
  let service: TipoCargoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TipoCargoFormService);
  });

  describe('Service methods', () => {
    describe('createTipoCargoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTipoCargoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreCargo: expect.any(Object),
          })
        );
      });

      it('passing ITipoCargo should create a new form with FormGroup', () => {
        const formGroup = service.createTipoCargoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombreCargo: expect.any(Object),
          })
        );
      });
    });

    describe('getTipoCargo', () => {
      it('should return NewTipoCargo for default TipoCargo initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTipoCargoFormGroup(sampleWithNewData);

        const tipoCargo = service.getTipoCargo(formGroup) as any;

        expect(tipoCargo).toMatchObject(sampleWithNewData);
      });

      it('should return NewTipoCargo for empty TipoCargo initial value', () => {
        const formGroup = service.createTipoCargoFormGroup();

        const tipoCargo = service.getTipoCargo(formGroup) as any;

        expect(tipoCargo).toMatchObject({});
      });

      it('should return ITipoCargo', () => {
        const formGroup = service.createTipoCargoFormGroup(sampleWithRequiredData);

        const tipoCargo = service.getTipoCargo(formGroup) as any;

        expect(tipoCargo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITipoCargo should not enable id FormControl', () => {
        const formGroup = service.createTipoCargoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTipoCargo should disable id FormControl', () => {
        const formGroup = service.createTipoCargoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
