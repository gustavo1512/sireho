import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEventos, NewEventos } from '../eventos.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEventos for edit and NewEventosFormGroupInput for create.
 */
type EventosFormGroupInput = IEventos | PartialWithRequiredKeyOf<NewEventos>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEventos | NewEventos> = Omit<T, 'fechaHora'> & {
  fechaHora?: string | null;
};

type EventosFormRawValue = FormValueOf<IEventos>;

type NewEventosFormRawValue = FormValueOf<NewEventos>;

type EventosFormDefaults = Pick<NewEventos, 'id' | 'fechaHora'>;

type EventosFormGroupContent = {
  id: FormControl<EventosFormRawValue['id'] | NewEventos['id']>;
  nombreEvento: FormControl<EventosFormRawValue['nombreEvento']>;
  fechaHora: FormControl<EventosFormRawValue['fechaHora']>;
  responsable: FormControl<EventosFormRawValue['responsable']>;
  capacidad: FormControl<EventosFormRawValue['capacidad']>;
  participantes: FormControl<EventosFormRawValue['participantes']>;
};

export type EventosFormGroup = FormGroup<EventosFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventosFormService {
  createEventosFormGroup(eventos: EventosFormGroupInput = { id: null }): EventosFormGroup {
    const eventosRawValue = this.convertEventosToEventosRawValue({
      ...this.getFormDefaults(),
      ...eventos,
    });
    return new FormGroup<EventosFormGroupContent>({
      id: new FormControl(
        { value: eventosRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombreEvento: new FormControl(eventosRawValue.nombreEvento),
      fechaHora: new FormControl(eventosRawValue.fechaHora),
      responsable: new FormControl(eventosRawValue.responsable),
      capacidad: new FormControl(eventosRawValue.capacidad),
      participantes: new FormControl(eventosRawValue.participantes),
    });
  }

  getEventos(form: EventosFormGroup): IEventos | NewEventos {
    return this.convertEventosRawValueToEventos(form.getRawValue() as EventosFormRawValue | NewEventosFormRawValue);
  }

  resetForm(form: EventosFormGroup, eventos: EventosFormGroupInput): void {
    const eventosRawValue = this.convertEventosToEventosRawValue({ ...this.getFormDefaults(), ...eventos });
    form.reset(
      {
        ...eventosRawValue,
        id: { value: eventosRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EventosFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaHora: currentTime,
    };
  }

  private convertEventosRawValueToEventos(rawEventos: EventosFormRawValue | NewEventosFormRawValue): IEventos | NewEventos {
    return {
      ...rawEventos,
      fechaHora: dayjs(rawEventos.fechaHora, DATE_TIME_FORMAT),
    };
  }

  private convertEventosToEventosRawValue(
    eventos: IEventos | (Partial<NewEventos> & EventosFormDefaults)
  ): EventosFormRawValue | PartialWithRequiredKeyOf<NewEventosFormRawValue> {
    return {
      ...eventos,
      fechaHora: eventos.fechaHora ? eventos.fechaHora.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
