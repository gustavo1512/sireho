import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFactura, NewFactura } from '../factura.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFactura for edit and NewFacturaFormGroupInput for create.
 */
type FacturaFormGroupInput = IFactura | PartialWithRequiredKeyOf<NewFactura>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IFactura | NewFactura> = Omit<T, 'fechaPago'> & {
  fechaPago?: string | null;
};

type FacturaFormRawValue = FormValueOf<IFactura>;

type NewFacturaFormRawValue = FormValueOf<NewFactura>;

type FacturaFormDefaults = Pick<NewFactura, 'id' | 'fechaPago'>;

type FacturaFormGroupContent = {
  id: FormControl<FacturaFormRawValue['id'] | NewFactura['id']>;
  cantidadPaga: FormControl<FacturaFormRawValue['cantidadPaga']>;
  fechaPago: FormControl<FacturaFormRawValue['fechaPago']>;
  metodoPgo: FormControl<FacturaFormRawValue['metodoPgo']>;
};

export type FacturaFormGroup = FormGroup<FacturaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FacturaFormService {
  createFacturaFormGroup(factura: FacturaFormGroupInput = { id: null }): FacturaFormGroup {
    const facturaRawValue = this.convertFacturaToFacturaRawValue({
      ...this.getFormDefaults(),
      ...factura,
    });
    return new FormGroup<FacturaFormGroupContent>({
      id: new FormControl(
        { value: facturaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cantidadPaga: new FormControl(facturaRawValue.cantidadPaga),
      fechaPago: new FormControl(facturaRawValue.fechaPago),
      metodoPgo: new FormControl(facturaRawValue.metodoPgo),
    });
  }

  getFactura(form: FacturaFormGroup): IFactura | NewFactura {
    return this.convertFacturaRawValueToFactura(form.getRawValue() as FacturaFormRawValue | NewFacturaFormRawValue);
  }

  resetForm(form: FacturaFormGroup, factura: FacturaFormGroupInput): void {
    const facturaRawValue = this.convertFacturaToFacturaRawValue({ ...this.getFormDefaults(), ...factura });
    form.reset(
      {
        ...facturaRawValue,
        id: { value: facturaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FacturaFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaPago: currentTime,
    };
  }

  private convertFacturaRawValueToFactura(rawFactura: FacturaFormRawValue | NewFacturaFormRawValue): IFactura | NewFactura {
    return {
      ...rawFactura,
      fechaPago: dayjs(rawFactura.fechaPago, DATE_TIME_FORMAT),
    };
  }

  private convertFacturaToFacturaRawValue(
    factura: IFactura | (Partial<NewFactura> & FacturaFormDefaults)
  ): FacturaFormRawValue | PartialWithRequiredKeyOf<NewFacturaFormRawValue> {
    return {
      ...factura,
      fechaPago: factura.fechaPago ? factura.fechaPago.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
