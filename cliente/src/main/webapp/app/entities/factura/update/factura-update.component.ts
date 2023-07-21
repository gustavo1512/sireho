import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { FacturaFormService, FacturaFormGroup } from './factura-form.service';
import { IFactura } from '../factura.model';
import { FacturaService } from '../service/factura.service';

@Component({
  standalone: true,
  selector: 'jhi-factura-update',
  templateUrl: './factura-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FacturaUpdateComponent implements OnInit {
  isSaving = false;
  factura: IFactura | null = null;

  editForm: FacturaFormGroup = this.facturaFormService.createFacturaFormGroup();

  constructor(
    protected facturaService: FacturaService,
    protected facturaFormService: FacturaFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ factura }) => {
      this.factura = factura;
      if (factura) {
        this.updateForm(factura);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const factura = this.facturaFormService.getFactura(this.editForm);
    if (factura.id !== null) {
      this.subscribeToSaveResponse(this.facturaService.update(factura));
    } else {
      this.subscribeToSaveResponse(this.facturaService.create(factura));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFactura>>): void {
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

  protected updateForm(factura: IFactura): void {
    this.factura = factura;
    this.facturaFormService.resetForm(this.editForm, factura);
  }
}
