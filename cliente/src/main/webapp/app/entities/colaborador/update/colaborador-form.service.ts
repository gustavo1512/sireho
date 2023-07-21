import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IColaborador, NewColaborador } from '../colaborador.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IColaborador for edit and NewColaboradorFormGroupInput for create.
 */
type ColaboradorFormGroupInput = IColaborador | PartialWithRequiredKeyOf<NewColaborador>;

type ColaboradorFormDefaults = Pick<NewColaborador, 'id'>;

type ColaboradorFormGroupContent = {
  id: FormControl<IColaborador['id'] | NewColaborador['id']>;
  nombreColaborador: FormControl<IColaborador['nombreColaborador']>;
  cargo: FormControl<IColaborador['cargo']>;
  departamento: FormControl<IColaborador['departamento']>;
  numTelefono: FormControl<IColaborador['numTelefono']>;
  correo: FormControl<IColaborador['correo']>;
  tipoCargoColaborador: FormControl<IColaborador['tipoCargoColaborador']>;
};

export type ColaboradorFormGroup = FormGroup<ColaboradorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ColaboradorFormService {
  createColaboradorFormGroup(colaborador: ColaboradorFormGroupInput = { id: null }): ColaboradorFormGroup {
    const colaboradorRawValue = {
      ...this.getFormDefaults(),
      ...colaborador,
    };
    return new FormGroup<ColaboradorFormGroupContent>({
      id: new FormControl(
        { value: colaboradorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      nombreColaborador: new FormControl(colaboradorRawValue.nombreColaborador),
      cargo: new FormControl(colaboradorRawValue.cargo),
      departamento: new FormControl(colaboradorRawValue.departamento),
      numTelefono: new FormControl(colaboradorRawValue.numTelefono),
      correo: new FormControl(colaboradorRawValue.correo),
      tipoCargoColaborador: new FormControl(colaboradorRawValue.tipoCargoColaborador),
    });
  }

  getColaborador(form: ColaboradorFormGroup): IColaborador | NewColaborador {
    return form.getRawValue() as IColaborador | NewColaborador;
  }

  resetForm(form: ColaboradorFormGroup, colaborador: ColaboradorFormGroupInput): void {
    const colaboradorRawValue = { ...this.getFormDefaults(), ...colaborador };
    form.reset(
      {
        ...colaboradorRawValue,
        id: { value: colaboradorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ColaboradorFormDefaults {
    return {
      id: null,
    };
  }
}
