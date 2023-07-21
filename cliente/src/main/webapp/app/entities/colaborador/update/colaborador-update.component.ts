import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ColaboradorFormService, ColaboradorFormGroup } from './colaborador-form.service';
import { IColaborador } from '../colaborador.model';
import { ColaboradorService } from '../service/colaborador.service';
import { ITipoCargo } from 'app/entities/tipo-cargo/tipo-cargo.model';
import { TipoCargoService } from 'app/entities/tipo-cargo/service/tipo-cargo.service';

@Component({
  standalone: true,
  selector: 'jhi-colaborador-update',
  templateUrl: './colaborador-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ColaboradorUpdateComponent implements OnInit {
  isSaving = false;
  colaborador: IColaborador | null = null;

  tipoCargosSharedCollection: ITipoCargo[] = [];

  editForm: ColaboradorFormGroup = this.colaboradorFormService.createColaboradorFormGroup();

  constructor(
    protected colaboradorService: ColaboradorService,
    protected colaboradorFormService: ColaboradorFormService,
    protected tipoCargoService: TipoCargoService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTipoCargo = (o1: ITipoCargo | null, o2: ITipoCargo | null): boolean => this.tipoCargoService.compareTipoCargo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ colaborador }) => {
      this.colaborador = colaborador;
      if (colaborador) {
        this.updateForm(colaborador);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const colaborador = this.colaboradorFormService.getColaborador(this.editForm);
    if (colaborador.id !== null) {
      this.subscribeToSaveResponse(this.colaboradorService.update(colaborador));
    } else {
      this.subscribeToSaveResponse(this.colaboradorService.create(colaborador));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IColaborador>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(colaborador: IColaborador): void {
    this.colaborador = colaborador;
    this.colaboradorFormService.resetForm(this.editForm, colaborador);

    this.tipoCargosSharedCollection = this.tipoCargoService.addTipoCargoToCollectionIfMissing<ITipoCargo>(
      this.tipoCargosSharedCollection,
      colaborador.tipoCargoColaborador
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tipoCargoService
      .query()
      .pipe(map((res: HttpResponse<ITipoCargo[]>) => res.body ?? []))
      .pipe(
        map((tipoCargos: ITipoCargo[]) =>
          this.tipoCargoService.addTipoCargoToCollectionIfMissing<ITipoCargo>(tipoCargos, this.colaborador?.tipoCargoColaborador)
        )
      )
      .subscribe((tipoCargos: ITipoCargo[]) => (this.tipoCargosSharedCollection = tipoCargos));
  }
}
