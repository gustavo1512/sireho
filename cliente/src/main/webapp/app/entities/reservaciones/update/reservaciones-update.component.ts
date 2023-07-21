import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ReservacionesFormService, ReservacionesFormGroup } from './reservaciones-form.service';
import { IReservaciones } from '../reservaciones.model';
import { ReservacionesService } from '../service/reservaciones.service';
import { IHabitaciones } from 'app/entities/habitaciones/habitaciones.model';
import { HabitacionesService } from 'app/entities/habitaciones/service/habitaciones.service';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { ColaboradorService } from 'app/entities/colaborador/service/colaborador.service';
import { IEventos } from 'app/entities/eventos/eventos.model';
import { EventosService } from 'app/entities/eventos/service/eventos.service';

@Component({
  standalone: true,
  selector: 'jhi-reservaciones-update',
  templateUrl: './reservaciones-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ReservacionesUpdateComponent implements OnInit {
  isSaving = false;
  reservaciones: IReservaciones | null = null;

  habitacionesReservacionesCollection: IHabitaciones[] = [];
  clientesSharedCollection: ICliente[] = [];
  colaboradorsSharedCollection: IColaborador[] = [];
  eventosSharedCollection: IEventos[] = [];

  editForm: ReservacionesFormGroup = this.reservacionesFormService.createReservacionesFormGroup();

  constructor(
    protected reservacionesService: ReservacionesService,
    protected reservacionesFormService: ReservacionesFormService,
    protected habitacionesService: HabitacionesService,
    protected clienteService: ClienteService,
    protected colaboradorService: ColaboradorService,
    protected eventosService: EventosService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareHabitaciones = (o1: IHabitaciones | null, o2: IHabitaciones | null): boolean =>
    this.habitacionesService.compareHabitaciones(o1, o2);

  compareCliente = (o1: ICliente | null, o2: ICliente | null): boolean => this.clienteService.compareCliente(o1, o2);

  compareColaborador = (o1: IColaborador | null, o2: IColaborador | null): boolean => this.colaboradorService.compareColaborador(o1, o2);

  compareEventos = (o1: IEventos | null, o2: IEventos | null): boolean => this.eventosService.compareEventos(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ reservaciones }) => {
      this.reservaciones = reservaciones;
      if (reservaciones) {
        this.updateForm(reservaciones);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const reservaciones = this.reservacionesFormService.getReservaciones(this.editForm);
    if (reservaciones.id !== null) {
      this.subscribeToSaveResponse(this.reservacionesService.update(reservaciones));
    } else {
      this.subscribeToSaveResponse(this.reservacionesService.create(reservaciones));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReservaciones>>): void {
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

  protected updateForm(reservaciones: IReservaciones): void {
    this.reservaciones = reservaciones;
    this.reservacionesFormService.resetForm(this.editForm, reservaciones);

    this.habitacionesReservacionesCollection = this.habitacionesService.addHabitacionesToCollectionIfMissing<IHabitaciones>(
      this.habitacionesReservacionesCollection,
      reservaciones.habitacionesReservaciones
    );
    this.clientesSharedCollection = this.clienteService.addClienteToCollectionIfMissing<ICliente>(
      this.clientesSharedCollection,
      reservaciones.clienteReservaciones
    );
    this.colaboradorsSharedCollection = this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
      this.colaboradorsSharedCollection,
      reservaciones.colaboradorResrvaciones
    );
    this.eventosSharedCollection = this.eventosService.addEventosToCollectionIfMissing<IEventos>(
      this.eventosSharedCollection,
      reservaciones.eventosReservaciones
    );
  }

  protected loadRelationshipsOptions(): void {
    this.habitacionesService
      .query({ filter: 'reservaciones-is-null' })
      .pipe(map((res: HttpResponse<IHabitaciones[]>) => res.body ?? []))
      .pipe(
        map((habitaciones: IHabitaciones[]) =>
          this.habitacionesService.addHabitacionesToCollectionIfMissing<IHabitaciones>(
            habitaciones,
            this.reservaciones?.habitacionesReservaciones
          )
        )
      )
      .subscribe((habitaciones: IHabitaciones[]) => (this.habitacionesReservacionesCollection = habitaciones));

    this.clienteService
      .query()
      .pipe(map((res: HttpResponse<ICliente[]>) => res.body ?? []))
      .pipe(
        map((clientes: ICliente[]) =>
          this.clienteService.addClienteToCollectionIfMissing<ICliente>(clientes, this.reservaciones?.clienteReservaciones)
        )
      )
      .subscribe((clientes: ICliente[]) => (this.clientesSharedCollection = clientes));

    this.colaboradorService
      .query()
      .pipe(map((res: HttpResponse<IColaborador[]>) => res.body ?? []))
      .pipe(
        map((colaboradors: IColaborador[]) =>
          this.colaboradorService.addColaboradorToCollectionIfMissing<IColaborador>(
            colaboradors,
            this.reservaciones?.colaboradorResrvaciones
          )
        )
      )
      .subscribe((colaboradors: IColaborador[]) => (this.colaboradorsSharedCollection = colaboradors));

    this.eventosService
      .query()
      .pipe(map((res: HttpResponse<IEventos[]>) => res.body ?? []))
      .pipe(
        map((eventos: IEventos[]) =>
          this.eventosService.addEventosToCollectionIfMissing<IEventos>(eventos, this.reservaciones?.eventosReservaciones)
        )
      )
      .subscribe((eventos: IEventos[]) => (this.eventosSharedCollection = eventos));
  }
}
