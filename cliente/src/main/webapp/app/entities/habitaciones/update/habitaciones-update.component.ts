import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { HabitacionesFormService, HabitacionesFormGroup } from './habitaciones-form.service';
import { IHabitaciones } from '../habitaciones.model';
import { HabitacionesService } from '../service/habitaciones.service';
import { IFactura } from 'app/entities/factura/factura.model';
import { FacturaService } from 'app/entities/factura/service/factura.service';

@Component({
  standalone: true,
  selector: 'jhi-habitaciones-update',
  templateUrl: './habitaciones-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HabitacionesUpdateComponent implements OnInit {
  isSaving = false;
  habitaciones: IHabitaciones | null = null;

  facturasSharedCollection: IFactura[] = [];

  editForm: HabitacionesFormGroup = this.habitacionesFormService.createHabitacionesFormGroup();

  constructor(
    protected habitacionesService: HabitacionesService,
    protected habitacionesFormService: HabitacionesFormService,
    protected facturaService: FacturaService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareFactura = (o1: IFactura | null, o2: IFactura | null): boolean => this.facturaService.compareFactura(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ habitaciones }) => {
      this.habitaciones = habitaciones;
      if (habitaciones) {
        this.updateForm(habitaciones);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const habitaciones = this.habitacionesFormService.getHabitaciones(this.editForm);
    if (habitaciones.id !== null) {
      this.subscribeToSaveResponse(this.habitacionesService.update(habitaciones));
    } else {
      this.subscribeToSaveResponse(this.habitacionesService.create(habitaciones));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHabitaciones>>): void {
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

  protected updateForm(habitaciones: IHabitaciones): void {
    this.habitaciones = habitaciones;
    this.habitacionesFormService.resetForm(this.editForm, habitaciones);

    this.facturasSharedCollection = this.facturaService.addFacturaToCollectionIfMissing<IFactura>(
      this.facturasSharedCollection,
      habitaciones.facturaHabitaciones
    );
  }

  protected loadRelationshipsOptions(): void {
    this.facturaService
      .query()
      .pipe(map((res: HttpResponse<IFactura[]>) => res.body ?? []))
      .pipe(
        map((facturas: IFactura[]) =>
          this.facturaService.addFacturaToCollectionIfMissing<IFactura>(facturas, this.habitaciones?.facturaHabitaciones)
        )
      )
      .subscribe((facturas: IFactura[]) => (this.facturasSharedCollection = facturas));
  }
}
