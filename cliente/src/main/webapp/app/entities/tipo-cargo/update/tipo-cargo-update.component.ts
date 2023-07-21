import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TipoCargoFormService, TipoCargoFormGroup } from './tipo-cargo-form.service';
import { ITipoCargo } from '../tipo-cargo.model';
import { TipoCargoService } from '../service/tipo-cargo.service';

@Component({
  standalone: true,
  selector: 'jhi-tipo-cargo-update',
  templateUrl: './tipo-cargo-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TipoCargoUpdateComponent implements OnInit {
  isSaving = false;
  tipoCargo: ITipoCargo | null = null;

  editForm: TipoCargoFormGroup = this.tipoCargoFormService.createTipoCargoFormGroup();

  constructor(
    protected tipoCargoService: TipoCargoService,
    protected tipoCargoFormService: TipoCargoFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tipoCargo }) => {
      this.tipoCargo = tipoCargo;
      if (tipoCargo) {
        this.updateForm(tipoCargo);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tipoCargo = this.tipoCargoFormService.getTipoCargo(this.editForm);
    if (tipoCargo.id !== null) {
      this.subscribeToSaveResponse(this.tipoCargoService.update(tipoCargo));
    } else {
      this.subscribeToSaveResponse(this.tipoCargoService.create(tipoCargo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITipoCargo>>): void {
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

  protected updateForm(tipoCargo: ITipoCargo): void {
    this.tipoCargo = tipoCargo;
    this.tipoCargoFormService.resetForm(this.editForm, tipoCargo);
  }
}
