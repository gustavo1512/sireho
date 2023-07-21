import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITipoCargo, NewTipoCargo } from '../tipo-cargo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITipoCargo for edit and NewTipoCargoFormGroupInput for create.
 */
type TipoCargoFormGroupInput = ITipoCargo | PartialWithRequiredKeyOf<NewTipoCargo>;

type TipoCargoFormDefaults = Pick<NewTipoCargo, 'id'>;

type TipoCargoFormGroupContent = {
  id: FormControl<ITipoCargo['id'] | NewTipoCargo['id']>;
  nombreCargo: FormControl<ITipoCargo['nombreCargo']>;
};

export type TipoCargoFormGroup = FormGroup<TipoCargoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TipoCargoFormService {
  createTipoCargoFormGroup(tipoCargo: TipoCargoFormGroupInput = { id: null }): TipoCargoFormGroup {
    const tipoCargoRawValue = {
      ...this.getFormDefaults(),
      ...tipoCargo,
    };
    return new FormGroup<TipoCargoFormGroupContent>({
      id: new FormControl(
        { value: tipoCargoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombreCargo: new FormControl(tipoCargoRawValue.nombreCargo),
    });
  }

  getTipoCargo(form: TipoCargoFormGroup): ITipoCargo | NewTipoCargo {
    return form.getRawValue() as ITipoCargo | NewTipoCargo;
  }

  resetForm(form: TipoCargoFormGroup, tipoCargo: TipoCargoFormGroupInput): void {
    const tipoCargoRawValue = { ...this.getFormDefaults(), ...tipoCargo };
    form.reset(
      {
        ...tipoCargoRawValue,
        id: { value: tipoCargoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TipoCargoFormDefaults {
    return {
      id: null,
    };
  }
}
