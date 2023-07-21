import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ITipoCargo } from '../tipo-cargo.model';

@Component({
  standalone: true,
  selector: 'jhi-tipo-cargo-detail',
  templateUrl: './tipo-cargo-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class TipoCargoDetailComponent {
  @Input() tipoCargo: ITipoCargo | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
