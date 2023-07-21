import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IHabitaciones, NewHabitaciones } from '../habitaciones.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHabitaciones for edit and NewHabitacionesFormGroupInput for create.
 */
type HabitacionesFormGroupInput = IHabitaciones | PartialWithRequiredKeyOf<NewHabitaciones>;

type HabitacionesFormDefaults = Pick<NewHabitaciones, 'id' | 'disponible'>;

type HabitacionesFormGroupContent = {
  id: FormControl<IHabitaciones['id'] | NewHabitaciones['id']>;
  tipo: FormControl<IHabitaciones['tipo']>;
  piso: FormControl<IHabitaciones['piso']>;
  disponible: FormControl<IHabitaciones['disponible']>;
};

export type HabitacionesFormGroup = FormGroup<HabitacionesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HabitacionesFormService {
  createHabitacionesFormGroup(habitaciones: HabitacionesFormGroupInput = { id: null }): HabitacionesFormGroup {
    const habitacionesRawValue = {
      ...this.getFormDefaults(),
      ...habitaciones,
    };
    return new FormGroup<HabitacionesFormGroupContent>({
      id: new FormControl(
        { value: habitacionesRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      tipo: new FormControl(habitacionesRawValue.tipo),
      piso: new FormControl(habitacionesRawValue.piso),
      disponible: new FormControl(habitacionesRawValue.disponible),
    });
  }

  getHabitaciones(form: HabitacionesFormGroup): IHabitaciones | NewHabitaciones {
    return form.getRawValue() as IHabitaciones | NewHabitaciones;
  }

  resetForm(form: HabitacionesFormGroup, habitaciones: HabitacionesFormGroupInput): void {
    const habitacionesRawValue = { ...this.getFormDefaults(), ...habitaciones };
    form.reset(
      {
        ...habitacionesRawValue,
        id: { value: habitacionesRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): HabitacionesFormDefaults {
    return {
      id: null,
      disponible: false,
    };
  }
}
