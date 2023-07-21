import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { EventosFormService, EventosFormGroup } from './eventos-form.service';
import { IEventos } from '../eventos.model';
import { EventosService } from '../service/eventos.service';

@Component({
  standalone: true,
  selector: 'jhi-eventos-update',
  templateUrl: './eventos-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EventosUpdateComponent implements OnInit {
  isSaving = false;
  eventos: IEventos | null = null;

  editForm: EventosFormGroup = this.eventosFormService.createEventosFormGroup();

  constructor(
    protected eventosService: EventosService,
    protected eventosFormService: EventosFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eventos }) => {
      this.eventos = eventos;
      if (eventos) {
        this.updateForm(eventos);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const eventos = this.eventosFormService.getEventos(this.editForm);
    if (eventos.id !== null) {
      this.subscribeToSaveResponse(this.eventosService.update(eventos));
    } else {
      this.subscribeToSaveResponse(this.eventosService.create(eventos));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEventos>>): void {
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

  protected updateForm(eventos: IEventos): void {
    this.eventos = eventos;
    this.eventosFormService.resetForm(this.editForm, eventos);
  }
}
