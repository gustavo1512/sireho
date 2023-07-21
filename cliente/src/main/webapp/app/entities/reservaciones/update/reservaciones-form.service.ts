import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservaciones, NewReservaciones } from '../reservaciones.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservaciones for edit and NewReservacionesFormGroupInput for create.
 */
type ReservacionesFormGroupInput = IReservaciones | PartialWithRequiredKeyOf<NewReservaciones>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservaciones | NewReservaciones> = Omit<T, 'fechaInicio' | 'fechaFinal'> & {
  fechaInicio?: string | null;
  fechaFinal?: string | null;
};

type ReservacionesFormRawValue = FormValueOf<IReservaciones>;

type NewReservacionesFormRawValue = FormValueOf<NewReservaciones>;

type ReservacionesFormDefaults = Pick<NewReservaciones, 'id' | 'fechaInicio' | 'fechaFinal'>;

type ReservacionesFormGroupContent = {
  id: FormControl<ReservacionesFormRawValue['id'] | NewReservaciones['id']>;
  fechaInicio: FormControl<ReservacionesFormRawValue['fechaInicio']>;
  fechaFinal: FormControl<ReservacionesFormRawValue['fechaFinal']>;
  habitacionesReservaciones: FormControl<ReservacionesFormRawValue['habitacionesReservaciones']>;
  clienteReservaciones: FormControl<ReservacionesFormRawValue['clienteReservaciones']>;
  colaboradorResrvaciones: FormControl<ReservacionesFormRawValue['colaboradorResrvaciones']>;
  eventosReservaciones: FormControl<ReservacionesFormRawValue['eventosReservaciones']>;
};

export type ReservacionesFormGroup = FormGroup<ReservacionesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservacionesFormService {
  createReservacionesFormGroup(reservaciones: ReservacionesFormGroupInput = { id: null }): ReservacionesFormGroup {
    const reservacionesRawValue = this.convertReservacionesToReservacionesRawValue({
      ...this.getFormDefaults(),
      ...reservaciones,
    });
    return new FormGroup<ReservacionesFormGroupContent>({
      id: new FormControl(
        { value: reservacionesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      fechaInicio: new FormControl(reservacionesRawValue.fechaInicio),
      fechaFinal: new FormControl(reservacionesRawValue.fechaFinal),
      habitacionesReservaciones: new FormControl(reservacionesRawValue.habitacionesReservaciones),
      clienteReservaciones: new FormControl(reservacionesRawValue.clienteReservaciones),
      colaboradorResrvaciones: new FormControl(reservacionesRawValue.colaboradorResrvaciones),
      eventosReservaciones: new FormControl(reservacionesRawValue.eventosReservaciones),
    });
  }

  getReservaciones(form: ReservacionesFormGroup): IReservaciones | NewReservaciones {
    return this.convertReservacionesRawValueToReservaciones(form.getRawValue() as ReservacionesFormRawValue | NewReservacionesFormRawValue);
  }

  resetForm(form: ReservacionesFormGroup, reservaciones: ReservacionesFormGroupInput): void {
    const reservacionesRawValue = this.convertReservacionesToReservacionesRawValue({ ...this.getFormDefaults(), ...reservaciones });
    form.reset(
      {
        ...reservacionesRawValue,
        id: { value: reservacionesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ReservacionesFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaInicio: currentTime,
      fechaFinal: currentTime,
    };
  }

  private convertReservacionesRawValueToReservaciones(
    rawReservaciones: ReservacionesFormRawValue | NewReservacionesFormRawValue
  ): IReservaciones | NewReservaciones {
    return {
      ...rawReservaciones,
      fechaInicio: dayjs(rawReservaciones.fechaInicio, DATE_TIME_FORMAT),
      fechaFinal: dayjs(rawReservaciones.fechaFinal, DATE_TIME_FORMAT),
    };
  }

  private convertReservacionesToReservacionesRawValue(
    reservaciones: IReservaciones | (Partial<NewReservaciones> & ReservacionesFormDefaults)
  ): ReservacionesFormRawValue | PartialWithRequiredKeyOf<NewReservacionesFormRawValue> {
    return {
      ...reservaciones,
      fechaInicio: reservaciones.fechaInicio ? reservaciones.fechaInicio.format(DATE_TIME_FORMAT) : undefined,
      fechaFinal: reservaciones.fechaFinal ? reservaciones.fechaFinal.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
