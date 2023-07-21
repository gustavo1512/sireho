import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { IHabitaciones } from '../habitaciones.model';
import { HabitacionesService } from '../service/habitaciones.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  standalone: true,
  templateUrl: './habitaciones-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HabitacionesDeleteDialogComponent {
  habitaciones?: IHabitaciones;

  constructor(protected habitacionesService: HabitacionesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.habitacionesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
